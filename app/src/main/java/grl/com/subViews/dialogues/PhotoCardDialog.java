package grl.com.subViews.dialogues;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.discovery.DiscoverySettingActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/4/2016.
 */
public class PhotoCardDialog extends Activity implements View.OnClickListener{
    // view
    ViewGroup layoutContent;
    TextView tvTitle;
    ImageView imgUserPhoto;
    TextView tvUserName;
    TextView tvUserJob;
    TextView tvEst;
    TextView tvRel;
    TextView tvStatus;
    TextView tvDistance;

    // task
    UserInfoGetTask userInfoGetTask;

    // response
    JSONObject userInfo;

    // require
    String userID;
    String strBackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_head_card);

        getParamsFromIntent();
        getViewByID();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        userID = intent.getStringExtra("user_id");
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
    }

    public void getViewByID () {
        layoutContent = (ViewGroup) findViewById(R.id.layoutContent);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        imgUserPhoto = (ImageView) findViewById(R.id.imgPhoto);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserJob = (TextView) findViewById(R.id.tvUserJob);
        tvEst = (TextView) findViewById(R.id.tvUserEst);
        tvRel = (TextView) findViewById(R.id.tvRel);
        tvStatus = (TextView) findViewById(R.id.tvUserStatus);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
    }

    public void initializeData () {
        // init view
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // get user data from server
        userInfoGetTask = new UserInfoGetTask(this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null)
                {
                    GlobalVars.showErrAlert(PhotoCardDialog.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                if(result.length() > 0) {
                    userInfo = result;
                    InitViews();
                }
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.btnConfirm).setOnClickListener(this);
        findViewById(R.id.imgPhoto).setOnClickListener(this);
    }

    public void InitViews () {
        try {
            String strTitle = tvTitle.getText().toString() + userInfo.getString("user_skill");
            String strStatus = tvStatus.getText().toString();
            int status = userInfo.getInt("user_status");
            strStatus = strStatus + getString(R.string.tab) + DiscoverySettingActivity.getStatus(status);
            String strEst = getString(R.string.good) + " " + userInfo.getString("user_judge_good") + "  " +
                    getString(R.string.normal) + " " + userInfo.getString("user_judge_normal") + "  " +
                    getString(R.string.bad) + " " + userInfo.getString("user_judge_bad");
            strEst = tvEst.getText().toString() + getString(R.string.tab) + strEst;
            String strRel = "师 " + userInfo.getString("teacher_cnt") + "  徒 " + userInfo.getString("disciple_cnt");
            strRel = tvRel.getText().toString() + getString(R.string.tab) + strRel;
            String strJob = tvUserJob.getText().toString()  + getString(R.string.tab) + userInfo.getString("user_job");


            GlobalVars.loadImage(imgUserPhoto, userInfo.getString("user_photo"));
            tvTitle.setText(strTitle);
            tvUserName.setText(userInfo.getString("user_name"));
            tvStatus.setText(strStatus);
            tvEst.setText(strEst);
            tvRel.setText(strRel);
            tvUserJob.setText(strJob);
            tvDistance.setText(GlobalVars.getDistanceString(SelfInfoModel.latitude, SelfInfoModel.longitude,
                    GlobalVars.getDoubleFromJson(userInfo, "latitude"), GlobalVars.getDoubleFromJson(userInfo, "longitude")));

        } catch (Exception e) {}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:                           // 확인단추를 누를 때의 처리
                finish();
                break;
            case R.id.imgPhoto:                             // 화상을 누른경우 제3인자창으로 이행한다.
                try {
                    String userName = userInfo.getString("user_name");
                    String userPhoto = userInfo.getString("user_photo");
                    Utils.start_Activity(this, OtherMainActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, strBackTitle),
                            new BasicNameValuePair(Constant.TITLE_ID, userName),
                            new BasicNameValuePair("user_id", userID),
                            new BasicNameValuePair("user_photo", userPhoto));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
