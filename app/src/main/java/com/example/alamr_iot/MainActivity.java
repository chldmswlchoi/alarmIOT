package com.example.alamr_iot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private FloatingActionButton add;
    private TimePickerDialog timePicker;
    private String saveDate;
    private ArrayList<DTO_alarm> alarmList = new ArrayList<>();
    private PreferenceHelper preferenceHelper;
    private AdapterAlarm alarmAdapter;
    private RecyclerView group_list;
    private LinearLayoutManager linearLayoutManager;
    private boolean IsCreating = false;
    private int changePosition = 0;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private int sendWhat;
    private String sendAddDatatToServer,sendClearDatatToServer,sendChangeDatatToServer;
//    private SendSocket sendSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = findViewById(R.id.floatingbtn);
        preferenceHelper = new PreferenceHelper(this);

        group_list = findViewById(R.id.group_list);
        linearLayoutManager = new LinearLayoutManager(this);
        group_list.setLayoutManager(linearLayoutManager);
        alarmAdapter = new AdapterAlarm(alarmList);
        group_list.setAdapter(alarmAdapter);
        setAlarmData(preferenceHelper.getAlarmList());
        Calendar calendar = Calendar.getInstance();
        int pHour = calendar.get(Calendar.HOUR);
        int pMinute = calendar.get(Calendar.MINUTE);
        SocketStart socketStart = new SocketStart();
        socketStart.start();

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                DateFormat dateFormat = new SimpleDateFormat("a hh:mm");
                saveDate = dateFormat.format(calendar.getTime());
                Log.d("TimePicker", "Selected time is "+saveDate);
                DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                String  splitTime = dateFormat2.format(calendar.getTime());
                String [] splitHour = splitTime.split(":");
                Log.d("TimePicker", "splitHour "+splitHour);
                String FinalHour = splitHour[0];
                String FinalMin = splitHour[1];
                if(IsCreating) { // 알람 새로 만드는 경우
                    createAlarm(saveDate,FinalHour,FinalMin); // 리사이클러뷰에 새로 만든 알람 추가해줌
                    IsCreating= false;
                }
                else { // 알람을 수정하는 경우
                    changeAlarm(changePosition,FinalHour, FinalMin);
                }
            }
        };

        timePicker = new TimePickerDialog(this,listener,pHour,pMinute,false);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "FloatingBtn 클릭", Toast.LENGTH_SHORT).show();
                //getInstance 는 현재 날짜를 선택기의 기본값으로 사용
                IsCreating = true;
                timePicker.show();
            }
        });


        alarmAdapter.setOnItemClickListener(new AdapterAlarm.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                changePosition =position;
                String hourOfDay = alarmList.get(position).getHourOfDay();
                String minute = alarmList.get(position).getMinute();
                timePicker.updateTime(Integer.parseInt(hourOfDay), Integer.parseInt(minute));
                timePicker.show();
            }

            @Override
            public void onDeleteClick(View v, int position) {
                alarmList.remove(position);
                preferenceHelper.changeAlarmData(alarmList);
                alarmAdapter.notifyItemRemoved(position);
                sendWhat = 1;
                sendClearDatatToServer = String.valueOf(position);
                SendSocket sendSocket = new SendSocket();
                sendSocket.start();
            }

            @Override
            public void IsOnButtonClick(View v, int position) { //ONOFF 버튼 클릭시
                if(alarmList.get(position).getOn() ==Boolean.TRUE){ // On-> Off
                    alarmList.get(position).setOn(Boolean.FALSE);
                    sendWhat = 1;
                    sendClearDatatToServer = String.valueOf(position);
                    SendSocket sendSocket = new SendSocket();
                    sendSocket.start();
                }
                else{// OFF->ON
                    alarmList.get(position).setOn(Boolean.TRUE);
                }
                preferenceHelper.changeAlarmData(alarmList);
                alarmAdapter.notifyItemChanged(position);
            }
        });
    }



    public void changeAlarm(int position, String hourOfDay, String minute){
        alarmList.get(position).setTime(saveDate);
        alarmList.get(position).setHourOfDay(hourOfDay);
        alarmList.get(position).setMinute(minute);

        preferenceHelper.changeAlarmData(alarmList);
        alarmAdapter.notifyItemChanged(position);
        sendWhat = 2;

        sendChangeDatatToServer =position+"/"+hourOfDay +":"+minute;
        SendSocket sendSocket = new SendSocket();
        sendSocket.start();

    }


    public void createAlarm(String saveDate,String hourOfDay, String minute){
        DTO_alarm postAlarm = new DTO_alarm(saveDate,"",Boolean.TRUE,hourOfDay,minute);
        alarmList.add(postAlarm);
        preferenceHelper.changeAlarmData(alarmList);
        alarmAdapter.notifyItemChanged(alarmList.size());

        sendWhat = 0;
        sendAddDatatToServer =hourOfDay +":"+minute;
        SendSocket sendSocket = new SendSocket();
        sendSocket.start();


    }

    public void setAlarmData(ArrayList<DTO_alarm> alarmData){
        for(int i =0; i<alarmData.size(); i++){
            alarmList.add(alarmData.get(i));
        }

        alarmAdapter.notifyDataSetChanged();
    }



    class SocketStart extends Thread {
        public SocketStart (){}
        public void run(){
            try {
                Log.e(TAG,"스레드 실행");
//                socket = new Socket("192.168.0.12",12345); // 집
                socket = new Socket();
//                socket.connect(new InetSocketAddress("192.168.0.145",12345),10000); // 2학원
//                socket.connect(new InetSocketAddress("192.168.0.3",12345),10000); // 3학원
//                socket.connect(new InetSocketAddress("192.168.0.3",12345),10000); // 집
                socket.connect(new InetSocketAddress("192.168.0.10",12345),10000); // 3-2학원
//                socket.connect(new InetSocketAddress("192.168.0.4",12345),10000); // 2학원

                Log.d(TAG, "서버와 소켓 연결 완료");

                ArrayList<String> sendingTimeTList = new ArrayList<>();
                for(int i =0; i<alarmList.size(); i++){
                    String hour = String.valueOf(alarmList.get(i).getHourOfDay());
                    String min = String.valueOf(alarmList.get(i).getMinute());
                    String addHourMin = hour+":"+min;
                    sendingTimeTList.add(addHourMin);
                }
                Log.e(TAG,"SocketStart : sendingTimeTList"+sendingTimeTList);
                writer = new PrintWriter(socket.getOutputStream(),true);
                if(sendingTimeTList.size()!=0){

                    writer.print("first"+"/"+sendingTimeTList.toString().replaceAll("\\p{Z}", ""));
                    writer.flush();
                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,"에러");
                Log.e(TAG,e.toString());
                Log.e(TAG, String.valueOf(e.getCause()));
                Log.e(TAG, e.getMessage());


            }
        }
    }



    class SendSocket extends Thread{
        public void run(){
            try{
                if(sendWhat == 0){ //add
                    writer.print("add/"+sendAddDatatToServer); // 보내는 데이터 형태 : add(이벤트명)/00:00(시간)
                   writer.flush();
                    Log.d(TAG,"라즈베리 서버에 add 메시지 보냄");
                }

                else if (sendWhat ==1){ //clear
                    writer.print("clear/"+sendClearDatatToServer); //clear/position(배열 인덱스)
                    writer.flush();
                    Log.d(TAG,"라즈베리 서버에 clear 메시지 보냄");

                }
                else if (sendWhat ==2){ //update
                    writer.print("update/"+sendChangeDatatToServer); //update/position(배열 인덱스)/00:00/
                    writer.flush();
                    Log.d(TAG,"라즈베리 서버에 update 메시지 보냄");
                }

            }catch (Exception e){

            }
        }
    }
}