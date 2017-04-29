package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.fgroup.FGroupResAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/8/2016.
 */
public class FGroupResActivity extends Activity implements View.OnClickListener {

    // view
    RecyclerView recyclerView;

    // value
    FGroupResAdapter recyclerAdapter;
    public static final int CHAMBER_REQ = 0;
    public static final int CLUB_REQ = 1;
    public static final int FELLOW_REQ = 2;
    public static final int ALUMNI_REQ = 3;
    public static final int COLLEAGUE_REQ = 4;
    public static final int FRIEND_REQ = 5;
    public static final int INTEREST_REQ = 6;

    // require
    JSONObject userInfo;
    String strBackTitle;
    String strTitle;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_res);

        getParamsFromIntent();
        getViewByIDs();
        initNavBar();
        initializeData();
        setOnListener();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        String data = intent.getStringExtra("data");
        try {
            userInfo = new JSONObject(data);
            userID = userInfo.getJSONObject("member_id").getString("$id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getViewByIDs () {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initNavBar () {
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        // set up recycler view
        recyclerAdapter = new FGroupResAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // init view
        recyclerAdapter.notifyData(userInfo);

    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    public void returnValues () {
        Intent intent = new Intent();
        if(!userID.equals(SelfInfoModel.userID)) {
            setResult(RESULT_CANCELED, intent);
        } else
        {
            intent.putExtra("data", recyclerAdapter.getData().toString());
            setResult(RESULT_OK, intent);
        }
        Utils.finish(this);
    }

    @Override
    public void onBackPressed() {
        returnValues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                             // 되돌이 단추를 누른 경우
            case R.id.img_back:
                returnValues();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CHAMBER_REQ:
            case CLUB_REQ:
            case FELLOW_REQ:
            case ALUMNI_REQ:
            case COLLEAGUE_REQ:
            case FRIEND_REQ:
            case INTEREST_REQ:
                String str = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                recyclerAdapter.updateData(str, requestCode);
                break;
        }
    }
}
