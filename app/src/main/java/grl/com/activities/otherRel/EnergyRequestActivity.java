package grl.com.activities.otherRel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.adapters.otherRel.EnergyRequestAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.newEnergy.EnergyGetRelationshipTask;
import grl.com.httpRequestTask.newEnergy.EnergyRequestTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class EnergyRequestActivity extends Activity implements View.OnClickListener {

    RecyclerView recyclerView;
    EnergyRequestAdapter recyclerAdapter;
    TextView tvTeacher;
    TextView tvDisciple;
    TextView tvGrl;

    // required parameters
    String strBackTitle;
    String strGrlID;

    // tasks
    EnergyGetRelationshipTask energyGetRelationshipTask;
    EnergyRequestTask energyRequestTask;

    // response values
    Integer mRequestDisciple;
    Integer mRequestTeacher;
    Integer mRequestGrl;
    JSONArray groupList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_request);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNabBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getDataFromIntent () {
        Intent intent = this.getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        strGrlID = intent.getStringExtra("other_userid");
    }

    public void getViewByID () {
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.tvTeacher = (TextView) findViewById(R.id.tvTeacher);
        this.tvDisciple = (TextView) findViewById(R.id.tvDisciple);
        this.tvGrl = (TextView) findViewById(R.id.tvGrl);
    }

    public void initNabBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_request_title));
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(this.getString(R.string.request));
    }

    public void initializeData () {
        // setupAdapter
        recyclerAdapter = new EnergyRequestAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // init data
        mRequestTeacher = 1;
        mRequestDisciple = 1;
        mRequestGrl = 1;
        groupList = new JSONArray();

        // request server
        if(this.strGrlID.equals(""))
            return;
        energyGetRelationshipTask = new EnergyGetRelationshipTask(this, strGrlID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(EnergyRequestActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                try {
                    mRequestDisciple = result.getInt("request_disciple");
                    mRequestGrl = result.getInt("request_grl");
                    mRequestTeacher = result.getInt("request_teacher");
                    groupList = result.getJSONArray("fgroup");
                    InitViews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);

        tvDisciple.setOnClickListener(this);
        tvGrl.setOnClickListener(this);
        tvTeacher.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:                    // 능량 요청 단추를 누른 경우
                this.sendRequest();
                break;
            case R.id.tvTeacher:                    // 스승모시기 단추를 누른 경우
                Drawable[] drawables = tvTeacher.getCompoundDrawables();
                if(drawables[2] == null) {
                    tvTeacher.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_yellow, 0);
                } else {
                    tvTeacher.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                break;
            case R.id.tvGrl:                        // 귀인 모시기 단추를 누른 경우
                Drawable[] drawables1 = tvGrl.getCompoundDrawables();
                if(drawables1[2] == null) {
                    tvGrl.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_yellow, 0);
                } else {
                    tvGrl.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                break;
            case R.id.tvDisciple:                   // 제자받아들이기 단추를 누른 경웅
                Drawable[] drawables2 = tvDisciple.getCompoundDrawables();
                if(drawables2[2] == null) {
                    tvDisciple.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_yellow, 0);
                } else {
                    tvDisciple.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                break;
        }
    }

    public void InitViews () {
        if(mRequestTeacher == 0) {
            tvTeacher.setVisibility(View.GONE);
            findViewById(R.id.border_teacher).setVisibility(View.GONE);
        } else {
            tvTeacher.setVisibility(View.VISIBLE);
            findViewById(R.id.border_teacher).setVisibility(View.VISIBLE);
        }
        if(mRequestDisciple == 0) {
            tvDisciple.setVisibility(View.GONE);
            findViewById(R.id.border_disciple).setVisibility(View.GONE);
        } else {
            tvDisciple.setVisibility(View.VISIBLE);
            findViewById(R.id.border_disciple).setVisibility(View.VISIBLE);
        }
        if(mRequestGrl == 0) {
            tvGrl.setVisibility(View.GONE);
        } else {
            tvGrl.setVisibility(View.VISIBLE);
        }
        recyclerAdapter.myList = this.groupList;
        recyclerAdapter.notifyDataSetChanged();
    }

    public void sendRequest() {
        // 요청 파라메터
        String requestString = "";
        final JSONObject params = new JSONObject();
        String fgroupID = "";
        String fgroupName = "";
        Integer requestTeacher;
        Integer requestDisciple;
        Integer requestGrl;
        Drawable[] drawableTeacher = tvTeacher.getCompoundDrawables();
        Drawable[] drawableDisciple = tvDisciple.getCompoundDrawables();
        Drawable[] drawableGrl = tvGrl.getCompoundDrawables();

        if(this.recyclerAdapter.getSelectedIndex() == -1) {
            fgroupID = "";
            fgroupName = "";
        } else {
            try {
                JSONObject info = this.groupList.getJSONObject(recyclerAdapter.getSelectedIndex());
                fgroupID = info.getString("group_id");
                fgroupName = info.getString("group_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(drawableTeacher[2] == null) {
            requestTeacher = 1;
        } else {
            requestTeacher = 0;
            requestString = "+拜师";
        }
        if(drawableDisciple[2] == null) {
            requestDisciple = 1;
        } else {
            requestDisciple = 0;
            requestString = requestString + "+收徒";
        }
        if(drawableGrl[2] == null) {
            requestGrl = 2;
        } else {
            requestGrl = 0;
            requestString = requestString + "+拜贵人";
        }
        if(!fgroupID.equals("")) {
            requestString = requestString + "+贵人圈";
        }

        if(requestString.equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(EnergyRequestActivity.this)
                    .setTitle("选择邀加能量")
                    .setMessage("")
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return;
        }

        // generate request
        try {
            params.put("grl_id", strGrlID);
            params.put("fgroup_id", fgroupID);
            params.put("fgroup_name", fgroupName);
            params.put("request_teacher", requestTeacher);
            params.put("request_disciple", requestDisciple);
            params.put("request_grl", requestGrl);
            // 정확히 상대방에게 요청을 보내겠는지를 문의 한다.
            final String finalRequestString = requestString;
            new AlertDialog.Builder(EnergyRequestActivity.this)
                    .setTitle("邀加能量")
                    .setMessage(requestString)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            energyRequestTask = new EnergyRequestTask(EnergyRequestActivity.this, params, new HttpCallback() {
                                @Override
                                public void onResponse(Boolean flag, Object Response) throws JSONException {
                                    if(!flag || Response == null) {
                                        GlobalVars.showErrAlert(EnergyRequestActivity.this);
                                        return;
                                    }
                                    // parsing value from Result
                                    JSONObject result = (JSONObject) Response;
                                    Boolean requestResult = result.getBoolean("request_result");
                                    if(!requestResult) {            // 사용자와 이미 관계를 맺었다.
                                        // do your coding
                                        return;
                                    }
                                    String toastMsg = "" + finalRequestString + "";
                                    Toast.makeText(EnergyRequestActivity.this, toastMsg, Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.finish(EnergyRequestActivity.this);
                                }
                            });
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
