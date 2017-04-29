package grl.com.activities.energy.tGroup.league;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import grl.com.adapters.energy.LeagueAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.TGroupMemberModel;
import grl.com.httpRequestTask.tGroup.LeagueStatusGetTask;
import grl.com.httpRequestTask.tGroup.LeagueStatusSetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/11/2016.
 */
public class LeagueActivity extends Activity implements View.OnClickListener{

    // views
    RecyclerView recyclerView;
    TextView tvStatusTitle;

    // tasks
    LeagueStatusGetTask leagueStatusGetTask;
    LeagueStatusSetTask leagueStatusSetTask;

    // require
    String tgroupID;

    // response
    JsonObject currentRound;
    JsonObject pastRound;
    JsonObject futureRound;

    // values
    LeagueAdapter recyclerAdapter;
    public static TGroupMemberModel tGroupMemberModel;
    public static String leagueID;
    public static String roundID;
    public static int statusType;
    public static final int PAST_STATUS = -1;
    public static final int CURRENT_STATUS = 0;
    public static final int FUTURE_STATUS = 1;
    final String[] butTitles = {"(已结束)", "(正在进行)", "(未进行)"};
    final static int FIGHT_DES_REQUIR = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgroup_league);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onDestroy() {
        leagueStatusChanged(LeagueActivity.statusType);
        JsonObject params = new JsonObject();
        JsonArray rounds = new JsonArray();
        if(!currentRound.equals(new JsonObject()))
            rounds.add(currentRound);
        if(!pastRound.equals(new JsonObject()))
            rounds.add(pastRound);
        if(!futureRound.equals(new JsonObject()))
            rounds.add(futureRound);
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.add("rounds", rounds);
        leagueStatusSetTask  = new LeagueStatusSetTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(LeagueActivity.this);
                    return;
                }

                JsonObject result = (JsonObject) Response;
                Boolean success = result.get("result").getAsBoolean();
                if(!success) {                                          // success
                    GlobalVars.showErrAlert(LeagueActivity.this);
                    return;
                } else {                                                // failure

                }
            }
        });
        super.onDestroy();
    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        tgroupID = intent.getStringExtra(Constant.TGROUP_ID);
    }

    public void getViewByID () {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvStatusTitle = (TextView) findViewById(R.id.tv_status_title);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.tgroup_prepare_title));
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.img_right)).setImageResource(R.drawable.setting_btn_icon);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.league_main_title));
    }

    public void initializeData () {
        // init values
        currentRound = new JsonObject();
        pastRound = new JsonObject();
        futureRound = new JsonObject();;
        tGroupMemberModel = new TGroupMemberModel(this, tgroupID);

        // set up Adapter
        recyclerAdapter = new LeagueAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        this.getLeagues();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_right).setOnClickListener(this);

        // segment Buttons action
        findViewById(R.id.btnPast).setOnClickListener(this);
        findViewById(R.id.btnCurrent).setOnClickListener(this);
        findViewById(R.id.btnFuture).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되돌이단추를 누를 때 처리
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.img_right:                    // 대전 설정단추를 누를 때 처리
                Utils.start_Activity(this, LeagueSettingActivity.class,
                        new BasicNameValuePair(Constant.TGROUP_ID, this.tgroupID));
                break;
            case R.id.btnPast:                      // 이미 진행된 대전 보기
                leagueStatusChanged(PAST_STATUS);
                break;
            case R.id.btnCurrent:                   // 현재 진행중인 대전 보기 (기정)
                leagueStatusChanged(CURRENT_STATUS);
                break;
            case R.id.btnFuture:                    // 미진행인 대전 보기
                leagueStatusChanged(FUTURE_STATUS);
                break;
        }
    }

    public void getLeagues () {
        leagueStatusGetTask = new LeagueStatusGetTask(this, tgroupID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(LeagueActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                try {
                    currentRound = result.getAsJsonObject("current");
                }catch (Exception e) {}
                try {
                    pastRound = result.getAsJsonObject("past");
                }catch (Exception e) {}
                try{
                    futureRound = result.getAsJsonObject("future");
                }catch (Exception e) {}
                leagueStatusChanged(CURRENT_STATUS);
            }
        });
    }

    public void leagueStatusChanged(int statusType) {
        LeagueActivity.statusType = statusType;

        // update data
        int oldStatus = recyclerAdapter.getStatusType();
        JsonArray tempData = recyclerAdapter.getRoundData();
        if(tempData.size() != 0)
        switch (oldStatus) {
            case PAST_STATUS:                            // 이미 진행
                pastRound.add("fights", tempData);
                break;
            case CURRENT_STATUS:                             // 현재 진행중
                currentRound.add("fights", tempData);
                break;
            case FUTURE_STATUS:                             // 미 진행
                futureRound.add("fights", tempData);
                break;
            default:
                break;
        }

        // update view
        JsonArray data = new JsonArray();
        String roundTitle = "";
        try {
            switch (statusType) {
                case PAST_STATUS:
                    data = pastRound.getAsJsonArray("fights");
                    roundTitle = LeagueActivity.convertstrForRound(pastRound.get("round_num").getAsInt()) + butTitles[0];
                    LeagueActivity.leagueID = pastRound.get("tleague_id").getAsString();
                    LeagueActivity.roundID = pastRound.get("_id").getAsString();
                    break;
                case CURRENT_STATUS:
                    data = currentRound.getAsJsonArray("fights");
                    roundTitle = LeagueActivity.convertstrForRound(currentRound.get("round_num").getAsInt()) + butTitles[1];
                    LeagueActivity.leagueID = currentRound.get("tleague_id").getAsString();
                    LeagueActivity.roundID = currentRound.get("_id").getAsString();
                    break;
                case FUTURE_STATUS:
                    data = futureRound.getAsJsonArray("fights");
                    roundTitle = LeagueActivity.convertstrForRound(futureRound.get("round_num").getAsInt()) + butTitles[2];
                    LeagueActivity.leagueID = futureRound.get("tleague_id").getAsString();
                    LeagueActivity.roundID = futureRound.get("_id").getAsString();
                    break;
                default:
                    break;
            }
        }catch (Exception e) {
            data = new JsonArray();
        }
        if(roundTitle.equals("")) {
            roundTitle = getString(R.string.league_rounds_empty);
        }
        tvStatusTitle.setText(roundTitle);
        recyclerAdapter.notifyData(data, statusType);
    }

    public void onFightDesAction (int position) {
        JsonObject fightDes = recyclerAdapter.getRoundData().get(position).getAsJsonObject();
        Utils.start_ActivityForResult(this, FightDescriptionActivity.class, FIGHT_DES_REQUIR,
                new BasicNameValuePair(Constant.DATA_KEY, fightDes.toString()),
                new BasicNameValuePair(Constant.FIHGT_INDEX_KEY, String.valueOf(position)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case FIGHT_DES_REQUIR:                          // 대전상황묘사 결과인 경우
                String info = data.getStringExtra(Constant.DATA_KEY);
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(info);
                this.recyclerAdapter.updateDataWithFightDes(jsonObject);
                break;
        }
    }

    public static String convertstrForRound (int round) {
        String result = "";
        String sh = "";
        String ch ="";
        int d, r, s = 1;
        d = round;
        while (d > 0) {
            r = d%10;
            switch (s) {
                case 1:
                    break;
                case 10:
                    sh = "十";
                    break;
                case 100:
                    sh = "百";
                    break;
                case 1000:
                    sh = "千";
                    break;
                case 10000:
                    sh = "万";
                    break;
                default:
                    break;
            }
            switch (round) {
                case 1:
                    ch = "一";
                    break;
                case 2:
                    ch = "二";
                    break;
                case 3:
                    ch = "三";
                    break;
                case 4:
                    ch = "四";
                    break;
                case 5:
                    ch = "五";
                    break;
                case 6:
                    ch = "六";
                    break;
                case 7:
                    ch = "七";
                    break;
                case 8:
                    ch = "八";
                    break;
                case 9:
                    ch = "九";
                    break;
                default:
                    break;
            }
            s *= 10;
            d /= 10;
            result = ch + sh + result;
        }
        result = "第" + result + "轮";
        return result;
    }
}
