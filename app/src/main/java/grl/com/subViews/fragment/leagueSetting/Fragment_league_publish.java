package grl.com.subViews.fragment.leagueSetting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.Calendar;

import grl.com.activities.energy.tGroup.league.LeagueActivity;
import grl.com.activities.energy.tGroup.league.LeagueSettingActivity;
import grl.com.adapters.energy.LeagueSettingAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.httpRequestTask.tGroup.LeagueGetInfoTask;
import grl.com.httpRequestTask.tGroup.LeagueRoundListGenerate;
import grl.com.httpRequestTask.tGroup.LeagueSettingUpdateTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class Fragment_league_publish extends Fragment implements View.OnClickListener{

    // view
    LeagueSettingActivity ctx;
    View layout;
    TextView tvLeagueTitle;
    TextView tvStartTime;
    TextView tvEndTime;
    Button btnPublish;
    RecyclerView recyclerView;

    // task
    LeagueGetInfoTask leagueGetInfoTask;
    LeagueSettingUpdateTask leagueSettingUpdateTask;
    LeagueRoundListGenerate leagueRoundListGenerate;

    // response
    JsonObject leagueInfo;
    JsonArray roundList;

    // require
    String tgroupID;

    // value
    LeagueSettingAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null) {
            ctx = (LeagueSettingActivity) this.getActivity();
            layout = ctx.getLayoutInflater().inflate(R.layout.fragment_league_publish,
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

    @Override
    public void onDestroy() {
        updateSetting();
        super.onDestroy();
    }

    public void initViews () {
        tvLeagueTitle = (TextView) layout.findViewById(R.id.tvLeagueTitle);
        tvStartTime = (TextView) layout.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) layout.findViewById(R.id.tvEndTime);
        btnPublish = (Button) layout.findViewById(R.id.btn_league_publish);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
    }

    public void initData () {
        // init values
        tgroupID = ctx.getGroupID();
        leagueInfo = new JsonObject();
        roundList = new JsonArray();

        // set up Adapter
        recyclerAdapter = new LeagueSettingAdapter(this.ctx);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.ctx.getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        leagueGetInfo();
    }

    public void setOnListener () {
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvStartTime:                  // 대전시작시간 설정
                showDatePicker(tvStartTime);
                break;
            case R.id.tvEndTime:                    // 대전마감시간 설정
                showDatePicker(tvEndTime);
                break;
            case R.id.btn_league_publish:           // 대전발표 진행
                break;
        }
    }

    public void showDatePicker(final TextView textView) {
        if(roundList.size() > 0)
            return;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        textView.setText(year + "年" + monthOfYear + "月" + dayOfMonth + "日");

                    }
                }, mYear, mMonth, mDay);

        dpd.show();
    }
    public void leagueGetInfo () {
        leagueGetInfoTask = new LeagueGetInfoTask(ctx, tgroupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    leagueInfo = result;
                    roundList = leagueInfo.get("round_list").getAsJsonArray();
                    tvStartTime.setText(leagueInfo.get("league_start").getAsString());
                    tvEndTime.setText(leagueInfo.get("league_end").getAsString());
                    tvLeagueTitle.setText(leagueInfo.get("league_name").getAsString());
                    recyclerAdapter.notifyData(roundList);
                } catch (Exception e) {}
            }
        });
    }

    // 변화된 값을 보관한다.
    public void updateSetting () {
        if(roundList.size() == 0)
            return;
        JsonObject params = new JsonObject();
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.addProperty("teacher_id", tgroupID);
        params.add("round_list", roundList);
        leagueSettingUpdateTask = new LeagueSettingUpdateTask(ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                leagueInfo = (JsonObject) Response;
                Boolean success = leagueInfo.get("result").getAsBoolean();
                if(success) {               // success

                } else {                    // failure

                }
            }
        });
    }

    // 대전발표진행
    public void onConfirmBtnAction () {
        if(roundList.size() > 0) {
            return;
        }
        if(LeagueActivity.tGroupMemberModel.disciples.size() < 8) {         // 제자수가 8명이상 되여야 대전을 진행할수있다.
            GlobalVars.showCommonAlertDialog(ctx, ctx.getString(R.string.league_publish_title), ctx.getString(R.string.league_publish_disable_msg));
            return;
        }
        String strStartTime = tvStartTime.getText().toString();
        String strEndTime = tvEndTime.getText().toString();
        if(strStartTime.equals("") || strEndTime.equals(""))              // 날자를 입력하지 않았을 때의 처리 진행
        {
            GlobalVars.showCommonAlertDialog(ctx, ctx.getString(R.string.textfield_alert), "");
            return;
        }
        JsonObject params = new JsonObject();
        params.addProperty("teacher_id", tgroupID);
        params.addProperty("start_time", tvStartTime.getText().toString());
        leagueRoundListGenerate = new LeagueRoundListGenerate(ctx, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(ctx);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    leagueInfo = result;
                    roundList = leagueInfo.get("round_list").getAsJsonArray();
                    tvStartTime.setText(leagueInfo.get("league_start").getAsString());
                    tvEndTime.setText(leagueInfo.get("league_end").getAsString());
                    tvLeagueTitle.setText(leagueInfo.get("league_name").getAsString());
                    recyclerAdapter.notifyData(roundList);
                } catch (Exception e) {}
            }
        });
    }
}
