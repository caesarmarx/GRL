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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import grl.com.adapters.chat.FavouriteListAdapter;
import grl.com.adapters.chat.ForwardListAdapter;
import grl.com.configuratoin.CommonUtils;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.configuratoin.dbUtil.MsgUser;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.chat.UserListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FavouriteActivity extends Activity implements View.OnClickListener, FavouriteListAdapter.OnItemClickListener{

    private RecyclerView msgListView;
    private FavouriteListAdapter msgListAdapter;

    MessageModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getViewByID() {
        msgListView = (RecyclerView) findViewById(R.id.msg_list_view);
    }

    private void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText("");
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.GONE);
    }

    private void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    private void initializeData() {
        // set up adapters
        msgListAdapter = new FavouriteListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        msgListView.setLayoutManager(mLayoutManager);
        msgListView.setItemAnimator(new DefaultItemAnimator());
        msgListView.setAdapter(msgListAdapter);
        msgListAdapter.setItemOnClickListener(this);

        final List<MessageModel> messages = DBManager.getFavouriteMsgList();
        if (messages.size() == 0) return;

        final String[] userIds = new String[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            userIds[i] = messages.get(i).getMsgFromUserId();
        }

        new UserListTask(FavouriteActivity.this, userIds, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(FavouriteActivity.this);
                    return;
                }
                msgListAdapter.myList.clear();
                try {
                    JSONArray result = (JSONArray) response;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        ConsultUserModel userModel = new ConsultUserModel();
                        userModel.parseFromJson(object);
                        for (int j = 0; j < messages.size(); j++) {
                            if (userModel.userID.compareTo(messages.get(j).getMsgFromUserId()) == 0) {
                                messages.get(i).setUserName(userModel.userName);;
                                messages.get(i).setPhoto(userModel.userPhoto);
                            }
                        }
                    }
                } catch (Exception ex) {

                }
                msgListAdapter.myList = messages;
                msgListAdapter.notifyDataSetChanged();
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

    @Override
    public void onFavouriteClick(int position) {
        MessageModel model = msgListAdapter.myList.get(position);
        Intent data = new Intent();
        data.putExtra("Position", position);
        setResult(RESULT_OK, data);
        finish();
    }
}
