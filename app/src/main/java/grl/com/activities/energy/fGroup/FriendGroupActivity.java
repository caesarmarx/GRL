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

import grl.com.adapters.energy.fgroup.FGroupViewAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FGroupListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FriendGroupActivity extends Activity implements View.OnClickListener{

    // Views
    private RecyclerView recyclerViewTop;
    private RecyclerView recyclerViewDown;

    // tasks
    private FGroupListTask fGroupListTask;

    // Response
    private JSONArray myFriendGroup;
    private JSONArray yourFriendGroup;

    // values
    private FGroupViewAdapter yourAdpater;
    private  FGroupViewAdapter myAdapter;
    public static FriendGroupActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_group);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getViewByID() {
        this.recyclerViewTop = (RecyclerView) findViewById(R.id.top_recycler_view);
        this.recyclerViewDown = (RecyclerView) findViewById(R.id.down_recycler_view);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.tab_energy_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.friend_group_title));
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.btn_create_group).setOnClickListener(this);
    }
    public void initializeData () {
        // init values
        self = this;
        // set up Adapter
        yourAdpater = new FGroupViewAdapter(this, 0);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        recyclerViewTop.setLayoutManager(mLayoutManagerTop);
        recyclerViewTop.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTop.setAdapter(yourAdpater);

        myAdapter = new FGroupViewAdapter(this, 1);
        RecyclerView.LayoutManager mLayoutManagerDown = new LinearLayoutManager(getApplicationContext());
        recyclerViewDown.setLayoutManager(mLayoutManagerDown);
        recyclerViewDown.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDown.setAdapter(myAdapter);

        this.getFgroupInfo();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_create_group:             // 친구계창설 단추를 누른 경우
                Utils.start_ActivityForResult(this, FriendGroupCreateActivity.class, R.id.btn_create_group);
                break;
        }
    }

    // 친구계가 이미 존재하는가를 판별한다.
    public  Boolean isExistingGroup(String groupName) {
        for(int i = 0; i < myFriendGroup.length(); i ++) {
            JSONObject temp = null;
            try {
                temp = myFriendGroup.getJSONObject(i);
                String groupTitle = temp.getString("group_name");
                if(groupName.equals(groupTitle)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return  false;
    }

    public void getFgroupInfo () {
// sernd request
        if(fGroupListTask != null)
            fGroupListTask = null;
        fGroupListTask = new FGroupListTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null)
                {
                    GlobalVars.showErrAlert(FriendGroupActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                myFriendGroup = result.getJSONArray("my_friend");
                yourFriendGroup = result.getJSONArray("your_friend");
                // refresh data
                yourAdpater.myList = yourFriendGroup;
                myAdapter.myList = myFriendGroup;
                yourAdpater.notifyDataSetChanged();
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case R.id.btn_create_group:                       // 친구계창설인 경우
                    this.getFgroupInfo();
                    break;
            }
        }
    }
}
