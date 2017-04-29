package grl.com.activities.discovery.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChatUserModel;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.GetAccpetUserTask;
import grl.com.httpRequestTask.order.OrderResendTask;
import grl.com.httpRequestTask.order.OrderSolveTask;
import grl.com.httpRequestTask.order.TGroupOrderDetailTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.view.GroupChatView;
import grl.wangu.com.grl.R;

public class DiscoverOrderSolveActivity extends Activity implements View.OnClickListener {

    ImageView userPhotoView;
    TextView userNameView;
    TextView distanceView;
    TextView orderBudgetView;
    TextView orderDateView;
    TextView orderContentView;

    EditText orderSolveEdit;
    Button solveBtn;

    String userId;
    String orderId;
    Boolean bTGroupOrder = false;

    Button resendBtn;

    RelativeLayout solveTabView;
    RecyclerView userListView;
    UserListAdapter userListAdapter;

    OrderEntireModel orderData;
    int tabIndex = 0;

    GroupChatView chatView;
    ArrayList<ChatUserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_order_solve);
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        orderId = intent.getStringExtra("order_id");
        if (intent.hasExtra("bTGroupOrder")) {
            bTGroupOrder = Boolean.parseBoolean(intent.getStringExtra("bTGroupOrder"));
        }

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        userNameView = (TextView)findViewById(R.id.tv_user_name);
        orderBudgetView = (TextView)findViewById(R.id.tv_order_budget);
        distanceView = (TextView)findViewById(R.id.tv_distance);
        orderContentView = (TextView)findViewById(R.id.tv_order_content);
        orderDateView = (TextView)findViewById(R.id.tv_order_date);
        orderSolveEdit = (EditText)findViewById(R.id.et_order_solve);

        solveBtn = (Button)findViewById(R.id.btn_order_solve);
        resendBtn = (Button)findViewById(R.id.btn_order_resend);

        solveTabView = (RelativeLayout)findViewById(R.id.relayout_solve_view);
        userListView = (RecyclerView)findViewById(R.id.user_list);

        chatView = (GroupChatView)findViewById(R.id.group_chat_view);
        chatView.GROUP_TYPE = Constant.CHAT_TGROUP;

    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.img_user_photo).setOnClickListener(this);

        findViewById(R.id.btn_order_solve).setOnClickListener(this);
        findViewById(R.id.btn_order_resend).setOnClickListener(this);
        findViewById(R.id.btn_solve_tab).setOnClickListener(this);
        findViewById(R.id.btn_chat_tab).setOnClickListener(this);
        findViewById(R.id.btn_list_tab).setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_solve));
//        ((TextView)findViewById(R.id.txt_title)).setText("");
    }

    public void initializeData () {
        // set up Adapter
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        userListView.setLayoutManager(mLayoutManager);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListAdapter = new UserListAdapter(this);
        userListView.setAdapter(userListAdapter);

        refresh();
        orderData = new OrderEntireModel();

        userList = new ArrayList<ChatUserModel>();
    }

    public void refresh() {
        new TGroupOrderDetailTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
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

                    chatView.GROUP_ID = contentModel.contentId;

                } catch (Exception e) {

                }
                updateUI();
            }
        });
    }

    void updateUI() {

        GlobalVars.loadImage(userPhotoView, orderData.userModel.userPhoto);
        userNameView.setText(getResources().getString(R.string.user_name) + " : " + orderData.userModel.userName);
        String orderContent = String.format("%s+%s", orderData.contentModel.ordType, orderData.contentModel.ordContent);
        orderContentView.setText(orderContent);
        orderDateView.setText(GlobalVars.getDateStringFromLong(orderData.contentModel.timeStart, "yyyy-MM-dd"));
        orderBudgetView.setText(String.format("%s : %d", getResources().getString(R.string.order_budget),orderData.orderModel.orderBudget));

        solveTabView.setVisibility(View.GONE);
        userListView.setVisibility(View.GONE);
        chatView.setVisibility(View.GONE);

        if (tabIndex == 0) {
            solveTabView.setVisibility(View.VISIBLE);
            if (!bTGroupOrder) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 20, 0, 20);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                solveBtn.setLayoutParams(params);
                resendBtn.setVisibility(View.GONE);
            } else {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(20, 20, 20, 80);
//                solveBtn.setLayoutParams(params);
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                resendBtn.setVisibility(View.VISIBLE);
            }
        }
        if (tabIndex == 1) {
            chatView.setVisibility(View.VISIBLE);
            new GetAccpetUserTask(this, userId, orderData.contentModel.ord_content_id, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    userList.clear();
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                        return;
                    }

                    try {
                        JSONArray result = (JSONArray) response;
                        for (int i = 0; i < result.length(); i++ ) {
                            JSONObject object = result.getJSONObject(i);
                            ChatUserModel userModel = new ChatUserModel();
                            userModel.parseFromJson(object);
                            userList.add(userModel);
                        }
                    } catch (Exception e) {

                    }
                    chatView.setReceiptUsers(userList);
                }
            });
        }
        if (tabIndex == 2) {
            userListView.setVisibility(View.VISIBLE);
            userListAdapter.myList.clear();
            userListAdapter.notifyDataSetChanged();
            new GetAccpetUserTask(this, userId, orderData.contentModel.ord_content_id, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {

                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                        return;
                    }

                    try {
                        JSONArray result = (JSONArray) response;
                        for (int i = 0; i < result.length(); i++ ) {
                            JSONObject object = result.getJSONObject(i);
                            UserModel model = new UserModel();
                            model.parseFromJson(object);
                            userListAdapter.myList.add(model);
                        }
                    } catch (Exception e) {

                    }
                    userListAdapter.notifyDataSetChanged();
                }
            });
        }

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

            case R.id.btn_solve_tab:
                tabIndex = 0;
                updateUI();
                break;
            case R.id.btn_chat_tab:
                tabIndex = 1;
                updateUI();
                break;
            case R.id.btn_list_tab:
                tabIndex = 2;
                updateUI();
                break;
            case R.id.btn_order_solve:
                solveAction();
                break;
            case R.id.btn_order_resend:
                resendAction();
                break;
        }
    }

    public void solveAction() {
        String orderResult = orderSolveEdit.getText().toString();
        if (orderResult.isEmpty())
            return;
        new OrderSolveTask(this, userId, orderId, orderResult, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    GlobalVars.loadRealTimeOrder(false);
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                    }

                } catch (Exception e) {

                }
            }
        });
    }

    public void resendAction() {
        new OrderResendTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(DiscoverOrderSolveActivity.this);
                    }

                } catch (Exception e) {

                }
            }
        });
    }

}
