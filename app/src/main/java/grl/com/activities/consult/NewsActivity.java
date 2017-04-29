package grl.com.activities.consult;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import grl.com.adapters.chat.GiftListAdapter;
import grl.com.adapters.chat.NewsListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.chat.GiftGetTask;
import grl.com.httpRequestTask.chat.NewsGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class NewsActivity extends Activity implements View.OnClickListener {

    private RecyclerView newsListView;
    private NewsListAdapter newsListAdapter;

    MessageModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

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
        newsListView = (RecyclerView) findViewById(R.id.news_list_view);
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
        newsListAdapter = new NewsListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        newsListView.setLayoutManager(mLayoutManager);
        newsListView.setItemAnimator(new DefaultItemAnimator());
        newsListView.setAdapter(newsListAdapter);

        new NewsGetTask(NewsActivity.this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(NewsActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    newsListAdapter.myList = result;
                } catch (Exception ex) {

                }
                newsListAdapter.notifyDataSetChanged();
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
