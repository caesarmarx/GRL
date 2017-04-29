package grl.com.activities.energy.tGroup.challenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.adapters.energy.ChallengeListAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.com.httpRequestTask.challenge.ChallengeListTask;
import grl.com.httpRequestTask.challenge.ChallengeNotifyTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
public class ChallengeResponseActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    int tabIndex = 0;

    Button requestTabBtn;
    Button responseTabBtn;

    RecyclerView challengeListView;
    ChallengeListAdapter challengeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_response);
        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        requestTabBtn = (RadioButton)findViewById(R.id.rb_challenge_request);
        responseTabBtn = (RadioButton)findViewById(R.id.rb_challenge_response);
        challengeListView = (RecyclerView)findViewById(R.id.challenge_list);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        requestTabBtn.setOnClickListener(this);
        responseTabBtn.setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_challenge_title));
    }

    public void initializeData () {
        challengeAdapter = new ChallengeListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        challengeListView.setLayoutManager(mLayoutManager);
        challengeListView.setItemAnimator(new DefaultItemAnimator());
        challengeListView.setAdapter(challengeAdapter);

        challengeAdapter.setTeacherId(SelfInfoModel.userID);

        refresh();
    }

    public void refresh() {
        challengeAdapter.setTabIndex(tabIndex);
        challengeAdapter.myList.clear();
        challengeAdapter.notifyDataSetChanged();
        new ChallengeListTask(this, SelfInfoModel.userID, "", new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeResponseActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    JSONArray jsonArr;
                    if (tabIndex == 0)
                        jsonArr = result.getJSONArray("challenges");
                    else
                        jsonArr = result.getJSONArray("responses");
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject object = jsonArr.getJSONObject(i);
                        ChallengeModel model = new ChallengeModel();
                        model.parseFromJson(object);
                        challengeAdapter.myList.add(model);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                challengeAdapter.notifyDataSetChanged();
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
            case R.id.rb_challenge_request:
                tabIndex = 0;
                refresh();
                break;
            case R.id.rb_challenge_response:
                tabIndex = 1;
                refresh();
                break;
        }
    }

}