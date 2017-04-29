package grl.com.subViews.dialogues;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.discovery.DiscoverySettingActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/4/2016.
 */
public class DirectionDialog extends Activity implements View.OnClickListener{
    // view
    ViewGroup layoutContent;

    RadioButton eastBtn;
    RadioButton eastSouthBtn;
    RadioButton southBtn;
    RadioButton westSouthBtn;
    RadioButton westBtn;
    RadioButton westNorthBtn;
    RadioButton northBtn;
    RadioButton eastNorthBtn;

    private int direction = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direction_view);

        Intent intent = getIntent();
        direction = intent.getIntExtra(Constant.ORDER_DIRECTION, -1);

        getViewByID();
        initializeData();
        setOnListener();

        resetChecked();
    }

    public void getViewByID () {
        eastBtn = (RadioButton)findViewById(R.id.btn_east);
        eastSouthBtn = (RadioButton)findViewById(R.id.btn_east_south);
        southBtn = (RadioButton)findViewById(R.id.btn_south);
        westSouthBtn = (RadioButton)findViewById(R.id.btn_west_south);
        westBtn = (RadioButton)findViewById(R.id.btn_west);
        westNorthBtn = (RadioButton)findViewById(R.id.btn_west_north);
        northBtn = (RadioButton)findViewById(R.id.btn_north);
        eastNorthBtn = (RadioButton)findViewById(R.id.btn_east_north);
    }

    public void initializeData () {
        // init view
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void setOnListener () {
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);


        eastBtn.setOnClickListener(this);
        eastSouthBtn.setOnClickListener(this);
        southBtn.setOnClickListener(this);
        westSouthBtn.setOnClickListener(this);
        westBtn.setOnClickListener(this);
        westNorthBtn.setOnClickListener(this);
        northBtn.setOnClickListener(this);
        eastNorthBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_ok: {                          // 확인단추를 누를 때의 처리
                Intent data = new Intent();
                data.putExtra(Constant.ORDER_DIRECTION, direction);
                setResult(RESULT_OK, data);
                finish();
                break;
            }
            case R.id.btn_cancel: {                           // 확인단추를 누를 때의 처리
                Intent data = new Intent();
                data.putExtra(Constant.ORDER_DIRECTION, direction);
                setResult(RESULT_CANCELED, data);
                finish();
                break;
            }
            case R.id.btn_east:
                direction = 0;
                resetChecked();
                break;
            case R.id.btn_east_south:
                direction = 1;
                resetChecked();
                break;
            case R.id.btn_south:
                direction = 2;
                resetChecked();
                break;
            case R.id.btn_west_south:
                direction = 3;
                resetChecked();
                break;
            case R.id.btn_west:
                direction = 4;
                resetChecked();
                break;
            case R.id.btn_west_north:
                direction = 5;
                resetChecked();
                break;
            case R.id.btn_north:
                direction = 6;
                resetChecked();
                break;
            case R.id.btn_east_north:
                direction = 7;
                resetChecked();
                break;
        }

    }

    public void resetChecked() {
        eastBtn.setChecked(false);
        eastSouthBtn.setChecked(false);
        southBtn.setChecked(false);
        westSouthBtn.setChecked(false);
        westBtn.setChecked(false);
        westNorthBtn.setChecked(false);
        northBtn.setChecked(false);
        eastNorthBtn.setChecked(false);
        switch(direction) {
            case 0:
                eastBtn.setChecked(true);
                break;
            case 1:
                eastSouthBtn.setChecked(true);
                break;
            case 2:
                southBtn.setChecked(true);
                break;
            case 3:
                westSouthBtn.setChecked(true);
                break;
            case 4:
                westBtn.setChecked(true);
                break;
            case 5:
                westNorthBtn.setChecked(true);
                break;
            case 6:
                northBtn.setChecked(true);
                break;
            case 7:
                eastNorthBtn.setChecked(true);
                break;
        }
    }
}
