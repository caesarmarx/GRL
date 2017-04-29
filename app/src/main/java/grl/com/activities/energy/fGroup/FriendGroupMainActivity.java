package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.jivesoftware.smack.Chat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChatUserModel;
import grl.com.httpRequestTask.friendGroup.FriendListTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.view.GroupChatView;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FriendGroupMainActivity extends Activity implements View.OnClickListener{

    // required
    String strTitle;
    String strBackTitle;
    String groupID;
    String bossID;

    GroupChatView chatView;
    ArrayList<ChatUserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_main);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();

    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        groupID = intent.getStringExtra("group_id");
        bossID = intent.getStringExtra("boss_id");
    }

    public void getViewByID () {
        chatView = (GroupChatView)findViewById(R.id.group_chat_view);
        chatView.GROUP_TYPE = Constant.CHAT_TGROUP;
        chatView.GROUP_ID = groupID;
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        userList = new ArrayList<ChatUserModel>();
        new FriendListTask(FriendGroupMainActivity.this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FriendGroupMainActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                JSONArray friendsList = result.getJSONArray("members");

                for (int i = 0; i < friendsList.length(); i++) {
                    JSONObject object = friendsList.getJSONObject(i);
                    ChatUserModel userModel = new ChatUserModel();
                    userModel.parseFromJson(object);
                    if (userModel.userID.compareTo(SelfInfoModel.userID) != 0)
                        userList.add(userModel);
                }
                if (bossID.compareTo(SelfInfoModel.userID) != 0) {
                    ChatUserModel model = new ChatUserModel();
                    model.userID = bossID;
                    model.userName = GlobalVars.getStringFromJson(result, "boss_name");
                    model.userPhoto = GlobalVars.getStringFromJson(result, "boss_photo");
                    userList.add(model);
                }
                chatView.setReceiptUsers(userList);
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.btn_group_ability).setOnClickListener(this);
        findViewById(R.id.btn_estimate).setOnClickListener(this);
        findViewById(R.id.btn_help).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_group_ability:            // 친구계의 능권단추를 누른 경우
                Utils.start_Activity(this, FGroupStatusActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_ability_title)),
                        new BasicNameValuePair("fgroup_id", groupID));
                break;
            case R.id.btn_estimate:                 // 친구계의 평가단추를 누른 경우
                Utils.start_Activity(this, FGroupEstActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_est_title)),
                        new BasicNameValuePair("fgroup_id", groupID));
                break;
            case R.id.btn_help:                     // 친구계의 방조단추를 누른 경우
                Utils.start_Activity(this, FGroupHelpActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_help_title)),
                        new BasicNameValuePair("fgroup_id", groupID));
                break;
            case R.id.btn_setting:                  // 친구계의 설정단추를 누른 경우
                if(SelfInfoModel.userID.equals(bossID))
                    Utils.start_Activity(this, FGroupSettingActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                            new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_setting_title)),
                            new BasicNameValuePair("fgroup_id", groupID));
                else
                    Utils.start_Activity(this, FGroupInvitedSettingActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                            new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_setting_title)),
                            new BasicNameValuePair("fgroup_id", groupID));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatView.onStart();
    }

    @Override
    protected void onStop() {
        chatView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        chatView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatView.onPause();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatView.onActivityResult(requestCode, resultCode, data);
    }
}
