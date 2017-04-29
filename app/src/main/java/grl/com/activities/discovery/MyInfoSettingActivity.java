package grl.com.activities.discovery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.activities.common.TextFieldEditerActivity;
import grl.com.activities.discovery.accountSetting.AccountSettingActivity;
import grl.com.activities.imageManage.UserPhotoSettingActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.httpRequestTask.discovery.UserInfoSettingTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class MyInfoSettingActivity extends Activity implements View.OnClickListener {

    // views
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
    TextView tvUserAccount;

    // tasks
    UserInfoGetTask userInfoGetTask;
    UserInfoSettingTask userInfoSettingTask;

    // response
    JSONObject userInfo;

    // values
    Integer gender;
    String[] genderArray = {"男", "女"};
    String userPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_settig);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        onConfirmAction();
        super.onDestroy();
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
        tvUserAccount = (TextView) findViewById(R.id.txtUserAccount);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.me_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.my_info_title));
    }

    public void initializeData () {
        // init values
        this.userInfo = new JSONObject();
        this.userPhotoPath = "";
        // get data from server
        userInfoGetTask = new UserInfoGetTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null)
                {
                    GlobalVars.showErrAlert(MyInfoSettingActivity.this);
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
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.layoutPhoto).setOnClickListener(this);
        findViewById(R.id.layoutUserName).setOnClickListener(this);
        findViewById(R.id.layoutUserNick).setOnClickListener(this);
        findViewById(R.id.layoutUserCert).setOnClickListener(this);
        findViewById(R.id.tvPrice).setOnClickListener(this);
        findViewById(R.id.layoutUserGender).setOnClickListener(this);
        findViewById(R.id.layoutUserArea).setOnClickListener(this);
        findViewById(R.id.layoutUserSkill).setOnClickListener(this);
        findViewById(R.id.layoutUserJob).setOnClickListener(this);
        findViewById(R.id.layoutUserEnergy).setOnClickListener(this);
        findViewById(R.id.layoutUserDonate).setOnClickListener(this);
        findViewById(R.id.layoutUserEst).setOnClickListener(this);
        findViewById(R.id.layoutUserComplete).setOnClickListener(this);
        findViewById(R.id.layoutUserAccount).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.layoutPhoto:                  // 사용자 그림보기
                Utils.start_ActivityForResult(this, UserPhotoSettingActivity.class, R.id.layoutPhoto,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, this.getString(R.string.me_title)),
                        new BasicNameValuePair(Constant.PHOTO_PATH, this.userPhotoPath));
                break;
            case R.id.layoutUserName:               // 사용자 이름 설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.layoutUserName,
                        new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.user_name_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvUserName.getText().toString()));
                break;
            case R.id.layoutUserNick:               // 사용자 별명 설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.layoutUserNick,
                        new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.user_nick_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvUserNick.getText().toString()));
                break;
            case R.id.layoutUserCert:               // 사용자 인증 보기
                break;
            case R.id.tvPrice:                      // 제가 받아들이기 가격 보기
                Utils.start_Activity(this, DisciplePriceActiviy.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.my_info_title)),
                        new BasicNameValuePair("user_id", SelfInfoModel.userID));
                break;
            case R.id.layoutUserGender:             // 성별설정
                showSelectGenderAlert();
                break;
            case R.id.layoutUserArea:                // 사용자 지역설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.layoutUserArea,
                        new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.user_area_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvUserArea.getText().toString()));
                break;
            case R.id.layoutUserSkill:               // 사용자 특기 설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.layoutUserSkill,
                        new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.user_skill_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvUserSkill.getText().toString()));
                break;
            case R.id.layoutUserJob:                 // 사용자 직업설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.layoutUserJob,
                        new BasicNameValuePair(Constant.TITLE_ID, this.getString(R.string.user_job_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvUserJob.getText().toString()));
                break;
            case R.id.layoutUserEnergy:              // 신능량 보기
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.user_energy_title)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.my_info_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, Constant.ENERGY_HELP));
                break;
            case R.id.layoutUserDonate:             // 기부 보기
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.user_donate_title)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.my_info_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, Constant.DONATE_HELP));
                break;
            case R.id.layoutUserEst:                // 평가 보기
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.user_est_title)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.my_info_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, Constant.EST_HELP));
                break;
            case R.id.layoutUserComplete:           // 완성정보 보기
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.user_complete_title)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.my_info_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, Constant.SUCCESS_HELP));
                break;
            case R.id.layoutUserAccount:            // 구좌정보 보기
                Utils.start_Activity(this, AccountSettingActivity.class);
                break;
            }
    }

    public void InitViews () throws JSONException {
        tvUserId.setText(userInfo.getString("grl_id"));
        tvUserName.setText(userInfo.getString("user_name"));
        tvUserArea.setText(userInfo.getString("user_area"));
        tvUserNick.setText(userInfo.getString("user_nickname"));
        tvUserSkill.setText(userInfo.getString("user_skill"));
        tvUserCert.setText(userInfo.getString("certification_cnt"));
        tvUserJob.setText(userInfo.getString("user_job"));
        tvUserEnergy.setText(userInfo.getString("user_ability") + "M");
        String strDonate = "已捐" + userInfo.getString("donate_total") + "令牌" + "  未捐" + userInfo.getString("to_donate_total") + "令牌";
        tvUserDonate.setText(strDonate);
        tvUserComplete.setText("1022令单");
        String strEst = getString(R.string.good) + userInfo.getString("user_judge_good") + " " +
                        getString(R.string.normal) + userInfo.getString("user_judge_normal") + " " +
                        getString(R.string.bad) + userInfo.getString("user_judge_bad");
        tvUserEst.setText(strEst);
        String strAccount = userInfo.getString("user_account") + "元";
        tvUserAccount.setText(strAccount);
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
        this.userPhotoPath = userInfo.getString("user_photo");
        GlobalVars.loadImage(imgUserPhoto, userPhotoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("before result", data.toString());
        if(resultCode != Activity.RESULT_OK)
            return;
        Log.e("after result", String.valueOf(requestCode));
        String result = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
        switch(requestCode) {
            case R.id.layoutUserNick:
                tvUserNick.setText(result);
                break;
            case R.id.layoutUserName:
                tvUserName.setText(result);
                break;
            case R.id.layoutUserArea:
                tvUserArea.setText(result);
                break;
            case R.id.layoutUserJob:
                tvUserJob.setText(result);
                break;
            case R.id.layoutUserSkill:
                tvUserSkill.setText(result);
                break;
            case R.id.layoutPhoto:                              // 사용자 화상을 설정한 경우
                String strPath = data.getStringExtra(Constant.PHOTO_PATH);
                Uri imageUri = Uri.parse(strPath);
                uploadImage(imageUri);
                break;
        }
    }

    public void showSelectGenderAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.user_gender_title));
        builder.setSingleChoiceItems(this.genderArray, gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    case 1:
                        tvUserGender.setText(genderArray[which]);
                        gender = which;
                        dialog.dismiss();
                        break;
                }
            }
        });
        AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
    }

    public int getGenderValue () {
        for(int i = 0; i < genderArray.length; i ++ ) {
            String strGender = tvUserGender.getText().toString();
            if(strGender.equals(genderArray[i]))
                return i;
        }
        return -1;
    }

    // 본인의 정보를 갱신한다.
    public void onConfirmAction () {
        try {
            userInfo.put("user_name", tvUserName.getText().toString());
            userInfo.put("user_area", tvUserArea.getText().toString());
            userInfo.put("user_sex", getGenderValue());
            userInfo.put("user_skill", tvUserSkill.getText().toString());
            userInfo.put("user_job", tvUserJob.getText().toString());
            userInfo.put("user_nickname", tvUserNick.getText().toString());

            updateUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateUserInfo () {
        userInfoSettingTask = new UserInfoSettingTask(userInfo, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(MyInfoSettingActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                Boolean val = result.getBoolean("result");
                if(!val) {
                    GlobalVars.showErrAlert(MyInfoSettingActivity.this);
                    return;
                }
                SelfInfoModel.userName = userInfo.getString("user_name");
                SelfInfoModel.userPhoto = userInfo.getString("user_photo");
                SelfInfoModel.userGender = userInfo.getInt("user_sex");
            }
        });
    }

    public void uploadImage(Uri imageUri) {
        if (imageUri == null) {
            return;
        }
        new FileUploadTask(this, imageUri.getPath(), Constant.MSG_IMAGE_TYPE, new HttpCallback() {

            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(MyInfoSettingActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    int success = result.getInt("upload_result");
                    if(success == 1) {
                        String filePath = result.getString("file_path");
                        userInfo.put("user_photo", filePath);
                        userPhotoPath = filePath;
                        GlobalVars.loadImage(imgUserPhoto, filePath);
                    } else {
                        GlobalVars.showErrAlert(MyInfoSettingActivity.this);
                        return;
                    }
                } catch (Exception e) {

                }

            }
        });
    }
}
