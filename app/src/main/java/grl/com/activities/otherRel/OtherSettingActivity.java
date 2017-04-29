package grl.com.activities.otherRel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import grl.com.activities.discovery.order.ReportActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.otherRel.DisconnectRelationTask;
import grl.com.httpRequestTask.otherRel.GetComValueTask;
import grl.com.httpRequestTask.otherRel.SetComValueTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class OtherSettingActivity extends Activity implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{

    // view
    TextView tvDescription;
    Switch switchConnection;
    Switch switchBlock;
    Switch switchDnd;
    TextView tvPost;
    LinearLayout layoutSetting;

    // task
    GetComValueTask getComValueTask;
    DisconnectRelationTask disconnectRelationTask;
    SetComValueTask setComValueTask;

    // require
    String strBack;
    String userID;
    String isConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_setting);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onDestroy() {
        if(switchConnection.isChecked()) {
            disconnectionAction();
        }
        settingComValues();
        super.onDestroy();
    }

    public void getDataFromIntent () {
        Intent intent = this.getIntent();
        this.strBack = intent.getStringExtra(Constant.BACK_TITLE_ID);
        this.userID = intent.getStringExtra("user_id");
        this.isConnection = intent.getStringExtra(Constant.REL_FLAG);
    }

    public void getViewByID () {
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        switchConnection = (Switch) findViewById(R.id.switch_connection);
        switchBlock = (Switch) findViewById(R.id.switch_block);
        switchDnd = (Switch) findViewById(R.id.switch_dnd);
        tvPost = (TextView) findViewById(R.id.tvPost);
        layoutSetting = (LinearLayout) findViewById(R.id.layoutConnection);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBack);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_setting_title));
    }

    public void initializeData () {
        if(this.isConnection.equals(Constant.REL_CONNECTED)) {                      // 상대방과 이미 관계를 맺었을 때의 처리 진행
           getRelStatus();
        } else  {                                                                   // 상대방과의 아무러한 관계도 존재하지 않는다.
            layoutSetting.setVisibility(View.GONE);
        }
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.tvPost).setOnClickListener(this);
        findViewById(R.id.tvDescription).setOnClickListener(this);

        switchConnection.setOnCheckedChangeListener(this);
        switchBlock.setOnCheckedChangeListener(this);
        switchDnd.setOnCheckedChangeListener(this);
    }

    public void getRelStatus () {
        // send request
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("other_userid", userID);
        getComValueTask = new GetComValueTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherSettingActivity.this);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    JsonObject mySetting = result.get("my_setting").getAsJsonObject();
                    int blockValue = mySetting.get("block").getAsInt();
                    int dndValue = mySetting.get("dnd").getAsInt();
                    if(blockValue == 1) {
                        switchBlock.setChecked(true);
                    } else {
                        switchBlock.setChecked(false);
                    }
                    if(dndValue == 1) {
                        switchDnd.setChecked(true);
                    } else {
                        switchDnd.setChecked(false);
                    }
                }catch (Exception e) {}
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
            case R.id.tvPost:                       // 신고단추를 누른 경우
                Utils.start_Activity(this, ReportActivity.class);
                break;
            case R.id.tvDescription:                 // 상대방묘사 단추를 누른 경우
                Utils.start_Activity(this, OtherDescriptionActivity.class,
                        new BasicNameValuePair("user_id", userID));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_connection:            // 상대방과 모든 관계를 해제한다.
                if(switchConnection.isChecked())
                    showRelationAlert();
                break;
            case R.id.switch_block:                 // 상대방을 블로크리스트에 추가한다.
                if(switchBlock.isChecked())
                    showBlockAlert();
                break;
            case R.id.switch_dnd:                   // dnd를 설정한다.
                if(switchDnd.isChecked())
                    showDisturbAlert();
                break;
        }
    }

    public void showRelationAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.connection_title));
        builder.setMessage("解除关系: 师徒关系/贵人关系");
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchConnection.setChecked(true);
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchConnection.setChecked(false);
            }
        });
        AlertDialog dlg =  builder.create();
        dlg.show();
    }

    public void showDisturbAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.relation_dnd));
        builder.setMessage("加黑名单, 向您发送邮件和发送邮件拦截");
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchDnd.setChecked(true);
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchDnd.setChecked(false);
            }
        });
        AlertDialog dlg =  builder.create();
        dlg.show();
    }

    public void showBlockAlert () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.relation_block));
        builder.setMessage("免打扰, 从发送消息您阻止，但您可以发送邮件");
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchBlock.setChecked(true);
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchBlock.setChecked(false);
            }
        });
        AlertDialog dlg =  builder.create();
        dlg.show();
    }

    public void disconnectionAction () {
        // send request
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("other_userid", userID);
        disconnectRelationTask = new DisconnectRelationTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherSettingActivity.this);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    Boolean res_flag = result.get("result").getAsBoolean();
                    if(res_flag) {                      // success

                    } else {                            // failure

                    }
                }catch (Exception e) {}
            }
        });
    }

    public void settingComValues () {
        int blockValue = 0;
        int dndValue = 0;
        if(switchBlock.isChecked()) {
            blockValue = 1;
        }
        if(switchDnd.isChecked()) {
            dndValue = 1;
        }
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("other_userid", userID);
        params.addProperty("block", blockValue);
        params.addProperty("dnd", dndValue);
        setComValueTask = new SetComValueTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherSettingActivity.this);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                } catch (Exception e) {}
            }
        });
    }
}
