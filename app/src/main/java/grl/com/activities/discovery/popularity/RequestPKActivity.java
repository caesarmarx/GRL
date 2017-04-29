package grl.com.activities.discovery.popularity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.order.UserListAdapter;
import grl.com.adapters.popularity.PopularPKAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.popularity.ExampleListTask;
import grl.com.httpRequestTask.popularity.PKListTask;
import grl.com.httpRequestTask.popularity.PKRequestTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class RequestPKActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    RecyclerView requestListView;
    UserListAdapter requestAdapter;
    EditText phoneField;
    Button requestBtn;

    String userId;
    String estName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_pk);

        Intent intent = getIntent();
        userId = SelfInfoModel.userID;
        estName = intent.getStringExtra("est_name");

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        requestListView = (RecyclerView)findViewById(R.id.popular_pk_list);
        phoneField = (EditText)findViewById(R.id.et_phone_number);
        requestBtn = (Button)findViewById(R.id.btn_request_pk);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        requestBtn.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.menu_example_list));
    }

    public void initializeData () {
        requestAdapter = new UserListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        requestListView.setLayoutManager(mLayoutManager);
        requestListView.setItemAnimator(new DefaultItemAnimator());
        requestListView.setAdapter(requestAdapter);
        refresh();
    }

    public void refresh() {
        requestAdapter.myList.clear();;
        requestAdapter.notifyDataSetChanged();
        new PKListTask(this, userId, estName, 0, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(RequestPKActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        UserModel model = new UserModel();
                        model.parseFromJson(object);
                        requestAdapter.myList.add(model);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                requestAdapter.notifyDataSetChanged();
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
            case R.id.btn_request_pk:
                requestPkAction();
                break;
        }
    }

    public void requestPkAction() {
        String phoneNumber = phoneField.getText().toString();
        new PKRequestTask(this, userId, phoneNumber, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(RequestPKActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        refresh();
//                        finish();
                    } else {
                        GlobalVars.showErrAlert(RequestPKActivity.this);
                    }

                } catch (Exception e) {

                }
                requestAdapter.notifyDataSetChanged();
            }
        });
    }
}
