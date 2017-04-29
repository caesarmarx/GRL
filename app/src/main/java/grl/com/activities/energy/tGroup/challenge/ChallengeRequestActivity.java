package grl.com.activities.energy.tGroup.challenge;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.challenge.ChallengeRequestTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
public class ChallengeRequestActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    TextView requestView;
    EditText responseEdit;
    Spinner estNameCombo;
    EditText moneyEdit;
    TextView startTimeView;
    TextView endTimeView;
    Spinner notifyCombo;

    ArrayAdapter<String> estAdapter;
    ArrayAdapter<String> notifyAdapter;

    Date startDate;
    Date endDate;
    String notifyArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_request);
        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    private void getViewByID () {
        requestView = (TextView) findViewById(R.id.tv_challenge_request);
        responseEdit = (EditText) findViewById(R.id.et_challenge_response);
        estNameCombo = (Spinner) findViewById(R.id.sp_challenge_est);
        moneyEdit = (EditText) findViewById(R.id.et_challenge_money);
        startTimeView = (TextView) findViewById(R.id.tv_challenge_start);
        endTimeView = (TextView) findViewById(R.id.tv_challenge_end);
        notifyCombo = (Spinner) findViewById(R.id.sp_challenge_notify);
    }

    private void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.btn_challenge_request).setOnClickListener(this);

        startTimeView.setOnClickListener(this);
        endTimeView.setOnClickListener(this);

    }

    private void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_challenge_title));
    }

    private void initializeData () {
        requestView.setText(SelfInfoModel.userName);
        List<String> estNameList = new ArrayList<String>();
        for (int i = 0; i < GlobalVars.factorList.size(); i++)
            estNameList.add(GlobalVars.factorList.get(i).estValue);

        estAdapter = new ArrayAdapter<String>(this,
                R.layout.layout_spinner_item, estNameList);
        estNameCombo.setAdapter(estAdapter);

        String[] notifyList = {"没有", "本城", "金国"};
        notifyAdapter = new ArrayAdapter<String>(this,
                R.layout.layout_spinner_item, notifyList);
        notifyCombo.setAdapter(notifyAdapter);

        startDate = new Date();
        endDate = new Date();

        startTimeView.setText((startDate.getYear() + 1900) + "年" + (startDate.getMonth() + 1) + "月" + startDate.getDate() + "日");
        endTimeView.setText((endDate.getYear() + 1900) + "年" + (endDate.getMonth() + 1) + "月" + endDate.getDate() + "日");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_challenge_request:
                saveAction();
                break;
            case R.id.tv_challenge_start:
                showStartTimePicker();
                break;
            case R.id.tv_challenge_end:
                showEndTimePicker();
                break;
        }
    }

    private void showStartTimePicker() {
        int mYear = startDate.getYear() + 1900;
        int mMonth = startDate.getMonth();
        int mDay = startDate.getDate();

        DatePickerDialog dpd = new DatePickerDialog(ChallengeRequestActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        startDate.setYear(year - 1900);
                        startDate.setMonth(monthOfYear);
                        startDate.setDate(dayOfMonth);
                        startTimeView.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(new Date().getTime() - 1000);

        dpd.show();
    }

    private void showEndTimePicker() {
        int mYear = endDate.getYear() + 1900;
        int mMonth = endDate.getMonth();
        int mDay = endDate.getDate();

        DatePickerDialog dpd = new DatePickerDialog(ChallengeRequestActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        endDate.setYear(year - 1900);
                        endDate.setMonth(monthOfYear);
                        endDate.setDate(dayOfMonth);
                        endTimeView.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(new Date().getTime() - 1000);

        dpd.show();
    }

    private void saveAction() {
        if (responseEdit.getText().toString().isEmpty())
            return;
        if (moneyEdit.getText().toString().isEmpty())
            return;
        String startTimeText = GlobalVars.getDateStringFromDate(startDate, "yyyy-MM-dd");
        String endTimeText = GlobalVars.getDateStringFromDate(endDate, "yyyy-MM-dd");
        String estName = GlobalVars.factorList.get(estNameCombo.getSelectedItemPosition()).estName;
        switch (notifyCombo.getSelectedItemPosition()) {
            case 0:
                notifyArea = "";
                break;
            case 1:
                notifyArea = "1";
                break;
            case 2:
                notifyArea = "2";
                break;
        }

        new ChallengeRequestTask(ChallengeRequestActivity.this, SelfInfoModel.userID, responseEdit.getText().toString()
                , estName, startTimeText, endTimeText, notifyArea, moneyEdit.getText().toString()
                , new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeRequestActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        finish();
//                        Toast.makeText();
                    } else {
                        GlobalVars.showErrAlert(ChallengeRequestActivity.this);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
    }
}