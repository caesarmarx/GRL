package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.activities.common.TextFieldEditerActivity;
import grl.com.activities.discovery.order.ReportActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.httpRequestTask.friendGroup.FGroupGetInfoTask;
import grl.com.httpRequestTask.friendGroup.FGroupSetInfoTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupInvitedSettingActivity extends Activity implements View.OnClickListener {

    // views
    private Button btnExit;
    private TextView tvRule;
    private TextView tvAdver;
    private RelativeLayout nickNameSetting;
    private TextView tvClear;
    private Button btnPost;
    private TextView tvInfo;
    private Switch switchNick;
    private TextView tvNick;

    // tasks
    FGroupGetInfoTask fGroupGetInfoTask;
    FGroupSetInfoTask fGroupSetInfoTask;

    // response
    JsonObject friendInfo;
    JsonObject userInfo;

    // require
    String strTitle;
    String strBackTitle;
    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_invited_setting);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onDestroy() {
        // 설정값들을 보관한다.
        JsonObject params = new JsonObject();
        Integer status = 0;
        if(switchNick.isChecked()) {
            status = 1;
        }
        userInfo.addProperty("isNickNameDisplay", status);
        userInfo.addProperty("nickName", tvNick.getText().toString());
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.addProperty("group_id", groupID);
        params.addProperty("user_id", SelfInfoModel.userID);
        params.add("group_info", friendInfo);
        params.add("user_info", userInfo);
        fGroupSetInfoTask = new FGroupSetInfoTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupInvitedSettingActivity.this);
                    return;
                }

                JsonObject result = (JsonObject) Response;
                boolean success = result.get("set_result").getAsBoolean();
                if(!success) {
                    GlobalVars.showErrAlert(FGroupInvitedSettingActivity.this);
                }
            }
        });
        super.onDestroy();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        groupID = intent.getStringExtra("fgroup_id");
    }

    public void getViewByID () {
        this.btnExit = (Button) findViewById(R.id.btn_exit);
        tvRule = (TextView) findViewById(R.id.tvRule);
        tvAdver = (TextView) findViewById(R.id.tvPost);
        nickNameSetting = (RelativeLayout) findViewById(R.id.layoutNick);
        tvClear = (TextView) findViewById(R.id.tvClear);
        btnPost = (Button) findViewById(R.id.btn_post);
        tvInfo = (TextView) findViewById(R.id.txtInfo);
        switchNick = (Switch) findViewById(R.id.switch_nick);

        tvNick = (TextView) findViewById(R.id.txtNick);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {

        getFgroupInfo();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        this.btnExit.setOnClickListener(this);
        tvRule.setOnClickListener(this);
        tvAdver.setOnClickListener(this);
        nickNameSetting.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        tvInfo.setOnClickListener(this);

        switchNick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_exit:                     // 퇴출단추를 누른 경우
                break;
            case R.id.tvRule:                       // 본계 규칙
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_setting_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_rule_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, friendInfo.get("group_rule").getAsString()));
                break;
            case R.id.tvPost:                       // 본계 광고
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_setting_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_post_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, friendInfo.get("group_post").getAsString()));
                break;
            case R.id.nickSetting:                  // 별명 설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.nickSetting,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_nick_name)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvNick.getText().toString()));
                break;
            case R.id.switch_nick:                  // 스위치를 클릭할때의 처리 진행
                break;
            case R.id.tvClear:                      // 대화 기록 지우기
                DBManager.removeMessageByGroupId(Constant.CHAT_FGROUP, groupID);
                break;
            case R.id.txtPost:                      // 신고 처리
                Utils.start_Activity(this, ReportActivity.class);
                break;
            case R.id.txtInfo:                      // 런계정보설정
                String strData = userInfo.toString();
                Utils.start_ActivityForResult(this, FGroupResActivity.class, R.id.txtInfo,
                        new BasicNameValuePair("data", strData),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_member_info)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_setting_title)));
                break;
            }
    }

    // 친구계정보얻기
    public void getFgroupInfo () {
        fGroupGetInfoTask = new FGroupGetInfoTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupInvitedSettingActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                friendInfo = (JsonObject) new JsonParser().parse(result.getJSONObject("group_info").toString());
                userInfo = (JsonObject) new JsonParser().parse(result.getJSONObject("user_info").toString());

                // set values to view
                tvNick.setText(userInfo.get("nickName").getAsString());
                Integer isDisplay = userInfo.get("isNickNameDisplay").getAsInt();
                switch (isDisplay) {
                    case 0:
                        switchNick.setChecked(false);
                        break;
                    case 1:
                        switchNick.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.nickSetting:
                tvNick.setText(data.getStringExtra(Constant.TEXTFIELD_CONTENT));
                break;
            case R.id.txtInfo:                      // 정보설정
                String strData = data.getStringExtra("data");
                try {
                    userInfo = new JsonParser().parse(strData).getAsJsonObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
