package grl.com.activities.otherRel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.discovery.DisciplePriceActiviy;
import grl.com.activities.imageManage.ImagePreviewActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class OtherInfoViewActivity extends Activity implements View.OnClickListener {

    // Views
    TextView tvUserId;
    ImageView imgUserPhoto;
    TextView tvUserName;
    TextView tvUserNick;
    TextView tvUserCert;
    TextView tvUserGender;
    TextView tvUserArea;
    TextView tvUserSkill;
    TextView tvUserJob;
    TextView tvUserEnergy;
    TextView tvUserDonate;
    TextView tvUserEst;
    TextView tvUserComplete;

    // tasks
    UserInfoGetTask userInfoGetTask;

    // response
    JSONObject userInfo;

    // required
    String strBackTitle;
    String userID;

    // values
    String userPhotoPath;
    Integer gender;
    String[] genderArray = {"男", "女"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherinfo_view);

        getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        this.strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        this.userID = intent.getStringExtra("user_id");
    }
    public void getViewByID () {
        tvUserId = (TextView) findViewById(R.id.txtUserId);
        imgUserPhoto = (ImageView) findViewById(R.id.imgUserPhoto);
        tvUserName = (TextView) findViewById(R.id.txtUserName);
        tvUserNick = (TextView) findViewById(R.id.txtUserNick);
        tvUserCert = (TextView) findViewById(R.id.txtCertAmount);
        tvUserGender = (TextView) findViewById(R.id.txtUserGender);
        tvUserArea = (TextView) findViewById(R.id.txtUserArea);
        tvUserSkill = (TextView) findViewById(R.id.txtUserSkill);
        tvUserJob = (TextView) findViewById(R.id.txtUserJob);
        tvUserEnergy = (TextView) findViewById(R.id.txtUserEnergy);
        tvUserDonate = (TextView) findViewById(R.id.txtUserDonate);
        tvUserEst = (TextView) findViewById(R.id.txtUserEst);
        tvUserComplete = (TextView) findViewById(R.id.txtUserComplet);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_info_title));
    }

    public void initializeData () {
// init values
        this.userInfo = new JSONObject();
        // get data from server
        userInfoGetTask = new UserInfoGetTask(this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null)
                {
                    GlobalVars.showErrAlert(OtherInfoViewActivity.this);
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

    public void InitViews () throws JSONException {
        tvUserId.setText(userInfo.getString("grl_id"));
        tvUserName.setText(userInfo.getString("user_name"));
        tvUserArea.setText(userInfo.getString("user_area"));
        tvUserNick.setText(userInfo.getString("user_nickname"));
        tvUserSkill.setText(userInfo.getString("user_skill"));
        tvUserCert.setText(userInfo.getString("certification_cnt"));
        tvUserJob.setText(userInfo.getString("user_job"));
        tvUserEnergy.setText(userInfo.getString("user_ability"));
        String strDonate = "已捐" + userInfo.getString("donate_total") + "令牌" + "  未捐" + userInfo.getString("to_donate_total") + "令牌";
        tvUserDonate.setText(strDonate);
        tvUserComplete.setText("1022令单");
        String strEst = getString(R.string.good) + userInfo.getString("user_judge_good") + " " +
                getString(R.string.normal) + userInfo.getString("user_judge_normal") + " " +
                getString(R.string.bad) + userInfo.getString("user_judge_bad");
        tvUserEst.setText(strEst);
        // 성별정보얻기
        Integer sex = userInfo.getInt("user_sex");
        gender = sex;
        switch (sex) {
            case 0:                     // male or female
            case 1:
                tvUserGender.setText(genderArray[sex]);
                break;
            default:                    // as male
                tvUserGender.setText(genderArray[0]);
                gender = 0;
                break;
        }
        userPhotoPath = userInfo.getString("user_photo");
        GlobalVars.loadImage(imgUserPhoto, userPhotoPath);
    }


    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.layoutPhoto).setOnClickListener(this);
        findViewById(R.id.layoutUserCert).setOnClickListener(this);
        findViewById(R.id.tvPrice).setOnClickListener(this);
        findViewById(R.id.tvOtherProfile).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.layoutPhoto:                 // 사용자 그림 보기
                Utils.start_Activity(this, ImagePreviewActivity.class,
                        new BasicNameValuePair(Constant.PHOTO_PATH, userPhotoPath));
                break;
            case R.id.layoutUserCert:               // 사용자 인증 보기
                break;
            case R.id.tvOtherProfile:                    // 프로화일 보기 진행
                String title = tvUserName.getText().toString() + "的路演";
                Utils.start_Activity(this, OtherProfileActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, title),
                        new BasicNameValuePair("user_id", userID));
                break;
            case R.id.tvPrice:                      // 제가 받아들이기 가격 보기
                Utils.start_Activity(this, DisciplePriceActiviy.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.other_info_title)),
                        new BasicNameValuePair("user_id", userID));
                break;
        }
    }
}
