package grl.com.subViews.fragment.leagueSetting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import grl.com.activities.common.MultilineTextEditerActivity;
import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.activities.energy.tGroup.league.LeagueSettingActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.tGroup.LeagueAwardingInfoTask;
import grl.com.httpRequestTask.tGroup.LeagueAwardingSetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class Fragment_league_award extends Fragment implements View.OnClickListener{
    // view
    LeagueSettingActivity ctx;
    View layout;
    TextView tvLeagueTitle;
    TextView tvLeagueTime;
    TextView tvLeagueSize;
    TextView tvFirstCup;
    TextView tvSecondCup;
    TextView tvThirdCup;
    TextView tvSkillCup;
    TextView tvFirstName;
    TextView tvSecondName;
    TextView tvThirdName;
    TextView tvSkillName;
    Button btnFirst;
    Button btnSecond;
    Button btnThird;
    Button btnFourth;

    // task
    LeagueAwardingInfoTask leagueAwardingInfoTask;
    LeagueAwardingSetTask leagueAwardingSetTask;

    // response
    JsonObject awardingInfo;
    JsonArray awardingListResult;

    // require
    String tgroupID;

    // value
    int leagueIndex;
    static final String[] awardingStatus = {"颁证", "已颁"};
    static final int AWARDING_COMMENT_KEY = 123;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (LeagueSettingActivity) this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_league_award,
                    null);
            initViews();
            initData();
            setOnListener();
        } else {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        return layout;
    }

    public void initViews () {
        tvLeagueTitle = (TextView) layout.findViewById(R.id.tvLeagueTitle);
        tvLeagueTime = (TextView) layout.findViewById(R.id.tvLeagueTime);
        tvLeagueSize = (TextView) layout.findViewById(R.id.tvLeagueSize);
        tvFirstCup = (TextView) layout.findViewById(R.id.tvFirstCup);
        tvSecondCup = (TextView) layout.findViewById(R.id.tvSecondCup);
        tvThirdCup = (TextView) layout.findViewById(R.id.tvThirdCup);
        tvSkillCup = (TextView) layout.findViewById(R.id.tvSpecialCup);
        tvFirstName = (TextView) layout.findViewById(R.id.tvFirstName);
        tvSecondName = (TextView) layout.findViewById(R.id.tvSecondName);
        tvThirdName = (TextView) layout.findViewById(R.id.tvThirdName);
        tvSkillName = (TextView) layout.findViewById(R.id.tvSpecialName);
        btnFirst = (Button) layout.findViewById(R.id.btnFirst);
        btnSecond = (Button) layout.findViewById(R.id.btnSecond);
        btnThird = (Button) layout.findViewById(R.id.btnThird);
        btnFourth = (Button) layout.findViewById(R.id.btnSpecial);
    }

    public void initData () {
        // init value
        tgroupID = ctx.getGroupID();
        awardingInfo = new JsonObject();
        awardingListResult = new JsonArray();
        leagueIndex = 0;

        getAwardingData();
    }

    public void setOnListener () {
        layout.findViewById(R.id.btnFirst).setOnClickListener(this);
        layout.findViewById(R.id.btnSecond).setOnClickListener(this);
        layout.findViewById(R.id.btnThird).setOnClickListener(this);
        layout.findViewById(R.id.btnSpecial).setOnClickListener(this);
        layout.findViewById(R.id.tvSpecialName).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFirst:                         // 1등 수여
                awardToFirst();
                break;
            case R.id.btnSecond:                        // 2등 수여
                awardToSecond();
                break;
            case R.id.btnThird:                         // 3등 수여
                awardToThird();
                break;
            case R.id.btnSpecial:                       // 특기상 수여
                Utils.start_ActivityForResult(ctx, MultilineTextEditerActivity.class, AWARDING_COMMENT_KEY,
                        new BasicNameValuePair(Constant.TITLE_ID, ctx.getString(R.string.league_awarding_publish_reason)),
                        new BasicNameValuePair(Constant.TEXTFIELD_CONTENT, ""));
                break;
            case R.id.tvLeagueTitle:                    // 대전목록선택진행
                showLeagueLst();
                break;
            case R.id.tvSpecialName:                    // 친구계 성원목록현시진행
                showDiscipleLst();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case AWARDING_COMMENT_KEY:                  // 특기상 원인 편집결과인 경우
                String comment = data.getStringExtra(Constant.TEXTFIELD_CONTENT);
                awardToSkill(comment);
                break;
        }
    }

    // 시상자료를 얻어오기
    public void getAwardingData () {
        leagueAwardingInfoTask = new LeagueAwardingInfoTask(ctx, tgroupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                awardingListResult = (JsonArray) Response;
                if(awardingListResult.size() == 0) {
                    GlobalVars.showCommonAlertDialog(ctx, ctx.getString(R.string.league_awarding_empty), "");
                    return;
                }
                awardingInfo = awardingListResult.get(leagueIndex).getAsJsonObject();
                try{
                    String skillName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(awardingInfo.get("skill_place").getAsJsonObject().get("user_id").getAsString());
                    tvSkillName.setText(skillName);
                    InitViewValues();
                }catch (Exception e) {}
            }
        });
    }

    //  view 갱신을 진행한다.
    public void InitViewValues () {
        int user_status;
        try {
            int cnt = LeagueActivity.tGroupMemberModel.disciples.size() + 1;
            String leagueTime = awardingInfo.get("league_start").getAsString() + "-" + awardingInfo.get("league_end").getAsString();
            tvLeagueTitle.setText(awardingInfo.get("league_name").getAsString());
            tvLeagueTime.setText(leagueTime);
            tvLeagueSize.setText(cnt + 1);
        }catch (Exception e) {}
        // 1 등
        try{
            String firstName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(awardingInfo.get("first_place").getAsJsonObject().get("user_id").getAsString());
            tvFirstName.setText(firstName);
            user_status = awardingInfo.get("first_place").getAsJsonObject().get("user_status").getAsInt();
            if(user_status == 1) {
                btnFirst.setEnabled(false);
                tvFirstCup.setVisibility(View.INVISIBLE);
            } else {
                btnFirst.setEnabled(true);
                tvFirstCup.setVisibility(View.VISIBLE);
            }
            btnFirst.setText(awardingStatus[user_status]);
        } catch (Exception e) {}
        // 2 등
        try{
            String secondName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(awardingInfo.get("second_place").getAsJsonObject().get("user_id").getAsString());
            tvSecondName.setText(secondName);
            user_status = awardingInfo.get("second_place").getAsJsonObject().get("user_status").getAsInt();
            if(user_status == 1) {
                btnSecond.setEnabled(false);
                tvSecondCup.setVisibility(View.INVISIBLE);
            } else {
                btnSecond.setEnabled(true);
                tvSecondCup.setVisibility(View.VISIBLE);
            }
            btnSecond.setText(awardingStatus[user_status]);
        } catch (Exception e) {}
        // 3 등
        try{
            String thirdName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(awardingInfo.get("first_place").getAsJsonObject().get("user_id").getAsString());
            tvFirstName.setText(thirdName);
            user_status = awardingInfo.get("third_place").getAsJsonObject().get("user_status").getAsInt();
            if(user_status == 1) {
                btnThird.setEnabled(false);
                tvThirdCup.setVisibility(View.INVISIBLE);
            } else {
                btnThird.setEnabled(true);
                tvThirdCup.setVisibility(View.VISIBLE);
            }
            btnThird.setText(awardingStatus[user_status]);
        } catch (Exception e) {}
        // 특기상
        try{
            String skillName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(awardingInfo.get("skill_place").getAsJsonObject().get("user_id").getAsString());
            tvFirstName.setText(skillName);
            user_status = awardingInfo.get("skill_place").getAsJsonObject().get("user_status").getAsInt();
            if(user_status == 1) {
                btnFourth.setEnabled(false);
                tvSkillCup.setVisibility(View.INVISIBLE);
            } else {
                btnFourth.setEnabled(true);
                tvSkillCup.setVisibility(View.VISIBLE);
            }
            btnFourth.setText(awardingStatus[user_status]);
        } catch (Exception e) {}
    }

    // 상대방에 대한 시상발표진행
    public void awardingSetRequest (JsonObject params) {
        leagueAwardingSetTask = new LeagueAwardingSetTask(ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean success = result.get("result").getAsBoolean();
                if(success) {           // success

                } else {                // failure

                }
            }
        });
    }

    // 1등에 대한 시상발표 진행
    public void awardToFirst () {
        try{
            JsonObject params = new JsonObject();
            String strFirstId = awardingInfo.get("first_place").getAsJsonObject().get("user_id").getAsString();
            params.addProperty("session_id", SelfInfoModel.sessionID);
            params.addProperty("tleague_id", awardingInfo.get("_id").getAsString());
            params.addProperty("awarding_type", 1);
            params.addProperty("awarding_userId",strFirstId);
            params.addProperty("comment", "");

            awardingSetRequest(params);
            InitViewValues();
        } catch (Exception e) {}
    }

    // 2등에 대한 시상발표 진행
    public void awardToSecond () {
        try {
            JsonObject params = new JsonObject();
            String strSecondId = awardingInfo.get("second_place").getAsJsonObject().get("user_id").getAsString();
            params.addProperty("session_id", SelfInfoModel.sessionID);
            params.addProperty("tleague_id", awardingInfo.get("_id").getAsString());
            params.addProperty("awarding_type", 2);
            params.addProperty("awarding_userId", strSecondId);
            params.addProperty("awwarding_comment", "");

            awardingSetRequest(params);
            InitViewValues();
        } catch (Exception e) {}
    }

    // 3등에 대한 시상발표 진행
    public void awardToThird () {
        try {
            JsonObject params = new JsonObject();
            String strThridId = awardingInfo.get("third_place").getAsJsonObject().get("user_id").getAsString();
            params.addProperty("session_id", SelfInfoModel.sessionID);
            params.addProperty("tleague_id", awardingInfo.get("_id").getAsString());
            params.addProperty("awarding_type", 3);
            params.addProperty("awarding_userId", strThridId);
            params.addProperty("comment", "");

            awardingSetRequest(params);
            InitViewValues();
        } catch (Exception e ) {}
    }

    // 특기상에 대한 시상진행
    public void awardToSkill (String comment) {
        try {
            JsonObject params = new JsonObject();
            String strSkillId = awardingInfo.get("skill_place").getAsJsonObject().get("user_id").getAsString();
            params.addProperty("session_id", SelfInfoModel.userID);
            params.addProperty("tlgeague_id", awardingInfo.get("_id").getAsString());
            params.addProperty("awarding_type", 0);
            params.addProperty("awarding_userId", strSkillId);
            params.addProperty("awarding_comment", comment);

            awardingSetRequest(params);
            InitViewValues();
        } catch (Exception e) {}
    }

    // 대전목록을 현시한다.
    public void showLeagueLst () {
        try{
            String[] strArray = new String[awardingListResult.size()];
            for(int i = 0; i < strArray.length; i ++) {
                String strName = awardingListResult.get(i).getAsJsonObject().get("league_name").getAsString();
                strArray[i] = strName;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(ctx.getString(R.string.league_list_title));
            builder.setSingleChoiceItems(strArray, leagueIndex, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    leagueIndex = which;
                    awardingInfo = awardingListResult.get(leagueIndex).getAsJsonObject();
                    InitViewValues();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        } catch (Exception e) { return; }

    }

    // 친구계성원목록 현시진행
    public void showDiscipleLst () {
        final String[] strArray = new String[LeagueActivity.tGroupMemberModel.disciples.size()];
        for(int i = 0; i < strArray.length; i ++) {
            String strName = LeagueActivity.tGroupMemberModel.disciples.get(i).getAsJsonObject().get("user_name").getAsString();
            strArray[i] = strName;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getString(R.string.league_list_title));
        builder.setSingleChoiceItems(strArray, leagueIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvSkillName.setText(strArray[which]);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
