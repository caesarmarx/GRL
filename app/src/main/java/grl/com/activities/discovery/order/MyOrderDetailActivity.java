package grl.com.activities.discovery.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.adapters.order.MyOrderSolveAdapter;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.MyOrderDetailTask;
import grl.com.httpRequestTask.order.OrderAcceptTask;
import grl.com.httpRequestTask.order.TGroupOrderDetailTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class MyOrderDetailActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    TextView orderTypeView;
    TextView orderContentView;
    TextView orderDateView;
    TextView orderBudgetView;
    TextView orderStateView;

    LinearLayout orderChatLayout;
    LinearLayout orderStateLayout;
    LinearLayout orderReportLayout;

    RecyclerView solveListView;
    MyOrderSolveAdapter solveListAdapter;

    int orderStatus;

    String userId;
    String contentId;

    JSONObject orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        contentId = intent.getStringExtra("content_id");

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        orderTypeView = (TextView)findViewById(R.id.tv_order_type);
        orderContentView = (TextView)findViewById(R.id.tv_order_content);
        orderDateView = (TextView)findViewById(R.id.tv_order_date);
        orderBudgetView = (TextView)findViewById(R.id.tv_order_budget);
        orderStateView = (TextView)findViewById(R.id.tv_order_state);
        solveListView = (RecyclerView)findViewById(R.id.my_solution_list);

        orderChatLayout = (LinearLayout)findViewById(R.id.linear_order_chat);
        orderStateLayout = (LinearLayout)findViewById(R.id.linear_order_state);
        orderReportLayout = (LinearLayout)findViewById(R.id.linear_order_report);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        orderChatLayout.setOnClickListener(this);
        orderStateLayout.setOnClickListener(this);
        orderReportLayout.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.my_order_title));
    }

    public void initializeData () {
        // set up Adapter
        solveListAdapter = new MyOrderSolveAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        solveListView.setLayoutManager(mLayoutManager);
        solveListView.setItemAnimator(new DefaultItemAnimator());
        solveListView.setAdapter(solveListAdapter);
        refresh();
    }

    public void refresh() {
        solveListAdapter.myList.clear();;

        new MyOrderDetailTask(this, userId, contentId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(MyOrderDetailActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    orderData = result;
                    JSONArray jsonSolves = (JSONArray)result.getJSONArray("solution");
                    for (int i = 0; i < jsonSolves.length(); i ++) {
                        JSONObject object = jsonSolves.getJSONObject(i);
                        SolveModel model = new SolveModel();
                        model.parseFromJson(object);
                        model.orderType = GlobalVars.getStringFromJson(orderData, "ord_type");
                        model.orderContent = GlobalVars.getStringFromJson(orderData, "ord_content");
                        solveListAdapter.myList.add(model);
                    }
                } catch (Exception e) {

                }
                solveListAdapter.notifyDataSetChanged();
                updateUI();
            }
        });
    }

    void updateUI() {
        orderTypeView.setText(GlobalVars.getStringFromJson(orderData, "ord_type"));
        orderContentView.setText(GlobalVars.getStringFromJson(orderData, "ord_content"));
        orderDateView.setText(GlobalVars.getStringFromJson(orderData, "time_start"));
        orderBudgetView.setText(GlobalVars.getStringFromJson(orderData, "ord_budget"));

        orderStatus = GlobalVars.getIntFromJson(orderData, "order_status");
        if (orderStatus == 3) {
            orderStateView.setText(getResources().getString(R.string.solve_select));
        } else {
            orderStateView.setText("");
        }
        solveListAdapter.setContentStatus(orderStatus);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;

            case R.id.linear_order_chat:
                Utils.start_Activity(this, MyOrderChatActivity.class,
                        new BasicNameValuePair(Constant.ORDER_CONTENT_ID, contentId));
                break;
            case R.id.linear_order_state:
                Utils.start_Activity(this, OrderStateActivity.class,
                        new BasicNameValuePair(Constant.ORDER_CONTENT_ID, contentId));
                break;
            case R.id.linear_order_report:
                Utils.start_Activity(this, ReportActivity.class);
                break;
        }
    }

}
