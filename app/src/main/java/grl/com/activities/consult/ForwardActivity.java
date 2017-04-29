package grl.com.activities.consult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcUeDb;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import grl.com.adapters.chat.ForwardListAdapter;
import grl.com.adapters.order.ContactsAdapter;
import grl.com.configuratoin.CommonUtils;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.configuratoin.dbUtil.MsgUser;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.ContactsModel;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.chat.UserListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class ForwardActivity extends Activity implements View.OnClickListener, ForwardListAdapter.OnItemClickListener{

    private RecyclerView userListView;
    private ForwardListAdapter userListAdapter;

    long sqlId;

    private BroadcastReceiver mtcImSendingReceiver;
    private BroadcastReceiver mtcImSendOkReceiver;
    private BroadcastReceiver mtcImSendDidFailReceiver;

    MessageModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_target);

        Intent intent = getIntent();
        sqlId = intent.getLongExtra(Constant.MSG_SQL_ID, 0);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();

        registerReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        super.onDestroy();

    }

    public void getViewByID () {
        userListView = (RecyclerView) findViewById(R.id.user_list_view);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText("");
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.GONE);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    public void initializeData() {
        // set up adapters
        userListAdapter = new ForwardListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        userListView.setLayoutManager(mLayoutManager);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);
        userListAdapter.setItemOnClickListener(this);

        final List<MsgUser> users = DBManager.getAllUsers(SelfInfoModel.userID);
        if (users.size() == 0) return;

        String[] userIds = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userIds[i] = users.get(i).getToUserId();
        }

        new UserListTask(ForwardActivity.this, userIds, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ForwardActivity.this);
                    return;
                }
                userListAdapter.myList.clear();
                try {
                    JSONArray result = (JSONArray) response;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        ConsultUserModel model = new ConsultUserModel();
                        model.parseFromJson(object);
                        userListAdapter.myList.add(model);
                    }
                } catch (Exception ex) {

                }
                userListAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    public void onForwardClick(int position) {
        ConsultUserModel userModel = userListAdapter.myList.get(position);
        model = DBManager.getMessage(sqlId);
        if (model == null)
            return;
        model.setSqlId(0);
        model.setMsgId(CommonUtils.getRndMsgId());
        model.setMsgFromUserId(SelfInfoModel.userID);
        model.setMsgToUserId(userModel.userID);
        model.setMsgFromMe(true);
        model.setbRead(true);
        model.setSendDate(new Date());
        model.setGroupType(Constant.CHAT_CONSULT);
        if (model.getMsgType().compareTo(Constant.MSG_TEXT_TYPE) == 0 ||
                model.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) == 0 ||
                model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0)
            sendText();
        if (model.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0 ||
                model.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0 ||
                model.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
            sendFile();
        }
        finish();

    }

    private void sendText() {
        model = DBManager.insertMessage(model);
        String info = String.format(Locale.getDefault(), "{\"MtcImDisplayNameKey\":\"%s\"}", MtcUeDb.Mtc_UeDbGetUserName());
        String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, model.getMsgToUserId());
        String type = String.format("%s-%s-%s", model.getGroupType(), model.getMsgType(), model.getMsgId());
        int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
        if (ret != MtcConstants.ZOK) {
            model.setMsgStatus(Constant.MSG_SEND_FAIL);
            DBManager.updateMessageState(Constant.MSG_SEND_FAIL, model.getSqlId());
        }
    }
    private void sendFile() {
        int type = -1;
        String fileType = model.getMsgType();
        if (fileType.compareTo(Constant.MSG_IMAGE_TYPE) == 0)
            type = MtcImConstants.EN_MTC_IM_FILE_IMAGE;
        if (fileType.compareTo(Constant.MSG_VIDEO_TYPE) == 0)
            type = MtcImConstants.EN_MTC_IM_FILE_VIDEO;
        if (fileType.compareTo(Constant.MSG_VOICE_TYPE) == 0)
            type = MtcImConstants.EN_MTC_IM_FILE_VOICE;

        String userUri = MtcUser.Mtc_UserFormUri(
                MtcUserConstants.EN_MTC_USER_ID_USERNAME, model.getMsgToUserId());
        int ret = MtcIm.Mtc_ImSendFile(0, userUri, type, model.getFilePath(), model.getThumbnailUrl());
        if (ret != MtcConstants.ZOK) {
            model.setMsgStatus(Constant.MSG_SEND_FAIL);
            DBManager.updateMessageState(Constant.MSG_SEND_FAIL, model.getSqlId());
        }

    }

    private void registerReceivers() {
        mtcImSendingReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);

                try {
                    JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                    int progress = json.getInt(MtcImConstants.MtcImProgressKey);
                    model.setProgress(progress);
                    model.setMsgStatus(Constant.MSG_SEND_PROGRESS);
                    DBManager.updateMessageState(Constant.MSG_SEND_PROGRESS, model.getSqlId());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        mtcImSendOkReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                model.setMsgStatus(Constant.MSG_SEND_SUCCESS);
                DBManager.updateMessageState(Constant.MSG_SEND_SUCCESS, model.getSqlId());

            }
        };

        mtcImSendDidFailReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                model.setMsgStatus(Constant.MSG_SEND_FAIL);
                DBManager.updateMessageState(Constant.MSG_SEND_FAIL, model.getSqlId());
            }
        };
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mtcImSendingReceiver, new IntentFilter(MtcImConstants.MtcImSendingNotification));
        broadcastManager.registerReceiver(mtcImSendOkReceiver, new IntentFilter(MtcImConstants.MtcImSendOkNotification));
        broadcastManager.registerReceiver(mtcImSendDidFailReceiver, new IntentFilter(MtcImConstants.MtcImSendDidFailNotification));
    }
    private void unregisterReceivers() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(mtcImSendingReceiver);
        broadcastManager.unregisterReceiver(mtcImSendOkReceiver);
        broadcastManager.unregisterReceiver(mtcImSendDidFailReceiver);
        mtcImSendingReceiver = null;
        mtcImSendOkReceiver = null;
        mtcImSendDidFailReceiver = null;
    }
}
