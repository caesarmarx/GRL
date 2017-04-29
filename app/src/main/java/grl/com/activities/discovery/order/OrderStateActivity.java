package grl.com.activities.discovery.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.Util;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.body.StringBody;

import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.activities.common.TextFieldEditerActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.order.GetOrderStateTask;
import grl.com.httpRequestTask.order.SetOrderStateTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class OrderStateActivity extends Activity implements View.OnClickListener {

    private String contentId;
    private String userId;

    String stateTime = "";
    String stateGoal = "";
    String stateEffect = "";
    String statePosition = "";

    int acceptCnt = 0;
    int resendCnt = 0;
    int solveCnt = 0;

    private TextView acceptCntView;
    private TextView resendCntView;
    private TextView solveCntView;

    private static final int TIME_INPUT_REQUEST = 1;
    private static final int GOAL_INPUT_REQUEST = 2;
    private static final int EFFECT_INPUT_REQUEST = 3;
    private static final int POSITION_INPUT_REQUEST = 4;

    boolean bEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_state);

        Intent intent = getIntent();
        contentId = intent.getStringExtra(Constant.ORDER_CONTENT_ID);

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        acceptCntView = (TextView) findViewById(R.id.tv_order_accept_cnt);
        resendCntView = (TextView) findViewById(R.id.tv_order_resend_cnt);
        solveCntView = (TextView) findViewById(R.id.tv_order_solve_cnt);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.linear_state_time).setOnClickListener(this);
        findViewById(R.id.linear_state_goal).setOnClickListener(this);
        findViewById(R.id.linear_state_effect).setOnClickListener(this);
        findViewById(R.id.linear_state_location).setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
//        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_accept));
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.order_state));
    }

    public void initializeData () {
        // set up Adapter
        refresh();

    }

    public void refresh() {
        new GetOrderStateTask(this, SelfInfoModel.userID, contentId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OrderStateActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    stateTime = GlobalVars.getStringFromJson(result, "state_time");
                    stateGoal = GlobalVars.getStringFromJson(result, "state_goal");
                    stateEffect = GlobalVars.getStringFromJson(result, "state_effect");
                    statePosition = GlobalVars.getStringFromJson(result, "state_position");

                    acceptCnt = GlobalVars.getIntFromJson(result, "accept_count");
                    resendCnt = GlobalVars.getIntFromJson(result, "resend_count");
                    solveCnt = GlobalVars.getIntFromJson(result, "solve_count");

                    userId = GlobalVars.getStringFromJson(result, "user_id");
                    if (userId.compareTo(SelfInfoModel.userID) == 0)
                        bEdit = true;
                    else
                        bEdit = false;
                } catch (Exception e) {

                }
                updateUI();
            }
        });
    }

    void updateUI() {
        acceptCntView.setText(String.format("%d人", acceptCnt));
        resendCntView.setText(String.format("%d人", resendCnt));
        solveCntView.setText(String.format("%d人", solveCnt));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.linear_state_time:
                stateTimeClick();
                break;
            case R.id.linear_state_goal:
                stateGoalClick();
                break;
            case R.id.linear_state_effect:
                stateEffectClick();
                break;
            case R.id.linear_state_location:
                stateLocationClick();
                break;

        }
    }

    private void stateTimeClick() {
        if (bEdit) {
            Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, TIME_INPUT_REQUEST,
                    new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.order_state_time)),
                    new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, stateTime));
        } else {
            Utils.start_Activity(this, ContentShowActivity.class,
                    new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.order_state_time)),
                    new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                    new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, stateTime));
        }
    }
    private void stateGoalClick() {
        if (bEdit) {
            Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, GOAL_INPUT_REQUEST,
                    new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.order_state_time)),
                    new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, stateTime));
        } else {
            Utils.start_Activity(this, ContentShowActivity.class,
                    new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.order_state_goal)),
                    new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                    new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, stateGoal));
        }
    }
    private void stateEffectClick() {
        if (bEdit) {
            Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, EFFECT_INPUT_REQUEST,
                    new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.order_state_time)),
                    new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, stateTime));
        } else {
            Utils.start_Activity(this, ContentShowActivity.class,
                    new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.order_state_effect)),
                    new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                    new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, stateEffect));
        }
    }
    private void stateLocationClick() {
        if (bEdit) {
            Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, POSITION_INPUT_REQUEST,
                    new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.order_state_time)),
                    new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, stateTime));
        } else {
            Utils.start_Activity(this, ContentShowActivity.class,
                    new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.order_state_location)),
                    new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                    new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, statePosition));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TIME_INPUT_REQUEST) {
                stateTime = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                updateStateValue();
                return;
            }
            if (requestCode == GOAL_INPUT_REQUEST) {
                stateGoal = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                updateStateValue();
                return;
            }
            if (requestCode == EFFECT_INPUT_REQUEST) {
                stateEffect = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                updateStateValue();
                return;
            }
            if (requestCode == POSITION_INPUT_REQUEST) {
                statePosition = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                updateStateValue();
                return;
            }
        }
    }

    private void updateStateValue() {
        new SetOrderStateTask(this, SelfInfoModel.userID, contentId, stateTime, stateGoal, stateEffect, statePosition, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OrderStateActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        Toast.makeText(OrderStateActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT);
                    } else {
                        GlobalVars.showErrAlert(OrderStateActivity.this);
                    }
                } catch (Exception e) {

                }
                updateUI();
            }
        });
    }
}
