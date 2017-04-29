package grl.com.activities.order;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.activities.MainActivity;
import grl.com.activities.search.PhoneSearchActivity;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.user.UserRankTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class UserRankActivity extends Activity implements View.OnClickListener{

    private RecyclerView userListView;
    private UserListAdapter userListAdapter;
    private TextView rankView;
    private Button worshipBtn;

    private int areaIndex;
    private int rankIndex;
    private int start = 0;
    private int limit = 100;

    private int userRank = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rank);

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        userListView = (RecyclerView)findViewById(R.id.user_rank_list);
        rankView = (TextView)findViewById(R.id.tv_user_rank);
        worshipBtn = (Button)findViewById(R.id.btn_worship);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.rank_area_all).setOnClickListener(this);
        findViewById(R.id.rank_same_city).setOnClickListener(this);
        findViewById(R.id.rank_near_by).setOnClickListener(this);
        findViewById(R.id.rank_tgroup).setOnClickListener(this);

        findViewById(R.id.user_rank_road).setOnClickListener(this);
        findViewById(R.id.user_rank_teacher).setOnClickListener(this);
        findViewById(R.id.user_rank_user).setOnClickListener(this);
        findViewById(R.id.user_rank_energy).setOnClickListener(this);
        findViewById(R.id.user_rank_money).setOnClickListener(this);
        findViewById(R.id.user_rank_fee).setOnClickListener(this);

        findViewById(R.id.user_search).setOnClickListener(this);

        findViewById(R.id.btn_worship).setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.friend_group_title));
    }

    public void initializeData () {
        // set up Adapter
        userListAdapter = new UserListAdapter(this);
        RecyclerView.LayoutManager mLayoutManagerTop = new LinearLayoutManager(getApplicationContext());
        userListView.setLayoutManager(mLayoutManagerTop);
        userListView.setItemAnimator(new DefaultItemAnimator());
        userListView.setAdapter(userListAdapter);
        userListAdapter.showNumber();

        refresh();
    }

    public void refresh() {
        if (rankIndex == 1 || rankIndex == 2)
            worshipBtn.setVisibility(View.VISIBLE);
        else
            worshipBtn.setVisibility(View.INVISIBLE);

        new UserRankTask(this, areaIndex, rankIndex, start, limit, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(UserRankActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    userRank = result.getInt("rank");
                    rankView.setText(String.format("%s : %d", SelfInfoModel.userName, userRank + 1));
                    JSONArray list = result.getJSONArray("user_list");

                    userListAdapter.myList.clear();

                    for (int i =0; i < list.length(); i++) {
                        JSONObject object = list.getJSONObject(i);
                        UserModel userModel = new UserModel();
                        userModel.parseFromJson(object);
                        userListAdapter.myList.add(userModel);
                    }
                    userListAdapter.notifyDataSetChanged();

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

            case R.id.btn_worship:
                Utils.start_Activity(UserRankActivity.this, WorshipActivity.class);
                break;
            case R.id.rank_area_all:
                areaIndex = 0;
                refresh();
                break;
            case R.id.rank_same_city:
                areaIndex = 1;
                refresh();
                break;
            case R.id.rank_near_by:
                areaIndex = 2;
                refresh();
                break;
            case R.id.rank_tgroup:
                areaIndex = 3;
                refresh();
                break;

            case R.id.user_rank_road:
                rankIndex = 0;
                refresh();
                break;
            case R.id.user_rank_teacher:
                rankIndex = 1;
                refresh();
                break;
            case R.id.user_rank_user:
                rankIndex = 2;
                refresh();
                break;
            case R.id.user_rank_energy:
                rankIndex = 3;
                refresh();
                break;
            case R.id.user_rank_money:
                rankIndex = 4;
                refresh();
                break;
            case R.id.user_rank_fee:
                rankIndex = 5;
                refresh();
                break;

            case R.id.user_search:
                Utils.start_Activity(UserRankActivity.this, PhoneSearchActivity.class);
                break;
        }
    }

}
