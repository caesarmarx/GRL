package grl.com.activities.energy.tGroup.challenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.microedition.khronos.opengles.GL;

import grl.com.adapters.energy.ChallengeListAdapter;
import grl.com.adapters.energy.ChallengeMemberAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeContributeModel;
import grl.com.httpRequestTask.challenge.ChallengeAcceptTask;
import grl.com.httpRequestTask.challenge.ChallengeAttendTask;
import grl.com.httpRequestTask.challenge.ChallengeInfoTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;
public class ChallengeDetailActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    ImageView requestImgView;
    TextView requestNameView;
    ImageView responseImgView;
    TextView responseNameView;
    TextView factorView;
    TextView moneyView;
    TextView stateView;
    TextView dateView;


    Button acceptBtn;
    View acceptSpace;
    Button declineBtn;
    View declineSpace;
    Button attendBtn;
    View attendSpace;

    String challengeId;
    String teacherId;
    int nShowType;

    RecyclerView memberListView;
    ChallengeMemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        Intent intent = getIntent();
        challengeId = intent.getStringExtra(Constant.CHALLENGE_ID);
        teacherId = intent.getStringExtra(Constant.TEACHER_ID);
        nShowType = Integer.parseInt(intent.getStringExtra("showType"));

        getViewByID();
        setOnListener();
        initNavBar();
        initializeData();
    }

    public void getViewByID () {
        requestImgView = (ImageView)findViewById(R.id.img_from_user_photo);
        requestNameView = (TextView) findViewById(R.id.tv_from_user_name);
        responseImgView = (ImageView)findViewById(R.id.img_to_user_photo);
        responseNameView = (TextView) findViewById(R.id.tv_to_user_name);
        factorView = (TextView)findViewById(R.id.tv_challenge_est_name);
        moneyView = (TextView) findViewById(R.id.tv_challenge_budget);
        dateView = (TextView) findViewById(R.id.tv_challenge_time);
        stateView = (TextView) findViewById(R.id.tv_challenge_state);

        acceptBtn = (Button) findViewById(R.id.btn_challenge_accept);
        acceptSpace = (View) findViewById(R.id.space_challenge_accept);
        declineBtn = (Button) findViewById(R.id.btn_challenge_decline);
        declineSpace = (View) findViewById(R.id.space_challenge_decline);
        attendBtn = (Button) findViewById(R.id.btn_challenge_attend);
        attendSpace = (View) findViewById(R.id.space_challenge_attend);
//        acceptBtn = (Button) findViewById(R.id.btn_challenge_request)

        memberListView = (RecyclerView)findViewById(R.id.conntribute_list);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        acceptBtn.setOnClickListener(this);
        declineBtn.setOnClickListener(this);
        attendBtn.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.tgroup_challenge_title));
    }

    public void initializeData () {

        memberAdapter = new ChallengeMemberAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        memberListView.setLayoutManager(mLayoutManager);
        memberListView.setItemAnimator(new DefaultItemAnimator());
        memberListView.setAdapter(memberAdapter);

        refresh();
    }

    public void refresh() {
        acceptBtn.setVisibility(View.GONE);
        acceptSpace.setVisibility(View.GONE);
        declineBtn.setVisibility(View.GONE);
        declineSpace.setVisibility(View.GONE);
        attendBtn.setVisibility(View.GONE);
        attendSpace.setVisibility(View.GONE);
        new ChallengeInfoTask(this, SelfInfoModel.userID, challengeId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    return;
                }

                memberAdapter.myList.clear();
                try {
                    JSONObject result = (JSONObject) response;
                    GlobalVars.loadImage(requestImgView, GlobalVars.getStringFromJson(result, "from_userphoto"));
                    GlobalVars.loadImage(responseImgView, GlobalVars.getStringFromJson(result, "to_userphoto"));
                    requestNameView.setText(GlobalVars.getStringFromJson(result,"from_username"));
                    responseNameView.setText(GlobalVars.getStringFromJson(result,"to_username"));
                    factorView.setText(GlobalVars.getStringFromJson(result, "est_value"));
                    moneyView.setText(String.format("%d", GlobalVars.getIntFromJson(result, "challenge_money")));

                    Boolean bAttendShow = true;
                    JSONArray jsonUsers = GlobalVars.getJSONArrayFromJson(result, "challenge_users");
                    for (int i = 0; i < jsonUsers.length(); i++) {
                        JSONObject object = jsonUsers.getJSONObject(i);
                        ChallengeContributeModel model = new ChallengeContributeModel();
                        model.parseFromJson(object);
                        if (SelfInfoModel.userID.compareTo(model.userId) == 0) {
                            bAttendShow = false;
                        }
                        if (nShowType == 0)
                            memberAdapter.myList.add(model);
                    }
                    jsonUsers = GlobalVars.getJSONArrayFromJson(result, "response_users");
                    for (int i = 0; i < jsonUsers.length(); i++) {
                        JSONObject object = jsonUsers.getJSONObject(i);
                        ChallengeContributeModel model = new ChallengeContributeModel();
                        model.parseFromJson(object);
                        if (SelfInfoModel.userID.compareTo(model.userId) == 0) {
                            bAttendShow = false;
                        }
                        if (nShowType == 1)
                            memberAdapter.myList.add(model);
                    }


                    String startTime = GlobalVars.getDateStringFromLong(
                                        GlobalVars.getDateFromJson(result, "start_date"),"yyyy-MM-dd");
                    String endTime = GlobalVars.getDateStringFromLong(
                            GlobalVars.getDateFromJson(result, "end_date"),"yyyy-MM-dd");
                    dateView.setText(String.format("%s : %s", startTime, endTime));
                    stateView.setText("新");

                    int acceptState = GlobalVars.getIntFromJson(result, "accept_state");
                    int challengeState = GlobalVars.getIntFromJson(result, "challenge_state");
                    int challengeResult = GlobalVars.getIntFromJson(result, "challenge_result");
                    if (acceptState < Constant.CHALLENGE__ACCEPT) {
                        if (nShowType == 1 && teacherId.compareTo(SelfInfoModel.userID) == 0) {
                            acceptBtn.setVisibility(View.VISIBLE);
                            acceptSpace.setVisibility(View.VISIBLE);
                            declineBtn.setVisibility(View.VISIBLE);
                            declineSpace.setVisibility(View.VISIBLE);
                        }
                        stateView.setText(getResources().getString(R.string.challenge_wait));
                    }
                    if (acceptState == Constant.CHALLENGE__DECLINE) {
                        stateView.setText(getResources().getString(R.string.challenge_decline));
                    }
                    if (acceptState == Constant.CHALLENGE__ACCEPT) {
                        if (teacherId.compareTo(SelfInfoModel.userID) != 0 && bAttendShow) {
                            attendBtn.setVisibility(View.VISIBLE);
                            attendSpace.setVisibility(View.VISIBLE);
                        }

                        if (challengeState == 0) {
                            stateView.setText(getResources().getString(R.string.challenge_start_state));
                        }
                        if (challengeState == 1) {
                            stateView.setText(getResources().getString(R.string.challenge_running_state));
                        }
                        if (challengeState == 2) {
                            if (challengeResult == 1) {
                                if (nShowType == 0)
                                    stateView.setText(getResources().getString(R.string.challenge_win));
                            }
                            if (challengeResult == -1) {
                                if (nShowType == 0)
                                    stateView.setText(getResources().getString(R.string.challenge_win));
                                else
                                    stateView.setText(getResources().getString(R.string.challenge_defeat));
                            }
                            if (challengeResult == 0) {
                                stateView.setText("");
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
                memberAdapter.notifyDataSetChanged();
            }
        });
    }

    private void acceptAction() {
        new ChallengeAcceptTask(this, challengeId, 2, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        refresh();
                    } else {
                        GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
    }
    private void declineAction() {
        new ChallengeAcceptTask(this, challengeId, 3, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        refresh();
                    } else {
                        GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
                }
            }
        });
    }
    private void attendAction() {
        new ChallengeAttendTask(this, challengeId, nShowType, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if (!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        refresh();
                    } else {
                        GlobalVars.showErrAlert(ChallengeDetailActivity.this);
                    }

                } catch (Exception e) {
                    Log.e("MYAPP", "exception", e);
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
            case R.id.btn_challenge_accept:
                acceptAction();
                break;
            case R.id.btn_challenge_decline:
                declineAction();
                break;
            case R.id.btn_challenge_attend:
                attendAction();
                break;
        }
    }

}