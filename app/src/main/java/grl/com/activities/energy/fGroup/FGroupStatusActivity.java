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

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.adapters.energy.fgroup.FGroupEnergyAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.friendGroup.FGroupEnergyTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class FGroupStatusActivity extends Activity implements View.OnClickListener {

    // views
    private RecyclerView recyclerView;

    // tasks
    FGroupEnergyTask fGroupEnergyTask;

    // response
    String featureEnergy;
    JSONArray members;

    // required
    String strTitle;
    String strBackTitle;
    String groupID;

    // values
    FGroupEnergyAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_ability);

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
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        // init data;
        featureEnergy = "";
        members = new JSONArray();

        // set up recyclerView
        recyclerAdapter = new FGroupEnergyAdapter(this);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManagerTop);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // send request
        fGroupEnergyTask = new FGroupEnergyTask(this, SelfInfoModel.userID, groupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FGroupStatusActivity.this);
                    return;
                }
                // parsing data
                JSONObject result = (JSONObject) Response;
                featureEnergy = result.getString("energy_feature");
                members = result.getJSONArray("members");
                recyclerAdapter.notifyData(members);
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.tv_fgroup_feature).setOnClickListener(this);
        findViewById(R.id.tv_fgroup_map).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.tv_fgroup_feature:            // 친구계 특성보기
                Utils.start_Activity(FGroupStatusActivity.this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_ability_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_feature_title)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, this.featureEnergy));
                break;
            case R.id.tv_fgroup_map:                // 친구계 세력지도보기
                String strMembers = this.members.toString();
                Utils.start_Activity(this, FGroupMapActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.fgroup_ability_title)),
                        new BasicNameValuePair(Constant.TITLE_ID, getString(R.string.fgroup_map_title)),
                        new BasicNameValuePair("members", strMembers));
                break;
        }
    }

}
