package com.example.alamr_iot;

import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterAlarm extends RecyclerView.Adapter<AdapterAlarm.AlarmviewHolder> {

    List<DTO_alarm> alarmItem = new ArrayList<>();
    public AdapterAlarm(List<DTO_alarm> alarmItem) {
        this.alarmItem = alarmItem;
        //  this.context = context;
    }

    @NonNull
    @Override
    public AlarmviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("1oncreate", "oncreate");
        View my_item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        //내가 만든 아이템 레이아웃을 클래스로 변환시켜주는 과정이다.
        //inflate 는 view를 실체 view 객체로 만들어준다.
        AlarmviewHolder holder = new AlarmviewHolder(my_item);
        //변환된 클래스는 내가 정의한 holder 클래스에 객체를 생성해 넘겨준다.
        //뷰홀더를 새로 생성해준다.
        Log.e("2oncreate", "oncreate");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmviewHolder holder, int position) {

        //첫번째 매개 변수는 onCreateviewHolder 의 반환값 holder을 받음
        // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
        Log.e("bind", "bind");

        DTO_alarm alarmItemList = alarmItem.get(position);
        //전체 데이터 값이 들어 있는 리스트에서 첫번째 요소의 값을 넣어준다.
        Log.e("뷰홀더 alarmItem", String.valueOf(alarmItem));
        Log.e("alarmItemList 뷰홀더", String.valueOf(alarmItemList));

        holder.time.setText(alarmItemList.getTime());
        Log.e("뷰홀더", alarmItemList.getTime());

        holder.alarm_title.setText(alarmItemList.getAlarmTitle());
        Log.e("뷰홀더", alarmItemList.getAlarmTitle());
        //뷰홀더에 데이터를 세팅해주는 과정

        if(alarmItemList.getOn() ==Boolean.TRUE){
            holder.IsOn.setText("ON");
        }
        else{
            holder.IsOn.setText("OFF");

        }
    }

    @Override
    //꼭 넣아주기 안 넣어줘서 오류 났었음
    public int getItemCount() {
        return alarmItem.size();
    }
    // 이 메서드를 통해 데이터 세트의 크기를 가져올 수 있다.
    // 리사이클러뷰는 이 메서드를 사용해서 더 이상 표시할 수 있는 항목이 없을 때를 결정한다.
    //size와 length는 다름 size는 동적 length 정적

    interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onDeleteClick(View v, int position);
        void IsOnButtonClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class AlarmviewHolder extends RecyclerView.ViewHolder {
        protected TextView time,alarm_title;
        protected ToggleButton IsOn;
        public AlarmviewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            alarm_title = itemView.findViewById(R.id.alarm_title);
            IsOn = itemView.findViewById(R.id.IsOn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
//                   getAdapterPosition()은 뷰홀더가 나타내는 항목의 어댑터 위치를 반환한다.
//                    단 아이템이 여전히 어댑터에 존재하는 경우에 어댑터의 위치이다.
//                    아이템이 어앱터에서 제거된경우  NO_position 반환
//                    RecyclerView.Adapter.notifyDataSetChanged()가 호출되었거
                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });

            IsOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.IsOnButtonClick(v, position);
                        }

                    }

                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position =getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("삭제하기").setPositiveButton("삭제하기",
                                new DialogInterface.OnClickListener(){
                                    public  void onClick(DialogInterface dialogInterface,int i){
                                        if (mListener != null) {
                                            mListener.onDeleteClick(view, position);
                                        }
                                    }
                                })
                                .setNeutralButton("취소",null).show();

                    }
                    return true;

                }
            });//롱클릭 리스너함수
        }
    }
}
