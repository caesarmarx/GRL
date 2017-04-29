package grl.com.activities.discovery.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.order.ReportAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.order.ReportReasonTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class ReportActivity extends Activity implements View.OnClickListener{

    ImageView imgBack;
    TextView textBack;

    RecyclerView reportListView;
    ReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_report);
        initView();
        initData();
    }

    private void initView() {
        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_left);
        textBack.setText(getResources().getText(R.string.user_nav_back));
        imgBack.setVisibility(View.VISIBLE);
        textBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_report));

        findViewById(R.id.txt_right).setOnClickListener(this);
    }
    private void initData() {
        reportListView = (RecyclerView)findViewById(R.id.reason_list_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        reportListView.setLayoutManager(mLayoutManager);
        reportListView.setItemAnimator(new DefaultItemAnimator());

        reportAdapter = new ReportAdapter(ReportActivity.this);
        reportListView.setAdapter(reportAdapter);

        new ReportReasonTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ReportActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;

                    reportAdapter.myList.clear();
                    for (int i =0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        String value = GlobalVars.getStringFromJson(object, "value");
                        reportAdapter.myList.add(value);
                    }


                } catch (Exception e) {

                }
                reportAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:
                Utils.finish(this);
                break;
        }
    }
}
