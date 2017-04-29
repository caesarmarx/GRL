package grl.com.activities.energy;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.energy.OrderGroupUserSectionAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.orderPlate.OrderPlateGroupTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by Administrator on 6/8/2016.
 */
public class OrderGroupPlateActivity extends Activity implements View.OnClickListener{

    // views
    private SectionedRecyclerViewAdapter sectionAdapter;
    private RecyclerView recyclerView;
    private OrderGroupUserSectionAdapter firstAdapter;
    private OrderGroupUserSectionAdapter secondAdapter;
    private OrderGroupUserSectionAdapter thirdAdapter;
    private ImageView imgPlateLogo;
    private TextView tvPlateTitle;
    private TextView tvPlateAmount;
    private TextView tvPlateAbility;
    private TextView tvPlateAvg;

    // tasks
    OrderPlateGroupTask orderPlateGroupTask;

    // Response values
    private String plateName;
    private String plateLogo;
    private Integer plateAmount;
    private Integer plateAbility;
    private Float plateAvg;
    private JSONArray groupOne;
    private JSONArray groupTwo;
    private JSONArray groupThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_group);

        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnClickListener();
    }

    public void getViewByID() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imgPlateLogo = (ImageView) findViewById(R.id.img_order_plate);
        tvPlateTitle = (TextView) findViewById(R.id.txt_order_title);
        tvPlateAvg = (TextView) findViewById(R.id.txt_power);
        tvPlateAmount = (TextView) findViewById(R.id.txt_order_cnt);
        tvPlateAbility = (TextView) findViewById(R.id.txt_assist_cnt);
    }
    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.tab_energy_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.order_plate_title));
    }

    public void initializeData() {
        // create an instance of sectionrecylcerview
        sectionAdapter = new SectionedRecyclerViewAdapter();
        firstAdapter = new OrderGroupUserSectionAdapter(this, 0);
        secondAdapter = new OrderGroupUserSectionAdapter(this, 1);
        thirdAdapter = new OrderGroupUserSectionAdapter(this, 2);
        // add sections
        sectionAdapter.addSection(firstAdapter);
        sectionAdapter.addSection(secondAdapter);
        sectionAdapter.addSection(thirdAdapter);
        // set up your recyclerview with sectinedrecyclerviewAdapter
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(sectionAdapter);

        // init values
        this.plateName = "";
        this.plateLogo = "";
        this.plateAmount = 0;
        this.plateAbility = 0;
        this.plateAvg = 0.0f;
        this.groupOne = new JSONArray();
        this.groupTwo = new JSONArray();
        this.groupThree = new JSONArray();

        // get data from server
        orderPlateGroupTask = new OrderPlateGroupTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OrderGroupPlateActivity.this);
                    return;
                }

                JSONObject result = (JSONObject) Response;
                plateName = result.getString("plate_name");
                plateLogo = result.getString("plate_logo");
                plateAmount = result.getInt("plate_amount");
                plateAbility = result.getInt("plate_ability");
                plateAvg = (float) result.getDouble("plate_avg");
                groupOne = result.getJSONArray("group_one");
                groupTwo = result.getJSONArray("group_two");
                groupThree = result.getJSONArray("group_three");

                refreshView();
            }
        });
    }

    public void setOnClickListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    public void refreshView() {
        // update views
        GlobalVars.loadImage(imgPlateLogo, plateLogo);
        tvPlateTitle.setText(plateName);
        tvPlateAmount.setText(plateAmount.toString());
        tvPlateAbility.setText(plateAbility.toString());
        tvPlateAvg.setText(plateAvg.toString());
        // refresh recyclerView
        firstAdapter.myList = this.groupOne;
        secondAdapter.myList = this.groupTwo;
        thirdAdapter.myList = this.groupThree;
        sectionAdapter.notifyDataSetChanged();
    }
}
