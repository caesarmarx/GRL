package grl.com.subViews.dialogues.league;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/25/2016.
 */
public class TimeSettingDialog extends AlertDialog.Builder{

    // view
    TextView tvStartTime;
    TextView tvEndTime;

    // value
    Context context;
    String startTime;
    String endTime;
    View dialoglayout;
    static final int startTimeKey = 1;
    static final int endTimeKey = 2;

    public TimeSettingDialog(Activity context) {
        super(context);

        this.context = context;
        LayoutInflater inflater = context.getLayoutInflater();
        this.dialoglayout = inflater.inflate(R.layout.dialog_time_setting, null);
        this.setView(dialoglayout);

        this.getViewByID();
        this.initializeData();
        this.setOnListener();
    }


    public void setData(String fromTime , String toTime) {
        this.startTime = fromTime;
        this.endTime = toTime;
        initializeData();
    }

    public String getStartTime () {
        return tvStartTime.getText().toString();
    }

    public String getEndTime () {
        return tvEndTime.getText().toString();
    }
    public void getViewByID () {
        tvStartTime = (TextView) dialoglayout.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) dialoglayout.findViewById(R.id.tvEndTime);
    }

    public void initializeData () {
        // init views
        tvStartTime.setText(startTime);
        tvEndTime.setText(endTime);
    }

    public void setOnListener () {
        this.tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(endTimeKey);
            }
        });
        this.tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(startTimeKey);
            }
        });
    }

    public void showTimePicker (final int flag) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        String strTime = hourOfDay + ":" + minute;
                        switch (flag) {
                            case startTimeKey:
                                tvStartTime.setText(strTime);
                                break;
                            case endTimeKey:
                                tvEndTime.setText(strTime);
                                break;
                        }
                    }
                }, mHour, mMinute, false);
        tpd.show();
    }
}
