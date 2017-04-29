package grl.com.activities.discovery.order;

import android.app.Activity;
import android.content.Intent;
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

import javax.microedition.khronos.opengles.GL;

import grl.com.adapters.order.MyOrderAdapter;
import grl.com.adapters.order.MySolveAdapter;
import grl.com.adapters.order.NewOrderAdapter;
import grl.com.adapters.order.TGroupOrderAdapter;
import grl.com.configuratoin.Constant;
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
import grl.com.httpRequestTask.order.OtherOrderDetailTask;
import grl.com.httpRequestTask.order.PlanetUserTask;
import grl.com.httpRequestTask.order.RealTimeTask;
import grl.com.httpRequestTask.order.TGroupOrderTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.star.PlanetShowView;
import grl.wangu.com.grl.R;

public class OtherOrderActivity extends Activity implements View.OnClickListener {

    private ImageView userPhotoView;

    private RecyclerView orderListView;

    private MyOrderAdapter myOrderAdapter;

    private PlanetShowView planetView;

    private ArrayList<UserModel> planetUsers;

    String userId = "";
    String userName = "";
    String userPhoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_order);

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constant.USER_ID);
        userName = intent.getStringExtra(Constant.USER_NAME);
        userPhoto= intent.getStringExtra(Constant.USER_PHOTO);

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
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        planetView = (PlanetShowView)findViewById(R.id.planet_view);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_order_title));
    }

    public void initializeData () {
        GlobalVars.loadImage(userPhotoView, userPhoto);
        // set up Adapter
        orderListView = (RecyclerView)findViewById(R.id.order_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        orderListView.setLayoutManager(mLayoutManager);
        orderListView.setItemAnimator(new DefaultItemAnimator());

        myOrderAdapter = new MyOrderAdapter(this);
        myOrderAdapter.setItemOnClickListener(myOrderClickListener);

        planetUsers = new ArrayList<UserModel>();
        planetView.initView();

        refresh();
        refreshPlanet();
    }

    private void refreshPlanet() {
        new PlanetUserTask(OtherOrderActivity.this, userId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OtherOrderActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    planetUsers.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        UserModel model = new UserModel();
                        model.parseFromJson(object);
                        model.state = "";
                        planetUsers.add(model);
                    }
                } catch (Exception ex) {

                }
                planetView.refreshData(planetUsers);
            }
        });
    }

    private void refresh() {
        if (userId == null || userId.isEmpty())
            return;
        orderListView.setAdapter(myOrderAdapter);
        myOrderAdapter.myList.clear();
        myOrderAdapter.notifyDataSetChanged();
        new MyOrderTask(this, userId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OtherOrderActivity.this);
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
    private MyOrderAdapter.OnItemOnClickListener myOrderClickListener = new MyOrderAdapter.OnItemOnClickListener() {
        @Override
        public void onItemClick(int position) {
        OrderModel model = myOrderAdapter.myList.get(position);
        final String contentId = model.contentId;
        new OtherOrderDetailTask(OtherOrderActivity.this, userId, model.contentId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OtherOrderActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    JSONObject jsonOrder = GlobalVars.getJSONObjectFromJson(result, "order");
                    if (jsonOrder == null || jsonOrder.length() == 0 ||
                        GlobalVars.getIntFromJson(jsonOrder, "order_status") == OrderModel.ORDER_INIT_STATE) {
                        // 점령
                        Utils.start_Activity(OtherOrderActivity.this, OtherOrderAcceptActivity.class,
                                new BasicNameValuePair(Constant.CONTENT_ID, contentId),
                                new BasicNameValuePair(Constant.USER_ID, userId));
                    } else {
                        // 해령
                        if (GlobalVars.getIntFromJson(jsonOrder, "order_status") == OrderModel.ORDER_ACCEPT_STATE ||
                                GlobalVars.getIntFromJson(jsonOrder, "order_status") == OrderModel.ORDER_DISCUSS_STATE) {
                            Utils.start_Activity(OtherOrderActivity.this, DiscoverOrderSolveActivity.class,
                                    new BasicNameValuePair(Constant.ORDER_ID, GlobalVars.getStringFromJson(jsonOrder, "order_id")),
                                    new BasicNameValuePair(Constant.USER_ID, userId));
                        }
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
        }
    };


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }
}
