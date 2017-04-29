package grl.com.activities.energy.tGroup.league;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.tGroup.LeagueResultSetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/25/2016.
 */
public class FightDescriptionActivity extends Activity implements View.OnClickListener{

    // view
    ImageView imgLeft;
    ImageView imgRight;
    TextView tvLeft;
    TextView tvRight;
    EditText etDes;
    EditText etInput;
    Button btnSend;

    // task
    LeagueResultSetTask leagueResultSetTask;
    // require
    JsonObject fightInfo;
    int indexNumber;

    // value
    Boolean isEditable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_des);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        String strInfo = intent.getStringExtra(Constant.DATA_KEY);
        fightInfo = (JsonObject) new JsonParser().parse(strInfo);
        indexNumber = Integer.valueOf(intent.getStringExtra(Constant.FIHGT_INDEX_KEY));
    }

    public void getViewByID () {
        imgLeft = (ImageView) findViewById(R.id.imgLeftPhoto);
        imgRight = (ImageView) findViewById(R.id.imgRightPhoto);
        tvLeft = (TextView) findViewById(R.id.txtLeft);
        tvRight = (TextView) findViewById(R.id.txtRight);
        etDes = (EditText) findViewById(R.id.etDes);
        etInput = (EditText) findViewById(R.id.etDesInput);
        btnSend = (Button) findViewById(R.id.btnSend);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.league_main_title));
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.league_fight_title));
    }

    public void initializeData () {
        // init value
        if(SelfInfoModel.userID.equals(fightInfo.get("left_player").getAsString()) && LeagueActivity.statusType == LeagueActivity.CURRENT_STATUS)
            isEditable = true;
        else
            isEditable = false;
        // init views
        String leftPhotoPath = LeagueActivity.tGroupMemberModel.getUserPhotoFromDiscipleID(fightInfo.get("left_player").getAsString());
        String rightPhotoPath = LeagueActivity.tGroupMemberModel.getUserPhotoFromDiscipleID(fightInfo.get("right_player").getAsString());
        String leftUserName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(fightInfo.get("left_player").getAsString());
        String rightUserName = LeagueActivity.tGroupMemberModel.getUserNameFromDiscipleID(fightInfo.get("right_player").getAsString());
        tvLeft.setText(leftUserName);
        tvRight.setText(rightUserName);
        etDes.setText(fightInfo.get("fight_comment").getAsString());
        GlobalVars.loadImage(imgLeft, leftPhotoPath);
        GlobalVars.loadImage(imgRight, rightPhotoPath);
        if(isEditable) {
            btnSend.setEnabled(true);
            etInput.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
            etInput.setEnabled(false);
        }
        fixSelectioin(etDes);
    }

    public void fixSelectioin(EditText et) {

        int position = et.length();
        Selection.setSelection(et.getText(), position);
    }
    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.imgRightPhoto).setOnClickListener(this);
        findViewById(R.id.imgLeftPhoto).setOnClickListener(this);
        findViewById(R.id.txtLeft).setOnClickListener(this);
        findViewById(R.id.txtRight).setOnClickListener(this);
        findViewById(R.id.tvEqual).setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되돌이 단추를 누를 때의 사건처리
            case R.id.txt_left:
                fightInfo.addProperty("fight_comment", etDes.getText().toString());
                Intent intent = new Intent();
                intent.putExtra(Constant.DATA_KEY, fightInfo.toString());
                setResult(Activity.RESULT_OK, intent);
                Utils.finish(this);
                break;
            case R.id.imgLeftPhoto:
            case R.id.txtLeft:                   // 좌방팀 승리
                showAlertDialog(tvLeft.getText().toString(), 3);
                break;
            case R.id.imgRightPhoto:
            case R.id.txtRight:                  // 우방팀 승리
                showAlertDialog(tvRight.getText().toString(), 0);
                break;
            case R.id.tvEqual:                      // 비김
                showAlertDialog(getString(R.string.league_equal_title), 1);
                break;
            case R.id.btnSend:                      // 대전 묘사진행단추 누르기
                onDessituation();
                break;
        }
    }

    public void showAlertDialog (String msg, final int result) {
        if(fightInfo.get("result").getAsInt() != -1)                // 이미 승부가 결정되였으면 다시 결정할수 없다.
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.league_reuslt_title));
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                fightInfo.addProperty("result", result);
                JsonObject params = new JsonObject();
                params.addProperty("tleague_id", LeagueActivity.leagueID);
                params.addProperty("tround_id", LeagueActivity.roundID);
                params.addProperty("left_player", fightInfo.get("left_player").getAsString());
                params.addProperty("right_player", fightInfo.get("right_player").getAsString());
                params.addProperty("result", result);
                params.addProperty("fights_index", indexNumber);
                params.addProperty("start_time", "");
                params.addProperty("end_time", "");
                setResultRequest(params);
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }

    public void onDessituation () {
        String comment = etDes.getText().toString() + "\n" + GlobalVars.getCurrentTime() +
                "\n" + etInput.getText().toString() + "\n";
        etInput.setText("");
        etDes.setText(comment);
        fixSelectioin(etDes);
    }

    public void setResultRequest(JsonObject params) {
        leagueResultSetTask = new LeagueResultSetTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(FightDescriptionActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean success = result.get("result").getAsBoolean();
                if(success) {          // success

                } else {                                            // failure

                }
            }
        });
    }
}
