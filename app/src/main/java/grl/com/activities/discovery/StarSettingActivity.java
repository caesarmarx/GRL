package grl.com.activities.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;

import org.json.JSONException;

import grl.com.adapters.discovery.StarSettingAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.planet.NinePlanetsListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class StarSettingActivity extends Activity implements View.OnClickListener {

    // view
    RecyclerView recyclerView;
    StarSettingAdapter recyclerAdapter;

    // task
    NinePlanetsListTask ninePlanetsListTask;

    // response
    JsonArray ninePlanets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_setting);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getDataFromIntent () {
        Intent intent = this.getIntent();
    }

    public void getViewByID () {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.setting_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.user_star_title));
    }

    public void initializeData () {
        // init value
        ninePlanets = new JsonArray();

        // set up Recycler Adapter
        recyclerAdapter = new StarSettingAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        getPlanetLst();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되돌이 단추를 누르는 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    // 귀성설정정보를 얻어오기
    public void getPlanetLst () {
        ninePlanetsListTask = new NinePlanetsListTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(StarSettingActivity.this);
                    return;
                }
                ninePlanets = (JsonArray) Response;
                recyclerAdapter.notifyData(ninePlanets);
            }
        });
    }

    public void refreshData () {
        this.initializeData();
    }
}
