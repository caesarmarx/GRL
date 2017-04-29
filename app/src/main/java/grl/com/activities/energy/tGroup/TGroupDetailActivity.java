package grl.com.activities.energy.tGroup;

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

import grl.com.adapters.energy.TGroupDetailAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/17/2016.
 */
public class TGroupDetailActivity extends Activity implements  View.OnClickListener{

    RecyclerView recyclerView;
    TGroupDetailAdapter recyclerAdapter;

    // require
    JSONArray arrayData;
    String strTitle;
    public String strType;
    String strBackTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgroup_detail);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        this.strTitle = intent.getStringExtra(Constant.TITLE_ID);
        this.strType = intent.getStringExtra("type");
        if(strType != null) {
            strBackTitle = "";
        } else {
            strBackTitle = getString(R.string.tab_energy_title);
        }
        try {
            this.arrayData = new JSONArray();
            this.arrayData = new JSONArray(intent.getStringExtra("arrayData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getViewByID () {
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle );
    }

    public void initializeData () {
        // setup recycler adapter
        this.recyclerAdapter = new TGroupDetailAdapter(this);
        this.recyclerAdapter.myList = this.arrayData;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.txt_left:                     // 취소 단추를 누른 경우
            case R.id.img_back:
                Utils.finish(this);
                break;
        }
    }

}
