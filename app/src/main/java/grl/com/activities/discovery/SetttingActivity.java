package grl.com.activities.discovery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import grl.com.activities.discovery.PurchaseCoin.PurchaseActivity;
import grl.com.activities.discovery.profile.SelfProfileSettingActivity;
import grl.com.activities.loginRigister.LoginActivity;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class SetttingActivity extends Activity implements View.OnClickListener {

//    Switch switchStatus;
    Switch switchGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE );
        if (switchGps != null)
            switchGps.setChecked(manager.isProviderEnabled( LocationManager.GPS_PROVIDER ));
    }

    public void getDataFromIntent() {
        Intent intent = this.getIntent();
    }

    public void getViewByID () {
//        switchStatus = (Switch) findViewById(R.id.switch_status);
        switchGps = (Switch) findViewById(R.id.switch_gps);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.setting_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.me_title));
    }

    public void initializeData () {
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE );
        switchGps.setChecked(manager.isProviderEnabled( LocationManager.GPS_PROVIDER ));
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.tvStarSetting).setOnClickListener(this);
        findViewById(R.id.tvPurchase).setOnClickListener(this);
        findViewById(R.id.tvProfile).setOnClickListener(this);
        findViewById(R.id.tvLogout).setOnClickListener(this);

        switchGps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE );

                if ( isChecked == true && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    // Disable ->  Enable
                    buildAlertMessageNoGps();
                }
                if ( isChecked == false && manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageGps();
                }

            }
        });
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的位置似乎被禁用，你要启用?")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void buildAlertMessageGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您的位置似乎被启用，你要禁用它?")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.tvStarSetting:                // 귀성설정
                Utils.start_Activity(this, StarSettingActivity.class);
                break;
            case R.id.tvPurchase:                   // 령패구입
                Utils.start_Activity(this, PurchaseActivity.class);
                break;
            case R.id.tvProfile:                    // 사용자인증 비데오 올리적재
                Utils.start_Activity(this, SelfProfileSettingActivity.class);
                break;
            case R.id.tvLogout:                     // 가입탈퇴
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                break;
        }
    }

}
