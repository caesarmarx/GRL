package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.activities.common.MultilineTextEditerActivity;
import grl.com.activities.common.TextFieldEditerActivity;
import grl.com.activities.discovery.order.ReportActivity;
import grl.com.adapters.energy.fgroup.FGroupHorizontalAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.httpRequestTask.friendGroup.FGroupGetInfoTask;
import grl.com.httpRequestTask.friendGroup.FGroupQuitTask;
import grl.com.httpRequestTask.friendGroup.FGroupSetInfoTask;
import grl.com.httpRequestTask.friendGroup.FriendListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupSettingActivity extends Activity implements View.OnClickListener{

    // views
    private RecyclerView horizontalView;
    private Button btnExit;
    private TextView tvRule;
    private TextView tvAdver;
    private RelativeLayout nickNameSetting;
    private TextView tvClear;
    private TextView tvPost;
    private RelativeLayout featureSetting;
    private RelativeLayout amountSetting;
    private TextView tvInfo;
    private TextView tvNick;
    private TextView tvFeature;
    private TextView tvAmount;

    // tasks
    FGroupGetInfoTask fGroupGetInfoTask;
    FriendListTask friendListTask;
    FGroupSetInfoTask fGroupSetInfoTask;
    FGroupQuitTask fGroupQuitTask;

    // response
    JSONObject friendInfo;
    JSONObject userInfo;
    JSONArray friendsList;

    // require
    String strTitle;
    String strBackTitle;
    String groupID;

    // values
    private FGroupHorizontalAdapter horizontalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_setting);

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
        JsonObject groupInfo = (JsonObject) new JsonParser().parse(friendInfo.toString());
        JsonObject userInfoParam = (JsonObject) new JsonParser().parse(userInfo.toString());
        groupInfo.addProperty("expense_rate", tvAmount.getText().toString());
        groupInfo.addProperty("energy_feature", tvFeature.getText().toString());
        userInfoParam.addProperty("nickName", tvNick.getText().toString());
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.addProperty("group_id", groupID);
        params.addProperty("user_id", SelfInfoModel.userID);
        params.add("group_info", groupInfo);
        params.add("user_info", userInfoParam);
        fGroupSetInfoTask = new FGroupSetInfoTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupSettingActivity.this);
                    return;
                }

                JsonObject result = (JsonObject) Response;
                boolean success = result.get("set_result").getAsBoolean();
                if(!success) {
                    GlobalVars.showErrAlert(FGroupSettingActivity.this);
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
        this.horizontalView = (RecyclerView) findViewById(R.id.horizontalRecyclerView);
        this.btnExit = (Button) findViewById(R.id.btn_exit);
        tvRule = (TextView) findViewById(R.id.tvRule);
        tvAdver = (TextView) findViewById(R.id.tvPost);
        nickNameSetting = (RelativeLayout) findViewById(R.id.nickSetting);
        tvClear = (TextView) findViewById(R.id.tvClear);
        tvPost = (TextView) findViewById(R.id.txtPost);
        featureSetting = (RelativeLayout) findViewById(R.id.featureSetting);
        amountSetting = (RelativeLayout) findViewById(R.id.amountSetting);
        tvInfo = (TextView) findViewById(R.id.txtInfo);

        tvNick = (TextView) findViewById(R.id.txtNick);
        tvFeature = (TextView) findViewById(R.id.txtFeature);
        tvAmount = (TextView) findViewById(R.id.txtOrderAmount);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.fgroup_setting_title));
    }

    public void initializeData () {
        // set up Adapter
        horizontalAdapter = new FGroupHorizontalAdapter(this);
        horizontalView.setItemAnimator(new DefaultItemAnimator());
        horizontalView.setAdapter(horizontalAdapter);

        // get data from server
        this.getFgroupInfo();
        this.getFriedsLst();
    }


    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        this.btnExit.setOnClickListener(this);
        tvRule.setOnClickListener(this);
        tvAdver.setOnClickListener(this);
        nickNameSetting.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        tvPost.setOnClickListener(this);
        featureSetting.setOnClickListener(this);
        amountSetting.setOnClickListener(this);
        tvInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_exit:                     // 퇴출단추를 누른 경우
                onExitUsers();
                break;
            case R.id.tvRule:                       // 본계 규칙
                try {
                    Utils.start_Activity(this, ContentShowActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_setting_title)),
                            new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_rule_title)),
                            new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, friendInfo.getString("group_rule")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvPost:                       // 본계 광고
                try {
                    Utils.start_ActivityForResult(this, MultilineTextEditerActivity.class, R.id.tvPost,
                            new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_post_title)),
                            new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, friendInfo.getString("group_post")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nickSetting:                  // 별명 설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.nickSetting,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_nick_name)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvNick.getText().toString()));
                break;
            case R.id.tvClear:                      // 대화 기록 지우기
                DBManager.removeMessageByGroupId(Constant.CHAT_FGROUP, groupID);
                break;
            case R.id.txtPost:                      // 신고 처리
                Utils.start_Activity(this, ReportActivity.class);
                break;
            case R.id.featureSetting:               // 능세특징 설정
                Utils.start_ActivityForResult(this, MultilineTextEditerActivity.class, R.id.featureSetting,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_feature_title)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvFeature.getText().toString()));
                break;
            case R.id.amountSetting:                // 가격설정
                Utils.start_ActivityForResult(this, TextFieldEditerActivity.class, R.id.amountSetting,
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_order_amount)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, tvAmount.getText().toString()));
                break;
            case R.id.txtInfo:                      // 런계정보설정
                Utils.start_ActivityForResult(this, FGroupResActivity.class, R.id.txtInfo,
                        new BasicNameValuePair("data", userInfo.toString()),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_member_info)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_setting_title)));
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case R.id.tvPost:                       // 본계광고설정
                try {
                    friendInfo.put("group_post", data.getStringExtra(Constant.TEXTFIELD_CONTENT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nickSetting:                  // 별명설정
                tvNick.setText(data.getStringExtra(Constant.TEXTFIELD_CONTENT));
                break;
            case R.id.featureSetting:               // 능세특징 설정
                tvFeature.setText(data.getStringExtra(Constant.TEXTFIELD_CONTENT));
                break;
            case R.id.amountSetting:                // 가격설정
                tvAmount.setText(data.getStringExtra(Constant.TEXTFIELD_CONTENT));
                break;
            case R.id.txtInfo:                      // 정보설정
                String strData = data.getStringExtra("data");
                try {
                    userInfo = new JSONObject(strData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // 친구계정보얻기
    public void getFgroupInfo () {
        fGroupGetInfoTask = new FGroupGetInfoTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupSettingActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                friendInfo = result.getJSONObject("group_info");
                userInfo = result.getJSONObject("user_info");

                // set values to view
                tvFeature.setText(friendInfo.getString("energy_feature"));
                tvAmount.setText(friendInfo.getString("expense_rate"));
                tvNick.setText(userInfo.getString("nickName"));
            }
        });
    }

    // 친구계목록얻기
    public void getFriedsLst () {
       friendListTask = new FriendListTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
           @Override
           public void onResponse(Boolean flag, Object Response) throws JSONException {
               if(!flag || Response == null) {
                   GlobalVars.showErrAlert(FGroupSettingActivity.this);
                   return;
               }
               JSONObject result = (JSONObject) Response;
               friendsList = result.getJSONArray("members");
               horizontalAdapter.notifyData(friendsList);
           }
       });
    }

    // 선택된 사용자들을 탈퇴시킨다.
    public void onExitUsers () {
        JsonArray exitUsers = this.horizontalAdapter.getExitUsers();
        if(exitUsers.size() == 0) {
            GlobalVars.showCommonAlertDialog(this, getString(R.string.fgroup_mem_exit_empty), "");
            return;
        }
        JsonObject params = new JsonObject();
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.addProperty("group_id", groupID);
        params.add("users", exitUsers);
        fGroupQuitTask = new FGroupQuitTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupSettingActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean success = result.get("quit_result").getAsBoolean();
                if(!success) {
                    GlobalVars.showErrAlert(FGroupSettingActivity.this);
                } else
                {
                    getFriedsLst();
                }

            }
        });
    }

}
