package grl.com.activities.discovery.popularity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.popularity.ExampleListTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class ExampleActivity extends Activity implements View.OnClickListener {

    RecyclerView exampleListView;
    UserListAdapter userListAdapter;
    String userId;
    String estName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Intent intent = getIntent();
        estName = intent.getStringExtra("est_name");
        userId = SelfInfoModel.userID;

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        exampleListView = (RecyclerView)findViewById(R.id.example_list);
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
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.menu_example_list));
    }

    public void initializeData () {
        userListAdapter = new UserListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        exampleListView.setLayoutManager(mLayoutManager);
        exampleListView.setItemAnimator(new DefaultItemAnimator());
        exampleListView.setAdapter(userListAdapter);
        refresh();
    }

    public void refresh() {
        userListAdapter.myList.clear();;
        userListAdapter.notifyDataSetChanged();
        new ExampleListTask(this, userId, estName, -1, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ExampleActivity.this);
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
                        if (GlobalVars.getIntFromJson(object, "state") == 1) {
                            model.state = "接受";
                        } else {
                            model.state = "等候";
                        }
                        userListAdapter.myList.add(model);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                userListAdapter.notifyDataSetChanged();
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
        }
    }
}
