package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.fgroup.FGroupEstAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.EstListTask;
import grl.com.httpRequestTask.friendGroup.FGroupEstListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FGroupEstActivity extends Activity implements View.OnClickListener {

    // Views
    private RecyclerView recyclerView;
    private TextView groupTitle;

    // Require
    String strBackTitle;
    String strTitle;
    String groupID;

    // Tasks
    FGroupEstListTask fGroupEstListTask;
    EstListTask estListTask;

    // Response
    JSONArray groupFriends;
    JSONArray estList;
    Boolean estEnable;

    // Values
    FGroupEstAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_estimate);

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
        this.groupTitle = (TextView) findViewById(R.id.txt_group_title);
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
        groupFriends = new JSONArray();
        estEnable = false;

        // set up RecyclerView
        recyclerAdapter = new FGroupEstAdapter(this);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManagerTop);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // send request
        getEstIndexList();
    }

    public void getListofFriend () {
        // 평가가능한 사용자들의 목록을 얻어온다.
        fGroupEstListTask = new FGroupEstListTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupEstActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                groupFriends = result.getJSONArray("member_list");
                estEnable = result.getBoolean("est_enable");
                // 자료를 갱신한다.
                recyclerAdapter.notifyData(groupFriends, estEnable, groupID);
            }
        });
    }
    public void getEstIndexList () {
        // 친구계 평가지표목록을 얻어온다.
        estListTask = new EstListTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupEstActivity.this);
                    return;
                }
                estList = (JSONArray) Response;
                recyclerAdapter.setEstList(estList);
                getListofFriend();
            }
        });
    }
    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }
}
