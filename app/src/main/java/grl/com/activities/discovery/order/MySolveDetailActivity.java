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

import org.json.JSONObject;

import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.OrderAcceptTask;
import grl.com.httpRequestTask.order.TGroupOrderDetailTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class MySolveDetailActivity extends Activity implements View.OnClickListener {

    TextView orderTypeView;
    TextView orderContentView;
    TextView orderDateView;
    TextView orderBudgetView;
    TextView orderSolveView;
    TextView orderStateView;

    LinearLayout orderStateLayout;
    LinearLayout orderReportLayout;

    String userId;
    String contentId;
    String orderType;
    String orderContent;
    String orderDate;
    String orderBudget;
    String orderResult;

    int orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_solve_detail);

        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        contentId = intent.getStringExtra("content_id");
        orderType = intent.getStringExtra("ord_type");
        orderContent = intent.getStringExtra("ord_content");
        orderResult = intent.getStringExtra("order_result");
        orderDate = intent.getStringExtra("time_start");
        orderBudget = intent.getStringExtra("ord_budget");
        orderStatus = GlobalVars.getIntFromString(intent.getStringExtra("order_status"));

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        orderTypeView = (TextView)findViewById(R.id.tv_order_type);
        orderContentView = (TextView)findViewById(R.id.tv_order_content);
        orderSolveView = (TextView)findViewById(R.id.tv_order_result);
        orderDateView = (TextView)findViewById(R.id.tv_order_date);
        orderBudgetView = (TextView)findViewById(R.id.tv_order_budget);
        orderStateView = (TextView)findViewById(R.id.tv_order_state);

        orderStateLayout = (LinearLayout)findViewById(R.id.linear_order_state);
        orderReportLayout = (LinearLayout)findViewById(R.id.linear_order_report);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        orderStateLayout.setOnClickListener(this);
        orderReportLayout.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.my_solution_menu));
    }

    public void initializeData () {
        // set up Adapter
        orderTypeView.setText(orderType);
        orderContentView.setText(orderContent);
        orderSolveView.setText(orderResult);
        orderDateView.setText(orderDate);
        orderBudgetView.setText(orderBudget);

        if (orderStatus == OrderModel.ORDER_SELECT_STATE) {
            orderStateView.setText(getResources().getString(R.string.solve_select));
        }
        if (orderStatus == OrderModel.ORDER_CANCEL_STATE) {
            orderStateView.setText(getResources().getString(R.string.solve_cancel));
        }
        if (orderStatus == OrderModel.ORDER_SOLVE_STATE) {
            orderStateView.setText(getResources().getString(R.string.solve_wait));
        }

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
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
