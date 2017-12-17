package com.macormap.lasveglia;

import android.app.Application;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by carlo on 10/09/2017.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

    private List<AlarmObj> alarmList;


    public MyAdapterListener onClickListener;

    public interface MyAdapterListener {

        void btnEditAlarmViewOnClick(View v, int position);
        void btnOnOffViewOnClick(View v, int position);
        void ErasedAlarm(View v, int position);
    }



    public AlarmAdapter(List<AlarmObj> alarmList) {
        this.alarmList = alarmList;
    }

    public AlarmAdapter(List<AlarmObj> alarmList, MyAdapterListener listener) {
        this.alarmList = alarmList;
        onClickListener = listener;
    }


    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView alarmNameView, alarmTimeView, alarmWhen, alarmtxtOnOff;
        public ImageButton alarmEditBtn, alarmOnOffBtn;
        public TextView[] DaysActiveViews;
        public boolean infaseTouch = false;
        public float downX, downY;
        public float downXStart, downYStart;

        public View HoldView;
        public float minDxEraseRow;
        public LinearLayout mainbacklayout;


        public MyViewHolder(View view) {
            super(view);
            HoldView = view;
             mainbacklayout = (LinearLayout) view.findViewById(R.id.idrowlayout);
             alarmTimeView = (TextView) view.findViewById(R.id.idTimeAlarm);
             alarmNameView = (TextView) view.findViewById(R.id.idNameAlarm);
             alarmWhen     = (TextView) view.findViewById(R.id.idWhen);
             alarmtxtOnOff = (TextView) view.findViewById(R.id.idtxtOnOff);
             alarmEditBtn  = (ImageButton) view.findViewById(R.id.idEditAlarm);
             alarmOnOffBtn = (ImageButton) view.findViewById(R.id.idOnOffAlarm);

             minDxEraseRow = 140f;
        //    originalLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

             // per intercettare i bottoni interni all'adapter
             alarmEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btnEditAlarmViewOnClick(v, getAdapterPosition());
                }
             });

             alarmOnOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.btnOnOffViewOnClick(v, getAdapterPosition());
             }
             });


            mainbacklayout.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    downX = event.getX() ;
                    downY = event.getY() ;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (!infaseTouch) {
                                downXStart = downX;
                                downYStart = downY;
                                minDxEraseRow = HoldView.getHeight();
                                infaseTouch = true;
                            }
                          break;
                        case MotionEvent.ACTION_MOVE:
                            float dx = -(float)(downX-downXStart);
                            HoldView.setScrollX((int)dx);
                            if ((Math.abs(downX-downXStart))>minDxEraseRow) {
                                HoldView.setAlpha(0.2f);
                            } else  HoldView.setAlpha(1.0f);
                            break;

                        case MotionEvent.ACTION_CANCEL:
                            if (infaseTouch) {
                                infaseTouch = false;
                                HoldView.setScrollX(0);
                                HoldView.setAlpha(1.0f);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (infaseTouch) {
                                if ((Math.abs(downX - downXStart)) > minDxEraseRow) {
                                    HoldView.setScrollX(0);
                                    HoldView.setAlpha(1.0f);
                                    onClickListener.ErasedAlarm(v, getAdapterPosition());
                                } else {
                                    infaseTouch = false;
                                    HoldView.setScrollX(0);
                                    HoldView.setAlpha(1.0f);
                                }
                            }
                            break;
                    }
                    return true;
                }
              }
            );
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        float parTrasp = 0.25f;
        float parTrasp2 = 0.35f;
        AlarmObj alarmObj =alarmList.get(position);
        holder.alarmTimeView.setText(alarmObj.getStrTime());
        holder.alarmNameView.setText(alarmObj.getName_alarm());
        holder.alarmWhen.setText(alarmObj.getWhenColored());
        holder.alarmtxtOnOff.setText(alarmObj.getStrState());
        if (alarmObj.isState_onoff()) {
            holder.alarmOnOffBtn.setAlpha(1.0f);
            holder.alarmEditBtn.setAlpha(1.0f);
            holder.alarmTimeView.setAlpha(1.0f);
            holder.alarmNameView.setAlpha(1.0f);
            holder.alarmWhen.setAlpha(1.0f);
        }
        else {
            holder.alarmOnOffBtn.setAlpha(parTrasp);
            holder.alarmEditBtn.setAlpha(parTrasp);
            holder.alarmTimeView.setAlpha(parTrasp);
            holder.alarmNameView.setAlpha(parTrasp2);
            holder.alarmWhen.setAlpha(parTrasp);
        }

        holder.infaseTouch = false; holder.HoldView.setX(0f);   holder.HoldView.setAlpha(1.0f);
    }


}
