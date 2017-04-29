package grl.com.activities.discovery.popularity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.order.PopularUserListAdapter;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.popularity.ExampleListTask;
import grl.com.httpRequestTask.popularity.ExampleRequestTask;
import grl.com.httpRequestTask.popularity.PopularUserRankTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class PopularUserRankActivity extends Activity implements View.OnClickListener {

    RecyclerView userListView;
    PopularUserListAdapter userListAdapter;
    String userId;
    String estName;

    int location;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_user);
        Intent intent = getIntent();
        estName = intent.getStringExtra("est_name");
        userId = SelfInfoModel.userID;

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        userListView = (RecyclerView)findViewById(R.id.user_list);
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
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.menu_popular_rank));
    }

    public void initializeData () {
        userListAdapter = new PopularUserListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        userListView.setLayoutManager(mLayoutManager);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);
        refresh();
    }

    public void refresh() {
        userListAdapter.myList.clear();;
        userListAdapter.notifyDataSetChanged();
        new PopularUserRankTask(this, userId, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularUserRankActivity.this);
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

    public void showPositonAlert (final int index) {
        CharSequence[] array = {"一位置", "二位置", "三位置","四位置"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        position = 0;
                        location = index;
                        break;
                    case 1:
                        position = 1;
                        location = index;
                        break;
                    case 2:
                        position = 2;
                        location = index;
                        break;
                    case 3:
                        position = 3;
                        location = index;
                        break;
                }
            }
        });
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendRequest(location, position);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });

        builder.show();
    }

    public void sendRequest(int index, int position) {
        UserModel model = userListAdapter.myList.get(index);
        new ExampleRequestTask(this, userId, model.userID,estName, position, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularUserRankActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
//                        finish();
                    } else {
                        GlobalVars.showErrAlert(PopularUserRankActivity.this);
                    }

                } catch (Exception e) {

                }
                userListAdapter.notifyDataSetChanged();
            }
        });
    }
}
