package grl.com.activities.discovery.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.order.MyOrderAdapter;
import grl.com.adapters.order.MySolveAdapter;
import grl.com.adapters.order.NewOrderAdapter;
import grl.com.adapters.order.TGroupOrderAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.MyOrderTask;
import grl.com.httpRequestTask.order.MySolveTask;
import grl.com.httpRequestTask.order.RealTimeTask;
import grl.com.httpRequestTask.order.TGroupOrderTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class MyOrderActivity extends Activity implements View.OnClickListener {

    private ImageView userPhotoView;

    private Integer menuIndex = 0;

    private RadioButton newOrderBtn;
    private RadioButton tgroupOrderBtn;
    private RadioButton myOrderBtn;
    private RadioButton mySolveBtn;

    private RecyclerView orderListView;

    private NewOrderAdapter newOrderAdapter;
    private TGroupOrderAdapter tgroupOrderAdapter;
    private MyOrderAdapter myOrderAdapter;
    private MySolveAdapter mySolveAdapter;

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        initNavBar();
        getViewByID();
        setOnListener();
        initializeData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void getViewByID () {
        newOrderBtn = (RadioButton)findViewById(R.id.btn_new_order);
        tgroupOrderBtn = (RadioButton)findViewById(R.id.btn_tgroup_order);
        myOrderBtn = (RadioButton)findViewById(R.id.btn_my_order);
        mySolveBtn = (RadioButton)findViewById(R.id.btn_my_solution);
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        newOrderBtn.setOnClickListener(this);
        tgroupOrderBtn.setOnClickListener(this);
        myOrderBtn.setOnClickListener(this);
        mySolveBtn.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_title));
    }

    public void initializeData () {
        GlobalVars.loadImage(userPhotoView, SelfInfoModel.userPhoto);
        // set up Adapter
        orderListView = (RecyclerView)findViewById(R.id.order_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        orderListView.setLayoutManager(mLayoutManager);
        orderListView.setItemAnimator(new DefaultItemAnimator());

        newOrderAdapter = new NewOrderAdapter(this);
        tgroupOrderAdapter = new TGroupOrderAdapter(this);
        myOrderAdapter = new MyOrderAdapter(this);
        myOrderAdapter.setItemOnClickListener(myOrderClickListener);
        mySolveAdapter = new MySolveAdapter(this);

    }

    public void refresh() {
        if (menuIndex == 0) {
            orderListView.setAdapter(newOrderAdapter);
            newOrderAdapter.myList.clear();
            newOrderAdapter.notifyDataSetChanged();
            new RealTimeTask(this, SelfInfoModel.userID, -1, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(MyOrderActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();


                    for (int i =0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);

                        OrderModel orderModel = new OrderModel();
                        orderModel.parseFromJson(object.getJSONObject("order"));
                        OrderContentModel contentModel = new OrderContentModel();
                        contentModel.parseFromJson(object.getJSONObject("ord_content"));
                        UserModel userModel = new UserModel();
                        userModel.parseFromJson(object.getJSONObject("user_info"));

                        OrderEntireModel entireModel = new OrderEntireModel();
                        entireModel.orderModel = orderModel;
                        entireModel.userModel = userModel;
                        entireModel.contentModel = contentModel;
                        newOrderAdapter.myList.add(entireModel);
                    }
                    newOrderAdapter.notifyDataSetChanged();


                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                }
            });
        }
        if (menuIndex == 1) {
            orderListView.setAdapter(tgroupOrderAdapter);
            tgroupOrderAdapter.myList.clear();
            tgroupOrderAdapter.notifyDataSetChanged();
            new TGroupOrderTask(this, SelfInfoModel.userID, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(MyOrderActivity.this);
                        return;
                    }

                    try {
                        JSONArray result = (JSONArray) response;

                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();


                        for (int i =0; i < result.length(); i++) {
                            JSONObject object = result.getJSONObject(i);

                            OrderModel orderModel = new OrderModel();
                            orderModel.parseFromJson(object.getJSONObject("order"));
                            OrderContentModel contentModel = new OrderContentModel();
                            contentModel.parseFromJson(object.getJSONObject("ord_content"));
                            UserModel userModel = new UserModel();
                            userModel.parseFromJson(object.getJSONObject("user_info"));

                            OrderEntireModel entireModel = new OrderEntireModel();
                            entireModel.orderModel = orderModel;
                            entireModel.userModel = userModel;
                            entireModel.contentModel = contentModel;
                            tgroupOrderAdapter.myList.add(entireModel);
                        }
                        tgroupOrderAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("MYAPP", "exception", e);
                    }
                }
            });
        }
        if (menuIndex == 2) {
            orderListView.setAdapter(myOrderAdapter);
            myOrderAdapter.myList.clear();
            myOrderAdapter.notifyDataSetChanged();
            new MyOrderTask(this, SelfInfoModel.userID, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(MyOrderActivity.this);
                        return;
                    }

                    try {
                        JSONArray result = (JSONArray) response;

                        for (int i =0; i < result.length(); i++) {
                            JSONObject object = result.getJSONObject(i);
                            OrderModel orderModel = new OrderModel();
                            orderModel.parseFromJson(object);
                            myOrderAdapter.myList.add(orderModel);
                        }
                        myOrderAdapter.notifyDataSetChanged();
                        myOrderAdapter.setUserId(SelfInfoModel.userID);

                    } catch (Exception e) {
                        Log.e("MYAPP", "exception", e);
                    }
                }
            });
        }
        if (menuIndex == 3) {
            orderListView.setAdapter(mySolveAdapter);
            mySolveAdapter.myList.clear();
            mySolveAdapter.notifyDataSetChanged();
            new MySolveTask(this, SelfInfoModel.userID, new HttpCallback() {
                @Override
                public void onResponse(Boolean flag, Object response) {
                    if(!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(MyOrderActivity.this);
                        return;
                    }

                    try {
                        JSONArray result = (JSONArray) response;

                        for (int i =0; i < result.length(); i++) {
                            JSONObject object = result.getJSONObject(i);
                            SolveModel solveModel = new SolveModel();
                            solveModel.parseFromJson(object);
                            mySolveAdapter.myList.add(solveModel);
                        }
                        mySolveAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("MYAPP", "exception", e);
                    }
                }
            });
        }
    }

    private MyOrderAdapter.OnItemOnClickListener myOrderClickListener = new MyOrderAdapter.OnItemOnClickListener() {
        @Override
        public void onItemClick(int position) {
            OrderModel model = myOrderAdapter.myList.get(position);
            Utils.start_Activity(MyOrderActivity.this, MyOrderDetailActivity.class,
                    new BasicNameValuePair("user_id", SelfInfoModel.userID),
                    new BasicNameValuePair("content_id",model.contentId));
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_new_order:
                menuIndex = 0;
                refresh();
                break;
            case R.id.btn_tgroup_order:
                menuIndex = 1;
                refresh();
                break;
            case R.id.btn_my_order:
                menuIndex = 2;
                refresh();
                break;
            case R.id.btn_my_solution:
                menuIndex = 3;
                refresh();
                break;
        }
    }
}
