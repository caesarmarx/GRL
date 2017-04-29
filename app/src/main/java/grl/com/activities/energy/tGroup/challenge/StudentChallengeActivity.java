package grl.com.activities.energy.tGroup.challenge;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import grl.com.activities.common.ContentShowActivity;
import grl.com.adapters.energy.ChallengeListAdapter;
import grl.com.adapters.energy.ChallengeNotifyAdapter;
import grl.com.adapters.energy.StudentChallengeAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.com.httpRequestTask.challenge.ChallengeListTask;
import grl.com.httpRequestTask.challenge.ChallengeLogTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
public class StudentChallengeActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    String teacherId = "";
    String teacherName = "";
    String teacherPhoto = "";

    String challengeLog = "";
    String challengeRule = "";

    ImageView userPhotoView;
    TextView groupNameView;
    TextView challengeLogView;
    LinearLayout ruleLayout;

    RecyclerView challengeListView;
    StudentChallengeAdapter challengeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_challenge);

        Intent intent = getIntent();
        teacherId = intent.getStringExtra(Constant.TGROUP_ID);
        teacherName = intent.getStringExtra(Constant.TGROUP_NAME);
        teacherPhoto = intent.getStringExtra(Constant.TGROUP_PHOTO);

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        groupNameView = (TextView)findViewById(R.id.tv_group_name);
        challengeLogView = (TextView)findViewById(R.id.tv_challenge_log);

        ruleLayout = (LinearLayout)findViewById(R.id.layout_challenge_rule);
        challengeListView = (RecyclerView)findViewById(R.id.challenge_list);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        ruleLayout.setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_challenge_title));
    }

    public void initializeData () {
        GlobalVars.loadImage(userPhotoView, teacherPhoto);
        groupNameView.setText(teacherName);

        challengeAdapter = new StudentChallengeAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        challengeListView.setLayoutManager(mLayoutManager);
        challengeListView.setItemAnimator(new DefaultItemAnimator());
        challengeListView.setAdapter(challengeAdapter);

        challengeAdapter.setTeacherId(teacherId);

        new ChallengeLogTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(StudentChallengeActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    int victory = GlobalVars.getIntFromJson(result, "challenge_victory");
                    int fail = GlobalVars.getIntFromJson(result, "challenge_failure");
                    int totalMoney = GlobalVars.getIntFromJson(result, "total_money");
                    int challengeRank = GlobalVars.getIntFromJson(result, "challenge_rank");
                    challengeLog = String.format("%d/%d", victory, fail);
                    challengeRule = GlobalVars.getStringFromJson(result, "challenge_rule");
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                challengeLogView.setText(challengeLog);

            }
        });

        refresh();
    }

    public void refresh() {
        challengeAdapter.myList.clear();
        challengeAdapter.notifyDataSetChanged();
        new ChallengeListTask(this, teacherId, "", new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(StudentChallengeActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    JSONArray jsonArr;

                    jsonArr = result.getJSONArray("challenges");
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject object = jsonArr.getJSONObject(i);
                        ChallengeModel model = new ChallengeModel();
                        model.parseFromJson(object);
                        model.challengeType = 0;
                        challengeAdapter.myList.add(model);
                    }
                    jsonArr = result.getJSONArray("responses");
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject object = jsonArr.getJSONObject(i);
                        ChallengeModel model = new ChallengeModel();
                        model.parseFromJson(object);
                        model.challengeType = 1;
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
            case R.id.layout_challenge_rule:
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getResources().getString(R.string.challenge_rule)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getResources().getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, challengeRule));
                break;
        }
    }
}