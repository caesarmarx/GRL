package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.fgroup.FGroupHelpAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FriendHelpListTask;
import grl.com.httpRequestTask.friendGroup.FriendHelpReqTask;
import grl.com.httpRequestTask.friendGroup.FriendListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class FGroupHelpActivity extends Activity implements View.OnClickListener {
    // views
    RecyclerView recyclerView;
    EditText etReqContent;

    // require
    String strBackTitle;
    String strTitle;
    String groupID;

    //tasks
    FriendListTask friendListTask;
    FriendHelpListTask friendHelpListTask;
    FriendHelpReqTask friendHelpReqTask;

    // response
    JSONArray helpList;
    JSONArray friendsList;

    // values
    FGroupHelpAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_help);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        groupID = intent.getStringExtra("fgroup_id");
    }

    public void getViewByID () {
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.etReqContent = (EditText) findViewById(R.id.et_req_content);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        // init values
        if(helpList == null || friendsList == null) {
            helpList = new JSONArray();
            friendsList = new JSONArray();
        }

        // set up recycler view
        recyclerAdapter = new FGroupHelpAdapter(this);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManagerTop);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // send request
        getFriendLst();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.bnt_request_h).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.bnt_request_h:                     // 도움발표 진행
                onPublish();
                break;
        }
    }

    public void getFriendLst () {
        friendListTask = new FriendListTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupHelpActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                friendsList = result.getJSONArray("members");
                getHelpLst();
            }
        });
    }

    public void getHelpLst () {
        friendHelpListTask = new FriendHelpListTask(this, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupHelpActivity.this);
                    return;
                }
                helpList = (JSONArray) Response;
                // refresh recycler view
                recyclerAdapter.notifyData(helpList, friendsList);
            }
        });
    }

    // 새로운 도움발표진행
    public void onPublish () {
        // check reqquest content
        String content = etReqContent.getText().toString();
        if(content.equals("")) {
            GlobalVars.showCommonAlertDialog(this, getString(R.string.fgroup_help_req_empty), "");
            return;
        }
        friendHelpReqTask = new FriendHelpReqTask(this, groupID, content, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupHelpActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                helpList.put(result);
                // refresh recycler view
                recyclerAdapter.notifyData(helpList, friendsList);
            }
        });
    }
}
