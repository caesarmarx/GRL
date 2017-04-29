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
import android.widget.TextView;

import org.json.JSONObject;

import grl.com.adapters.order.UserListAdapter;
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

public class DiscoverOrderAcceptActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    ImageView userPhotoView;
    TextView userNameView;
    TextView userRelView;
    TextView orderTypeView;
    TextView orderContentView;
    TextView orderDateView;
    TextView orderBudgetView;

    String userId;
    String orderId;
    Boolean bTGroupOrder = false;
    Boolean isTeacher;

    Button acceptBtn;

    OrderEntireModel orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_order_accept);
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        orderId = intent.getStringExtra("order_id");
        if (intent.hasExtra("bTGroupOrder"))
            bTGroupOrder = intent.getBooleanExtra("bTGroupOrder" ,false);

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        userNameView = (TextView)findViewById(R.id.tv_user_name);
        userRelView = (TextView)findViewById(R.id.tv_user_relation);
        orderTypeView = (TextView)findViewById(R.id.tv_order_type);
        orderContentView = (TextView)findViewById(R.id.tv_order_content);
        orderDateView = (TextView)findViewById(R.id.tv_order_date);
        orderBudgetView = (TextView)findViewById(R.id.tv_order_budget);
        acceptBtn = (Button)findViewById(R.id.btn_order_accept);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.img_user_photo).setOnClickListener(this);
        findViewById(R.id.btn_order_accept).setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_accept));
//        ((TextView)findViewById(R.id.txt_title)).setText("");
    }

    public void initializeData () {
        // set up Adapter
        refresh();
        orderData = new OrderEntireModel();
    }

    public void refresh() {
        new TGroupOrderDetailTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverOrderAcceptActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    OrderModel orderModel = new OrderModel();
                    orderModel.parseFromJson(result.getJSONObject("order"));
                    OrderContentModel contentModel = new OrderContentModel();
                    contentModel.parseFromJson(result.getJSONObject("ord_content"));
                    UserModel userModel = new UserModel();
                    userModel.parseFromJson(result.getJSONObject("user_info"));

                    orderData.orderModel = orderModel;
                    orderData.userModel = userModel;
                    orderData.contentModel = contentModel;
                    if (bTGroupOrder) {
                        JSONObject jsonOrder = result.getJSONObject("order");
                        isTeacher = GlobalVars.getBooleanFromJson(jsonOrder, "is_teacher");
                    }
                } catch (Exception e) {

                }
                updateUI();
            }
        });
    }

    void updateUI() {
        GlobalVars.loadImage(userPhotoView, orderData.userModel.userPhoto);
        userNameView.setText(orderData.userModel.userName);

        orderTypeView.setText(orderData.contentModel.ordType);
        orderContentView.setText(orderData.contentModel.ordContent);
        if (bTGroupOrder) {
            userRelView.setVisibility(View.VISIBLE);
            if (isTeacher)
                userRelView.setText(getResources().getString(R.string.teacher));
            else
                userRelView.setText(getResources().getString(R.string.student));
        } else {
            userRelView.setVisibility(View.INVISIBLE);
        }
        orderDateView.setText(GlobalVars.getDateStringFromLong(orderData.contentModel.timeStart, "yyyy-MM-dd"));
        orderBudgetView.setText(String.format("%d", orderData.orderModel.orderBudget));
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;

            case R.id.img_user_photo:               // 3 인자창
                break;
            case R.id.btn_order_accept:
                acceptAction();
                break;
        }
    }

    public void acceptAction() {
        new OrderAcceptTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                 if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverOrderAcceptActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(DiscoverOrderAcceptActivity.this);
                    }

                } catch (Exception e) {

                }
            }
        });
    }

    public void discussAction() {

    }

}
