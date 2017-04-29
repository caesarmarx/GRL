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

import grl.com.adapters.donate.DonateHistoryAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.discovery.donate.DonateAcceptedListTask;
import grl.com.httpRequestTask.discovery.donate.DonateCancelListTask;
import grl.com.httpRequestTask.discovery.donate.DonateConfirmListTask;
import grl.com.httpRequestTask.discovery.donate.GetUserPaidTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class Fragment_donate_history extends Fragment implements View.OnClickListener{
    Activity ctx;
    View layout;

    // Views
    RecyclerView recyclerView;
    TextView emptyAlert;

    // Tasks
    DonateCancelListTask donateCancelListTask;
    DonateAcceptedListTask donateAcceptedListTask;
    DonateConfirmListTask donateConfirmListTask;
    GetUserPaidTask getUserPaidTask;

    // Response
    JSONArray cancelResult;
    JSONArray confirmResult;
    JSONArray givenResult;
    JSONArray acceptedResult;
    // Values
    DonateHistoryAdapter recyclerAdapter;
    Integer type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_donate_history,
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
        recyclerView = (RecyclerView)layout.findViewById(R.id.recycler_view);
        emptyAlert = (TextView)layout.findViewById(R.id.tvEmpty);
    }

    private void setOnListener() {
        // TODO Auto-generated method stub
        layout.findViewById(R.id.button1).setOnClickListener(this);
        layout.findViewById(R.id.button2).setOnClickListener(this);
        layout.findViewById(R.id.button3).setOnClickListener(this);
        layout.findViewById(R.id.button4).setOnClickListener(this);
    }

    private void initData() {
        // TODO Auto-generated method stub
        recyclerAdapter = new DonateHistoryAdapter(this.ctx, 0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.ctx.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        type = 0;
        getDataFromServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                type = 0;
                break;
            case R.id.button2:
                type = 1;
                break;
            case R.id.button3:
                type = 2;
                break;
            case R.id.button4:
                type = 3;
                break;
        }
        getDataFromServer();
    }

    public void getDataFromServer () {
        switch (type) {
            case 0:
                donateCancelListTask = new DonateCancelListTask(ctx, SelfInfoModel.userID, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object Response) throws JSONException {
                        if(!flag || Response == null) {
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                        cancelResult = (JSONArray) Response;
                        recyclerAdapter = new DonateHistoryAdapter(ctx, 0);
                        recyclerAdapter.myList = cancelResult;
                        recyclerView.setAdapter(recyclerAdapter);

                        if(cancelResult.length() > 0)
                            emptyAlert.setVisibility(View.VISIBLE);
                        else
                            emptyAlert.setVisibility(View.GONE);
                    }
                });
                break;
            case 1:
                donateConfirmListTask = new DonateConfirmListTask(ctx, SelfInfoModel.userID, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object Response) throws JSONException {
                        if(!flag || Response == null) {
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                        confirmResult = (JSONArray) Response;
                        recyclerAdapter = new DonateHistoryAdapter(ctx, 1);
                        recyclerAdapter.myList = confirmResult;
                        recyclerView.setAdapter(recyclerAdapter);

                        if(cancelResult.length() > 0)
                            emptyAlert.setVisibility(View.VISIBLE);
                        else
                            emptyAlert.setVisibility(View.GONE);
                    }
                });
                break;
            case 2:
                getUserPaidTask = new GetUserPaidTask(ctx, SelfInfoModel.userID, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object Response) throws JSONException {
                        if(!flag || Response == null) {
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                        givenResult = (JSONArray) Response;
                        recyclerAdapter = new DonateHistoryAdapter(ctx, 2);
                        recyclerAdapter.myList = givenResult;
                        recyclerView.setAdapter(recyclerAdapter);

                        if(cancelResult.length() > 0)
                            emptyAlert.setVisibility(View.VISIBLE);
                        else
                            emptyAlert.setVisibility(View.GONE);
                    }
                });
                break;
            case 3:
                donateAcceptedListTask = new DonateAcceptedListTask(ctx, SelfInfoModel.userID, new HttpCallback() {
                    @Override
                    public void onResponse(Boolean flag, Object Response) throws JSONException {
                        if(!flag || Response == null) {
                            GlobalVars.showErrAlert(ctx);
                            return;
                        }
                        acceptedResult = (JSONArray) Response;
                        recyclerAdapter = new DonateHistoryAdapter(ctx, 3);
                        recyclerAdapter.myList = acceptedResult;
                        recyclerView.setAdapter(recyclerAdapter);

                        if(cancelResult.length() > 0)
                            emptyAlert.setVisibility(View.VISIBLE);
                        else
                            emptyAlert.setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
}
