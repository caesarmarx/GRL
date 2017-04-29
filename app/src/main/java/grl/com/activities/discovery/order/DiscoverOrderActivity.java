package grl.com.activities.discovery.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.order.DiscoverOrderAdapter;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderEntireModel;
import grl.wangu.com.grl.R;

public class DiscoverOrderActivity extends Activity implements View.OnClickListener {

    private ImageView userPhotoView;
    private RecyclerView orderListView;
    private DiscoverOrderAdapter orderListAdapter;

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_order);

        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        orderListView = (RecyclerView)findViewById(R.id.discover_order_list);

        orderListAdapter = new DiscoverOrderAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        orderListView.setLayoutManager(mLayoutManager);
        orderListView.setItemAnimator(new DefaultItemAnimator());
        orderListView.setAdapter(orderListAdapter);

        initData();

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_left);
        textBack.setText(getResources().getText(R.string.user_nav_back));
        imgBack.setVisibility(View.VISIBLE);
        textBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);
    }

    private void initData() {
        List<OrderEntireModel> tempList = new ArrayList<OrderEntireModel>();

        OrderEntireModel model1 = new OrderEntireModel();
        model1.contentModel.ordContent = "Order";
        tempList.add(model1);

        OrderEntireModel model2 = new OrderEntireModel();
        model1.contentModel.ordContent = "Order";
        tempList.add(model2);

        OrderEntireModel model3 = new OrderEntireModel();
        model1.contentModel.ordContent = "Order";
        tempList.add(model3);

        OrderEntireModel model4 = new OrderEntireModel();
        model1.contentModel.ordContent = "Order";
        tempList.add(model4);

        this.orderListAdapter.myList = tempList;

        this.orderListAdapter.notifyDataSetChanged();
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
        }
    }
}
