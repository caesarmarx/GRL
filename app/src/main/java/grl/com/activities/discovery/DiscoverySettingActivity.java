package grl.com.activities.discovery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.activities.discovery.donate.DonateMainActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.discovery.UserStatusGetTask;
import grl.com.httpRequestTask.discovery.UserStatusSetTask;
import grl.com.httpRequestTask.order.PlanetUserTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.star.PlanetShowView;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class DiscoverySettingActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    // Views
    Switch switchStatus;
    TextView tvStatus;
    ImageView imgUserPhoto;
    PlanetShowView planetView;

    // Response values;
    Integer userEnableValue;
    private ArrayList<UserModel> planetUsers;

    // Tasks
    UserStatusGetTask userStatusGetTask;
    UserStatusSetTask userStatusSetTask;

    // values
    public final static String[] statusArray = {"随时候命", "can I help you", "全力以赴"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_setting);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.setOnListener();
    }

    @Override
    protected void onPause() {
        this.updateUserStatus();
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.initializeData();
        super.onResume();
    }

    public void getDataFromIntent () {

    }

    public void getViewByID () {
        this.switchStatus = (Switch) findViewById(R.id.switch_status);
        this.tvStatus = (TextView) findViewById(R.id.tvStatus);
        this.planetView = (PlanetShowView)findViewById(R.id.planet_view);
        this.imgUserPhoto = (ImageView) findViewById(R.id.img_user_photo);
    }
    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.tab_discorvery_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.me_title));
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.tvInfo).setOnClickListener(this);
//        findViewById(R.id.tvMyOrder).setOnClickListener(this);
//        findViewById(R.id.tvMyCheer).setOnClickListener(this);
        findViewById(R.id.tvDonate).setOnClickListener(this);
        findViewById(R.id.tvSetting).setOnClickListener(this);

        switchStatus.setOnCheckedChangeListener(this);
    }

    public void initializeData() {

        // get data from server
        userStatusGetTask = new UserStatusGetTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(DiscoverySettingActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                userEnableValue = result.getInt("user_status");
                InitViews();
            }
        });

        // init value
        planetUsers = new ArrayList<UserModel>();
        // init view
        GlobalVars.loadImage(imgUserPhoto, SelfInfoModel.userPhoto);
        planetView.initView();
        refreshPlanet();
    }

    private void refreshPlanet() {
        new PlanetUserTask(DiscoverySettingActivity.this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(DiscoverySettingActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    planetUsers.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        UserModel model = new UserModel();
                        model.parseFromJson(object);
                        model.state = "";
                        planetUsers.add(model);
                    }
                } catch (Exception ex) {

                }
                planetView.refreshData(planetUsers);
            }
        });
    }

    public void InitViews () {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String strStatus = "";
                switch (userEnableValue) {
                    case 0:
                        switchStatus.setChecked(false);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        switchStatus.setChecked(true);
                        strStatus = statusArray[userEnableValue - 1];
                        break;
                    default:
                        break;
                }
                tvStatus.setText(strStatus);
            }
        });
    }

    public static String getStatus(int status) {
        String result = "跑开";
        switch (status) {
            case 1:
            case 2:
            case 3:
                result = statusArray[status - 1];
                break;
            default:
                break;
        }
        return  result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.tvInfo:                       // 나의 정보보기 단추를 누른 경우
                Utils.start_Activity(this, MyInfoSettingActivity.class);
                break;
//            case R.id.tvMyOrder:                    // 나의 령보기 단추를 누른 경우
//                Utils.start_Activity(this, MyOrderActivity.class);
//                break;
//            case R.id.tvMyCheer:                    // 나의 인기 보기 단추를 누른 경우
//                break;
            case R.id.tvDonate:                     // 나의 기부통 보기 단추를 누른 경우
                Utils.start_Activity(this, DonateMainActivity.class);
                break;
            case R.id.tvSetting:                    // 나의 설정보기 단추를 누른 경우
                Utils.start_Activity(this, SetttingActivity.class);
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_status:                // 사용자설정 단추를 누를 때의 처리 진행
                if(isChecked) {
                    this.showUserStatusAlert();
                } else {
                    userEnableValue = 0;
                    tvStatus.setText("");
                }
                break;
        }
    }

    public void showUserStatusAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setSingleChoiceItems(this.statusArray, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                    case 1:
                    case 2:
                        userEnableValue = which + 1;
                        tvStatus.setText(statusArray[which]);
                        break;
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userEnableValue = 0;
                switchStatus.setChecked(false);
            }
        });
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void updateUserStatus () {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", SelfInfoModel.userID);
            params.put("user_status", userEnableValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userStatusSetTask = new UserStatusSetTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(DiscoverySettingActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                Boolean res_flag = result.getBoolean("result");
                if(res_flag) {                          // success

                } else {                                // failure

                }
            }
        });
    }
}
