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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.adapters.popularity.OtherPopularityAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.PopularityModel;
import grl.com.httpRequestTask.popularity.MyPopularityTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class OtherPopularityActivity extends Activity implements View.OnClickListener {

    int menuIndex = 1;

    int ability;

    String userId = "";

    private RadioGroup menuGroup;

    private RecyclerView popularListView;
    private OtherPopularityAdapter popularAdapter;
    private TextView abilityView;
    private TextView commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_popularity);

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constant.USER_ID);

        getViewByID();
        setOnListener();
        initNavBar();
        initMenuView();
        initializeData();
    }

    public void getViewByID () {
        popularListView = (RecyclerView) findViewById(R.id.my_popularity_list);
        menuGroup = (RadioGroup) findViewById(R.id.rg_factor_menu);
        abilityView = (TextView) findViewById(R.id.tv_ability);
        commentView = (TextView) findViewById(R.id.tv_est_comment);
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
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_cheer_title));
    }

    public void initializeData () {
        popularAdapter = new OtherPopularityAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        popularListView.setLayoutManager(mLayoutManager);
        popularListView.setItemAnimator(new DefaultItemAnimator());
        popularListView.setAdapter(popularAdapter);
        refresh();
    }

    public void initMenuView() {
        for (int i = 0; i < GlobalVars.factorList.size(); i++) {
            RadioButton button = (RadioButton) getLayoutInflater().inflate(R.layout.layout_tab_btn, null);
            if (i == 0)
                button.setChecked(true);
            button.setId(i);
            button.setText(GlobalVars.factorList.get(i).estValue);
            button.setTextSize(15);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuClicked(v.getId());
                }
            });
            menuGroup.addView(button);
        }
    }

    public void menuClicked(int index) {
        menuIndex = index;
        refresh();
    }

    public void refresh() {
        if (menuIndex >= GlobalVars.factorList.size())
            return;
        String estName = GlobalVars.factorList.get(menuIndex).estName;
        commentView.setText(GlobalVars.factorList.get(menuIndex).description);
        // Set Est Name of Parent

        popularAdapter.myList.clear();
        popularAdapter.notifyDataSetChanged();
        new MyPopularityTask(this, userId, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OtherPopularityActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    ability = GlobalVars.getIntFromJson(result, "ability");
                    abilityView.setText(String.format("%d", ability));
                    JSONArray list = result.getJSONArray("twitters");
                    for (int i =0; i < list.length(); i++) {
                        JSONObject object = list.getJSONObject(i);
                        PopularityModel model = new PopularityModel();
                        model.parseFromJson(object);
                        popularAdapter.myList.add(model);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                popularAdapter.notifyDataSetChanged();
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
