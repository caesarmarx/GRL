package grl.com.activities.search;

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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.adapters.order.GPSListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.user.GPSSearchTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.DirectionDialog;
import grl.wangu.com.grl.R;


/**
 * Created by Administrator on 6/7/2016.
 */
public class GpsSearchActivity extends Activity implements View.OnClickListener{

    private EditText minDistanceEdit;
    private EditText maxDistanceEdit;
    private Button directionBtn;
    private RecyclerView userListView;
    private GPSListAdapter userListAdapter;

    private int minDistance = 0;
    private int maxDistance = 0;
    private int direct = -1;
    private int start = 0;
    private int limit = 100;

    private final int DIRECTION_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_search);

        initNavBar();
        getViewByID();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        userListView = (RecyclerView)findViewById(R.id.user_gps_list);

        minDistanceEdit = (EditText)findViewById(R.id.et_min_distance);
        maxDistanceEdit = (EditText)findViewById(R.id.et_max_distance);

        directionBtn = (Button)findViewById(R.id.btn_direction);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.btn_search).setOnClickListener(this);
        directionBtn.setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.gps_search_title));
    }

    public void initializeData () {
        // set up Adapter
        userListAdapter = new GPSListAdapter(this);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        userListView.setLayoutManager(mLayoutManagerTop);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);
        userListAdapter.showNumber();

    }

    public void refresh() {
        minDistance = GlobalVars.getIntFromString(minDistanceEdit.getText().toString());
        maxDistance = GlobalVars.getIntFromString(maxDistanceEdit.getText().toString());

        new GPSSearchTask(this, minDistance, maxDistance, direct, start, limit, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(GpsSearchActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    userListAdapter.myList.clear();

                    for (int i =0; i < result.length(); i++) {
                        JSONObject ojbect = result.getJSONObject(i);
                        UserModel userModel = gson.fromJson(String.valueOf(ojbect), UserModel.class);
                        userModel.state = GlobalVars.getDistanceString(userModel.latitude, userModel.longitude,
                                SelfInfoModel.latitude, SelfInfoModel.longitude);
                        userListAdapter.myList.add(userModel);
                    }
                    userListAdapter.notifyDataSetChanged();


                } catch (Exception e) {

                }
            }
        });

    }

    public void showDirection() {
        Intent intent = new Intent(this, DirectionDialog.class);
        intent.putExtra("Direct", direct);
        startActivityForResult(intent, DIRECTION_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_search:
                refresh();
                break;
            case R.id.btn_direction:
                showDirection();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            direct = data.getIntExtra(Constant.ORDER_DIRECTION, -1);
        }
    }
}
