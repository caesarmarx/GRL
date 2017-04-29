package grl.com.activities.discovery.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.activities.energy.tGroup.TGroupEstimateActivity;
import grl.com.activities.energy.tGroup.TGroupPrepareActivity;
import grl.com.activities.energy.tGroup.challenge.StudentChallengeActivity;
import grl.com.activities.energy.tGroup.challenge.TeacherChallengeActivity;
import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.adapters.chat.ChatUserListAdapter;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChatUserModel;
import grl.com.httpRequestTask.order.GetAccpetUserTask;
import grl.com.httpRequestTask.tGroup.TeacherDiscipleTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.view.GroupChatView;
import grl.wangu.com.grl.R;

public class MyOrderChatActivity extends Activity implements View.OnClickListener{

    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mUserList;
    private LinearLayout mDrawView;
    private ChatUserListAdapter userListAdapter;

    // views
    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    // require
    String orderContentId;                            // 스승제자계 식별자

    GroupChatView chatView;
    ArrayList<ChatUserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_chat);

        Intent intent = getIntent();
        orderContentId = intent.getStringExtra(Constant.ORDER_CONTENT_ID);

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

    private void initView() {
        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_left);
        textBack.setText(getResources().getText(R.string.user_nav_back));
        textTitle = (TextView)findViewById(R.id.txt_title);
        textTitle.setText(getResources().getText(R.string.order_chat));
        imgBack.setVisibility(View.VISIBLE);
        textBack.setVisibility(View.VISIBLE);

        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawView = (LinearLayout)findViewById(R.id.drawer_view);
        mUserList = (RecyclerView) findViewById(R.id.user_list_view);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mUserList.setLayoutManager(mLayoutManager);
        mUserList.setItemAnimator(new DefaultItemAnimator());
        userListAdapter = new ChatUserListAdapter(this);
        mUserList.setAdapter(userListAdapter);



        chatView = (GroupChatView)findViewById(R.id.group_chat_view);
        chatView.GROUP_TYPE = Constant.CHAT_ORDER;
        chatView.GROUP_ID = orderContentId;
    }

    private void initData() {
        userList = new ArrayList<ChatUserModel>();

        new GetAccpetUserTask(this, SelfInfoModel.userID, orderContentId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                userList.clear();
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(MyOrderChatActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    for (int i = 0; i < result.length(); i++ ) {
                        JSONObject object = result.getJSONObject(i);
                        ChatUserModel userModel = new ChatUserModel();
                        userModel.parseFromJson(object);
                        userList.add(userModel);
                    }
                } catch (Exception e) {

                }
                userListAdapter.myList = userList;
                userListAdapter.notifyDataSetChanged();
                chatView.setReceiptUsers(userList);
            }
        });

    }

    void showUserList() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(mDrawView);
        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatView.onActivityResult(requestCode, resultCode, data);
    }
}
