package grl.com.activities.consult;

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
import org.json.JSONObject;

import java.util.List;

import grl.com.adapters.chat.FavouriteListAdapter;
import grl.com.adapters.chat.GiftListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.dataModels.ConsultUserModel;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.chat.GiftGetTask;
import grl.com.httpRequestTask.chat.UserListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class GiftActivity extends Activity implements View.OnClickListener {

    private RecyclerView giftListView;
    private GiftListAdapter giftListAdapter;

    MessageModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);

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
        giftListView = (RecyclerView) findViewById(R.id.gift_list_view);
    }

    private void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.gift_list));
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.GONE);
    }

    private void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    private void initializeData() {
        // set up adapters
        giftListAdapter = new GiftListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        giftListView.setLayoutManager(mLayoutManager);
        giftListView.setItemAnimator(new DefaultItemAnimator());
        giftListView.setAdapter(giftListAdapter);

        new GiftGetTask(GiftActivity.this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(GiftActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    giftListAdapter.myList = result;
                } catch (Exception ex) {

                }
                giftListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되돌이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }
}
