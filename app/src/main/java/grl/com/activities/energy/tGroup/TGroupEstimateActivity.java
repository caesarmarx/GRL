package grl.com.activities.energy.tGroup;

import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.activities.discovery.popularity.MyExampleActivity;
import grl.com.adapters.order.UserListAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.httpRequestTask.popularity.MyExampleListTask;
import grl.com.httpRequestTask.tGroup.TeacherSetJudgeTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class TGroupEstimateActivity extends Activity implements View.OnClickListener{

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    RadioButton goodBtn;
    TextView goodView;
    RadioButton normalBtn;
    TextView normalView;
    RadioButton badBtn;
    TextView badView;

    Button estimateBtn;

    int goodValue = 0;
    int normalValue = 0;
    int badValue = 0;

    int selectedIndex = 0;

    String teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgroup_estimate);

        Intent intent = getIntent();
        teacherId = intent.getStringExtra(Constant.TGROUP_ID);

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        goodBtn = (RadioButton)findViewById(R.id.btn_estimate_good);
        normalBtn = (RadioButton)findViewById(R.id.btn_estimate_normal);
        badBtn = (RadioButton)findViewById(R.id.btn_estimate_bad);
        goodView = (TextView)findViewById(R.id.tv_estimate_good);
        normalView = (TextView)findViewById(R.id.tv_estimate_normal);
        badView = (TextView)findViewById(R.id.tv_estimate_bad);
        estimateBtn = (Button)findViewById(R.id.btn_estimate);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        goodBtn.setOnClickListener(this);
        normalBtn.setOnClickListener(this);
        badBtn.setOnClickListener(this);

        estimateBtn.setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_estimate_title));
    }

    public void initializeData () {
        refresh();
        if (teacherId.compareTo(SelfInfoModel.userID) == 0) {
            estimateBtn.setVisibility(View.GONE);
            goodBtn.setVisibility(View.INVISIBLE);
            normalBtn.setVisibility(View.INVISIBLE);
            badBtn.setVisibility(View.INVISIBLE);
        } else {
            estimateBtn.setVisibility(View.VISIBLE);
            goodBtn.setVisibility(View.VISIBLE);
            normalBtn.setVisibility(View.VISIBLE);
            badBtn.setVisibility(View.VISIBLE);
        }
        refresh();

    }

    public void refresh() {
        new UserInfoGetTask(this, teacherId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(TGroupEstimateActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    goodValue = GlobalVars.getIntFromJson(result, "user_judge_good");
                    normalValue = GlobalVars.getIntFromJson(result, "user_judge_normal");
                    badValue = GlobalVars.getIntFromJson(result, "user_judge_bad");

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                goodView.setText(String.format("%d次", goodValue));
                normalView.setText(String.format("%d次", normalValue));
                badView.setText(String.format("%d次", badValue));
            }
        });
    }

    void updateUI() {
        goodBtn.setChecked(false);
        normalBtn.setChecked(false);
        badBtn.setChecked(false);

        if (selectedIndex == 0)
            goodBtn.setChecked(true);
        if (selectedIndex == 1)
            normalBtn.setChecked(true);
        if (selectedIndex == 2)
            badBtn.setChecked(true);
    }

    public void estimateAction() {
        new TeacherSetJudgeTask(this, teacherId, selectedIndex, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                try {
                    if (!flag || response == null) {                 //failure
                        GlobalVars.showErrAlert(TGroupEstimateActivity.this);
                        return;
                    }
                    JSONObject result = (JSONObject) response;

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                refresh();
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
            case R.id.btn_estimate_good:
                selectedIndex = 0;
                updateUI();
                break;
            case R.id.btn_estimate_normal:
                selectedIndex = 1;
                updateUI();
                break;
            case R.id.btn_estimate_bad:
                selectedIndex = 2;
                updateUI();
                break;
            case R.id.btn_estimate:
                estimateAction();
                break;
        }
    }

}
