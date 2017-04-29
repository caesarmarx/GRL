package grl.com.activities.energy.tGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.energy.tGroup.challenge.StudentChallengeActivity;
import grl.com.activities.energy.tGroup.challenge.TeacherChallengeActivity;
import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChatUserModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.tGroup.TeacherDiscipleTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.view.GroupChatView;
import grl.wangu.com.grl.R;

public class TGroupActivity extends Activity implements View.OnClickListener{

    // views
    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    // require
    String tgroupID;                            // 스승제자계 식별자
    String tgroupName;                            // 스승제자계 식별자
    String tgroupPhoto;                            // 스승제자계 식별자

    GroupChatView chatView;
    ArrayList<ChatUserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgroup);

        getParamsFromIntent();
        initView();
        initData();
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

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        tgroupID = intent.getStringExtra(Constant.TGROUP_ID);
        tgroupName = intent.getStringExtra(Constant.TGROUP_NAME);
        tgroupPhoto = intent.getStringExtra(Constant.TGROUP_PHOTO);
    }
    private void initView() {
        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_left);
        textBack.setText(getResources().getText(R.string.user_nav_back));
        textTitle = (TextView)findViewById(R.id.txt_title);
        textTitle.setText(getResources().getText(R.string.tgroup_lesson_title));
        imgBack.setVisibility(View.VISIBLE);
        textBack.setVisibility(View.VISIBLE);

        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);

        chatView = (GroupChatView)findViewById(R.id.group_chat_view);
        chatView.GROUP_TYPE = Constant.CHAT_TGROUP;
        chatView.GROUP_ID = tgroupID;

    }

    private void initData() {
        userList = new ArrayList<ChatUserModel>();

        GlobalVars.showWaitDialog(TGroupActivity.this);
        new TeacherDiscipleTask(TGroupActivity.this, tgroupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(TGroupActivity.this);
                    return;
                }
                GlobalVars.hideWaitDialog();
                JsonObject result = (JsonObject) Response;
                String jsonString = result.toString();
                JSONObject jsonResult = new JSONObject(jsonString);
                JSONArray users = jsonResult.getJSONArray("disciple_group");
                for (int i = 0; i < users.length(); i++) {
                    JSONObject object = users.getJSONObject(i);
                    ChatUserModel model = new ChatUserModel();
                    model.parseFromJson(object);
                    if (model.userID.compareTo(SelfInfoModel.userID) !=0)
                        userList.add(model);
                }
                users = jsonResult.getJSONArray("teacher_group");
                for (int i = 0; i < users.length(); i++) {
                    JSONObject object = users.getJSONObject(i);
                    ChatUserModel model = new ChatUserModel();
                    model.parseFromJson(object);
                    if (model.userID.compareTo(SelfInfoModel.userID) !=0)
                        userList.add(model);
                }
                if (tgroupID.compareTo(SelfInfoModel.userID) != 0) {
                    ChatUserModel model = new ChatUserModel();
                    model.userID = tgroupID;
                    model.userName = tgroupName;
                    model.userPhoto = tgroupPhoto;
                    userList.add(model);
                }
                chatView.setReceiptUsers(userList);
                chatView.updateChatHistory();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    // 스승제자계 수업준비창으로 이행한다.
    public void prepareClick(View view) {
        Utils.start_Activity(this, TGroupPrepareActivity.class,
                new BasicNameValuePair(Constant.TGROUP_ID, tgroupID));
    }

    // 스승제자계 대전창으로 이행한다.
    public void leagueClick(View view) {
        Utils.start_Activity(this, LeagueActivity.class,
                new BasicNameValuePair(Constant.TGROUP_ID, tgroupID));
    }

    // 스승제자계 평가창으로 이행한다.
    public void estimateClick(View view) {
        Utils.start_Activity(this, TGroupEstimateActivity.class,
                new BasicNameValuePair(Constant.TGROUP_ID, tgroupID));
    }

    // 스승제자계 도전창으로 이행한다.
    public void challengeClick(View view) {
        if (tgroupID.compareTo(SelfInfoModel.userID) == 0) {
            Utils.start_Activity(this, TeacherChallengeActivity.class,
                    new BasicNameValuePair(Constant.TGROUP_ID, tgroupID),
                    new BasicNameValuePair(Constant.TGROUP_NAME, tgroupName),
                    new BasicNameValuePair(Constant.TGROUP_PHOTO, tgroupPhoto));
        } else {
            Utils.start_Activity(this, StudentChallengeActivity.class,
                    new BasicNameValuePair(Constant.TGROUP_ID, tgroupID),
                    new BasicNameValuePair(Constant.TGROUP_NAME, tgroupName),
                    new BasicNameValuePair(Constant.TGROUP_PHOTO, tgroupPhoto));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatView.onActivityResult(requestCode, resultCode, data);
    }
}
