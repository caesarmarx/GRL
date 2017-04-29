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

import org.json.JSONObject;

import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.OrderAcceptTask;
import grl.com.httpRequestTask.order.SolveSelectTask;
import grl.com.httpRequestTask.order.TGroupOrderDetailTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class SolutionSelectActivity extends Activity implements View.OnClickListener {

    ImageView userPhotoView;
    TextView userNameView;

    TextView orderTypeView;
    TextView orderContentView;
    TextView orderBudgetView;
    TextView orderSolveView;

    Button selectBtn;

    String userId;
    String userName;
    String userPhoto;
    String orderId;
    String contentId;
    String orderType;
    String orderContent;
    String orderDate;
    String orderBudget;
    String orderResult;

    int orderStatus;

    int contentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution_select);

        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        userName = intent.getStringExtra("user_name");
        userPhoto = intent.getStringExtra("user_photo");
        orderId = intent.getStringExtra("order_id");
        contentId = intent.getStringExtra("content_id");
        contentStatus = GlobalVars.getIntFromString(intent.getStringExtra("content_status"));  // 기본령상태 선택된 해령이 있는가 ?
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
        userNameView = (TextView)findViewById(R.id.tv_user_name);
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        orderTypeView = (TextView)findViewById(R.id.tv_order_type);
        orderContentView = (TextView)findViewById(R.id.tv_order_content);
        orderSolveView = (TextView)findViewById(R.id.tv_order_result);
        orderBudgetView = (TextView)findViewById(R.id.tv_order_budget);
        selectBtn = (Button)findViewById(R.id.btn_solution_select);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        selectBtn.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.solution_select));
    }

    public void initializeData () {
        userNameView.setText(userName);
        GlobalVars.loadImage(userPhotoView, userPhoto);
        orderTypeView.setText(orderType);
        orderContentView.setText(orderContent);
        orderSolveView.setText(orderResult);
        orderBudgetView.setText(orderBudget);
        if (contentStatus == OrderModel.ORDER_SELECT_STATE || contentStatus == OrderModel.ORDER_CANCEL_STATE) {
            // 금낭이 선택된 경우
            selectBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void selectSolution() {
        new SolveSelectTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
            if(!flag || response == null) {                 //failure
                GlobalVars.showErrAlert(SolutionSelectActivity.this);
                return;
            }

            try {
                JSONObject result = (JSONObject) response;
                Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                if (success) {
                    finish();
                } else {
                    GlobalVars.showErrAlert(SolutionSelectActivity.this);
                }

            } catch (Exception e) {

            }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_solution_select:
                selectSolution();
                break;

        }
    }

}
