package grl.com.subViews.fragment.donate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.donate.DonateHelpAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.discovery.donate.DonateHelpCheckTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class Fragment_donate_help extends Fragment implements View.OnClickListener{
    Activity ctx;
    View layout;

    // views
    RecyclerView topRecyclerView;
    RecyclerView downRecyclerView;
    TextView topAlert;
    TextView downAlert;

    // tasks
    DonateHelpCheckTask donateHelpCheckTask;

    // values
    public static Fragment_donate_help self = null;
    DonateHelpAdapter topAdapter;
    DonateHelpAdapter downAdapter;
    public JSONArray acceptingList;
    public JSONArray requesting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_donate_help,
                    null);
            initViews();
            initData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }
    private void initViews() {
        // TODO Auto-generated method stub
        Fragment_donate_help.self = this;
        topRecyclerView = (RecyclerView) layout.findViewById(R.id.top_recycler_view);
        downRecyclerView = (RecyclerView) layout.findViewById(R.id.down_recycler_view);
        topAlert = (TextView) layout.findViewById(R.id.tvTopAlert);
        downAlert = (TextView) layout.findViewById(R.id.tvDownAlert);
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
    }

    private void initData() {
        // TODO Auto-generated method stub
        topAdapter = new DonateHelpAdapter(this.ctx, 0);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(this.ctx.getApplicationContext());
        topRecyclerView.setLayoutManager(mLayoutManagerTop);
        topRecyclerView.setItemAnimator(new DefaultItemAnimator());
        topRecyclerView.setAdapter(topAdapter);

        downAdapter = new DonateHelpAdapter(this.ctx, 1);
        RecyclerView.LayoutManager mLayoutManagerDown = new LinearLayoutManager(this.ctx.getApplicationContext());
        downRecyclerView.setLayoutManager(mLayoutManagerDown);
        downRecyclerView.setItemAnimator(new DefaultItemAnimator());
        downRecyclerView.setAdapter(downAdapter);

        // send request to server
        donateHelpCheckTask = new DonateHelpCheckTask(ctx, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                acceptingList = result.getJSONArray("accepting");
                requesting = result.getJSONArray("requesting");
                notifyRecycler();
            }
        });
    }

    public void notifyRecycler () {
        if(acceptingList.length() > 0) {
            topAlert.setVisibility(View.GONE);
        } else {
            topAlert.setVisibility(View.VISIBLE);
        }
        if(requesting.length() > 0) {
            downAlert.setVisibility(View.GONE);
        } else {
            downAlert.setVisibility(View.VISIBLE);
        }
        topAdapter.myList = acceptingList;
        downAdapter.myList = requesting;
        topAdapter.notifyDataSetChanged();
        downAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {

    }
}
