package com.example.alamr_iot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

//내가 저장한 sp에서 값 저장 또는 불러오는 함수
public class PreferenceHelper {

    //어디다 쓰이는거지? -> 키 값으로
    private final String IsOn = "IsOn";
    private final String ALARMDATA = "alarm_data";
    Gson gson;
    private SharedPreferences app_prefs;
    private Context context;
    private ArrayList<DTO_alarm> fromSP_alarmData = new ArrayList<>();

    // 이 함수는 어떤 용도로 쓰이는 거지?
    // context는 현재 파일에 대한 정보? 같은 느낌
    public PreferenceHelper(Context context) {
        //생성자임 sp 객체 생성할 때 마다 꼭 인자값 전달해줘야 함
        //context 안 써주면 오류 난다 왜 써줘야 하는거지?
//        이 클래스안에 있는 함수를 사용하기 위해서 액티비티
        app_prefs = context.getSharedPreferences("alarm_data", 0);
        // 파일이름 shared 모드 0-> 읽기쓰기 가능
        //MODE_PRIVSTE -> 이 앱에서만 사용가능
        gson = new GsonBuilder().create();
        this.context = context;

    }

    public void changeAlarmData(ArrayList alarmDataList) {
        SharedPreferences.Editor edit = app_prefs.edit();
        String stirngAlarmData = gson.toJson(alarmDataList);
        // Editor을  app_prefs 에 쓰기 위해서 연결해준다.
        // shared 라는 파일엘 edit 해주겠다.
        edit.putString(ALARMDATA, stirngAlarmData);
        edit.apply();
    }


    public ArrayList getAlarmList() {
        Map<String, ?> keys = app_prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {//key 값을 몰라도 전체 SP,
            Log.d("map values", entry.getKey() + " : " + entry.getValue().toString());
            String alarmData = app_prefs.getString(entry.getKey(), "");
            fromSP_alarmData = new ArrayList<>();
            fromSP_alarmData = gson.fromJson(alarmData, new TypeToken<ArrayList<DTO_alarm>>() {
            }.getType());
        }
        return fromSP_alarmData;
    }
}
