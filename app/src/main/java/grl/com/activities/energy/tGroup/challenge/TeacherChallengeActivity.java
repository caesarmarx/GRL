package grl.com.activities.energy.tGroup.challenge;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
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

import com.koushikdutta.async.Util;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.body.StreamBody;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import grl.com.activities.common.ContentShowActivity;
import grl.com.adapters.energy.ChallengeNotifyAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.com.httpRequestTask.challenge.ChallengeLogTask;
import grl.com.httpRequestTask.challenge.ChallengeNotifyTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class TeacherChallengeActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    RecyclerView notifyListView;
    ChallengeNotifyAdapter notifyAdapter;

    ImageView userPhotoView;
    TextView groupNameView;
    TextView challengeLogView;
    TextView challengeMoneyView;
    LinearLayout goalLayout;
    LinearLayout ruleLayout;

    Button requestBtn;
    Button responseBtn;

    String challengeLog = "";
    String challengeMoney = "";
    String challengeGoal = "";
    String challengeRule = "";
    Boolean bNewChallenge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_challenge);
        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        userPhotoView = (ImageView)findViewById(R.id.img_user_photo);
        groupNameView = (TextView)findViewById(R.id.tv_group_name);
        challengeLogView = (TextView)findViewById(R.id.tv_challenge_log);
        challengeMoneyView = (TextView)findViewById(R.id.tv_challenge_money);

        goalLayout = (LinearLayout)findViewById(R.id.layout_challenge_goal);
        ruleLayout = (LinearLayout)findViewById(R.id.layout_challenge_rule);

        requestBtn = (Button)findViewById(R.id.btn_challenge_request);
        responseBtn = (Button)findViewById(R.id.btn_challenge_response);

        notifyListView = (RecyclerView)findViewById(R.id.challenge_notify_list);

    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        goalLayout.setOnClickListener(this);
        ruleLayout.setOnClickListener(this);
        requestBtn.setOnClickListener(this);
        responseBtn.setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_challenge_title));
    }

    public void initializeData () {
        GlobalVars.loadImage(userPhotoView, SelfInfoModel.userPhoto);
        groupNameView.setText(getResources().getString(R.string.my_tgroup));

        notifyAdapter = new ChallengeNotifyAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notifyListView.setLayoutManager(mLayoutManager);
        notifyListView.setItemAnimator(new DefaultItemAnimator());
        notifyListView.setAdapter(notifyAdapter);

        new ChallengeLogTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(TeacherChallengeActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    int victory = GlobalVars.getIntFromJson(result, "challenge_victory");
                    int fail = GlobalVars.getIntFromJson(result, "challenge_failure");
                    int totalMoney = GlobalVars.getIntFromJson(result, "total_money");
                    int challengeRank = GlobalVars.getIntFromJson(result, "challenge_rank");
                    challengeLog = String.format("%d/%d", victory, fail);
                    challengeMoney = String.format("%d/%d", totalMoney, challengeRank);
                    challengeRule = GlobalVars.getStringFromJson(result, "challenge_rule");
                    challengeGoal = GlobalVars.getStringFromJson(result, "challenge_goal");
                    bNewChallenge = GlobalVars.getBooleanFromJson(result, "new_challenge");
                    if (bNewChallenge) {
                        responseBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_arrow, 0);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                challengeLogView.setText(challengeLog);
                challengeMoneyView.setText(challengeMoney);

            }
        });

        refresh();
    }

    public void refresh() {
        notifyAdapter.myList.clear();
        notifyAdapter.notifyDataSetChanged();
        new ChallengeNotifyTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(TeacherChallengeActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        ChallengeModel model = new ChallengeModel();
                        model.parseFromJson(object);
                        notifyAdapter.myList.add(model);
                    }
                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                notifyAdapter.notifyDataSetChanged();
            }
        });
    }

    public void requestAction() {
        Utils.start_Activity(this, ChallengeRequestActivity.class);
    }

    public void responseAction() {
        Utils.start_Activity(this, ChallengeResponseActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_challenge_response:
                responseAction();
                break;
            case R.id.btn_challenge_request:
                requestAction();
                break;
            case R.id.layout_challenge_goal:
                Utils.start_Activity(this, ContentShowActivity.class,
                        new BasicNameValuePair(Constant.TITLE_ID, getResources().getString(R.string.challenge_goal)),
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getResources().getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.CONTENT_VIEW_KEY, challengeGoal));
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
