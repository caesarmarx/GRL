package grl.com.activities.loginRigister;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.justalk.cloud.juslogin.LoginDelegate;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcDiag;
import com.justalk.cloud.lemon.MtcIm;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import grl.com.App;
import grl.com.activities.MainActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Signer;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.FactorModel;
import grl.com.httpRequestTask.popularity.TGroupFactorListTask;
import grl.com.httpRequestTask.user.LoginTask;
import grl.com.httpRequestTask.user.UpdateLocationTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/6/2016.
 */
public class LoginActivity extends Activity implements View.OnClickListener, AMapLocationListener ,LoginDelegate.Callback {

    static boolean bFirst = true;
    String phoneNumber;
    String password;

    Button loginBtn;
    Button registerBtn;
    Button passForgetBtn;
    Button loginProblemBtn;

    EditText userPhone;
    EditText userPassword;

    private AMapLocationClientOption locationOption = null;
    private AMapLocationClient locationClient = null;

    private static BroadcastReceiver mDiagTptStatisticsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("userPhone");
        password = intent.getStringExtra("userPassword");

        if (bFirst) {
            phoneNumber = GlobalVars.getPhoneNumberFromPre(LoginActivity.this);
            password = GlobalVars.getPasswordFromPre(LoginActivity.this);
            bFirst = false;
        }

        // get views
        userPhone = (EditText)findViewById(R.id.userPhone);
        userPassword = (EditText)findViewById(R.id.userPassword);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        passForgetBtn = (Button)findViewById(R.id.passForgetBtn);
        loginProblemBtn = (Button)findViewById(R.id.loginProblemBtn);

        //add actions
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        passForgetBtn.setOnClickListener(this);
        loginProblemBtn.setOnClickListener(this);

        // Location Reset
        SelfInfoModel.reset();

        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(this);
        locationClient.startLocation();


        // Chat Reset
        LoginDelegate.setCallback(this);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (mDiagTptStatisticsReceiver == null){
            mDiagTptStatisticsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int sendBw = 0;
                    int recvBw = 0;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        sendBw = json.getInt(MtcDiag.MtcDiagSendBandwidthKey);
                        recvBw = json.getInt(MtcDiag.MtcDiagReceiveBandwidthKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            };
            broadcastManager.registerReceiver(mDiagTptStatisticsReceiver, new IntentFilter(MtcDiag.MtcDiagTptTestStatisticsNotification));
        }

        if (phoneNumber != null && password != null && !phoneNumber.isEmpty() && !password.isEmpty()) {
            userPhone.setText(phoneNumber);
            userPhone.setSelection(phoneNumber.length());
            userPassword.setText(password);
            userPassword.setSelection(password.length());
            loginAction();
        }
        String result =  sHA1(this);
        setOnListener();

        GlobalVars.realTimeOrder = new ArrayList<grl.com.dataModels.OrderEntireModel>();
        GlobalVars.bkOrderList = new ArrayList<grl.com.dataModels.BackgroundOrderModel>();
        getFactorAction();

        // Screen Resolution Init
        GlobalVars.initResolution(this);
    }


    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        LoginDelegate.enterBackground();
    }

    protected void onResume() {
        super.onResume();
        LoginDelegate.enterForeground();
    }

    public void testingData (Boolean flag) {
        if(!flag)
            return;
        this.userPhone.setText("12345678907");
        this.userPassword.setText("1");
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                loginAction();
                break;
            case R.id.registerBtn:
                registerAction();
                break;
            case R.id.loginProblemBtn:
                loginProblemAction();
                break;
            case R.id.passForgetBtn:
                passForgetAction();
                break;
        }
    }

    private void getFactorAction() {
        GlobalVars.factorList = new ArrayList<FactorModel>();
        new TGroupFactorListTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(LoginActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        FactorModel model = gson.fromJson(object.toString(), FactorModel.class);
                        GlobalVars.factorList.add(model);
                    }

                } catch (Exception e) {

                }
            }
        });
    }
    private void loginAction() {

        final String userPhone = this.userPhone.getText().toString();
        final String userPw = this.userPassword.getText().toString();

        new LoginTask(this, userPhone, userPw, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(LoginActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    Boolean login_result = result.getBoolean("login_result");
                    if(!login_result) {
                        GlobalVars.showErrAlert(LoginActivity.this);
                        return;
                    }

                    SelfInfoModel.parseFromJson(result);

                    // Location Update
//                    updateLocation();

                    // Login Success
                    GlobalVars.setPasswordToPre(LoginActivity.this, userPw);
                    GlobalVars.setPhoneNumberToPre(LoginActivity.this, userPhone);

                    if (LoginDelegate.login(SelfInfoModel.userID, SelfInfoModel.userPass, SelfInfoModel.chatServer)) {

                    }
                    Utils.start_Activity(LoginActivity.this, MainActivity.class);

                } catch (Exception e) {

                }
            }
        });
    }

    private void registerAction() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void passForgetAction() {
        Intent intent = new Intent(this, PNumberActivity.class);
        startActivity(intent);
    }

    private void loginProblemAction() {

    }

    public void setOnListener() {
        userPhone.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    return true;
                }
                return false;
            }
        });
        userPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    loginAction();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation.getLatitude() < Double.MIN_NORMAL ||
                aMapLocation.getLongitude() < Double.MIN_NORMAL)
            return;

        double offsetLatitude = SelfInfoModel.latitude - aMapLocation.getLatitude();
        double offsetLongitude = SelfInfoModel.longitude - aMapLocation.getLongitude();
        if (Math.abs(offsetLatitude) > 0.001 || Math.abs(offsetLongitude) > 0.001) {

            SelfInfoModel.latitude = aMapLocation.getLatitude();
            SelfInfoModel.longitude = aMapLocation.getLongitude();
            SelfInfoModel.posArea.put("citycode", aMapLocation.getCityCode());
            SelfInfoModel.posArea.put("adcode", aMapLocation.getAdCode());
            SelfInfoModel.posArea.put("province", aMapLocation.getProvince());
            SelfInfoModel.posArea.put("city", aMapLocation.getCity());
            SelfInfoModel.posArea.put("district", aMapLocation.getDistrict());
            updateLocation();
        }
    }

    public void updateLocation() {
        if (SelfInfoModel.userID == null || SelfInfoModel.userID.length() == 0)
            return;
        new UpdateLocationTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

            }
        });
    }

    public static String sHA1 (Context context) {
        try {
            PackageInfo info = context.getPackageManager ().getPackageInfo(
                    context.getPackageName (), PackageManager.GET_SIGNATURES);
            byte [] cert = info.signatures [0] .toByteArray ();
            MessageDigest md = MessageDigest.getInstance ( "SHA1");
            byte [] publicKey = md.digest (cert);
            StringBuffer hexString = new StringBuffer ();
            for (int i = 0; i <publicKey.length; i ++) {
                String appendString = Integer.toHexString (0xFF & publicKey [i])
                        .toUpperCase (Locale.CHINA);
                if (appendString.length () == 1)
                    hexString.append ( "0");
                hexString.append (appendString);
                hexString.append ( ":");
            }
            return hexString.toString ();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        }
        return null;
    }


    /**
     * User Login Chat
     */

    @Override
    public void mtcLoginOk() {
        App app = (App)App.getInstance();
        app.registerReceivers();
        MtcIm.Mtc_ImRefresh();
//        Utils.start_Activity(LoginActivity.this, MainActivity.class);
//        MiPush.start(getApplicationContext(), getString(R.string.MiPush_AppId), getString(R.string.MiPush_AppKey));
//        Gcm.start(getApplicationContext(), getString(R.string.gcm_sender_id));
    }

    @Override
    public void mtcLoginDidFail() {
        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mtcLogoutOk() {
//        MiPush.stop(getApplicationContext());
//        Gcm.stop(getApplicationContext());
    }

    @Override
    public void mtcLogouted() {
//        MiPush.stop(getApplicationContext());
//        Gcm.stop(getApplicationContext());
//        Intent i = new Intent(this, LoginActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(i);
    }

    @Override
    public void mtcAuthRequire(String id, String nonce) {
        String key =
                "MIIEpAIBAAKCAQEAtzJF3SdDDZNuMit61hrIrDNROPhCnma9lp1gPaBF0tZGvn2X" + "\n" +
                        "GQWtlrQ1XEGbfTrpN1whsVB/e8SBWdmNHwE6Ze67fzYAw8B5Ul+bkO4lx79LL2If" + "\n" +
                        "n2oQ6doW9NYCCFt0CHv4A9esU1zB8SNKZazKfU/u3n/UEEInd/cJ/vMiuAWGSSPa" + "\n" +
                        "wLCqJJT7Ly+/Cgq9vK4jdX0YohBA7/ZSr7jx+9Zs2Lj4/L+y6lKR6UdXoTY0nJKf" + "\n" +
                        "jSEZCxwFCPh57snvg90fDyizn58EI1dZ977+bG5oD1zE2O4CmhLaX4tQiQCioZeP" + "\n" +
                        "D+iHTsXWYP9u8l2J/PBxVObqLBPAcqV4UyslZwIDAQABAoIBADd9X9IUEWhsTsWd" + "\n" +
                        "i/CMXlpilOinsi4eurCDbOJdyKiLRRRwIDNxF9p9LWiLatis3nVpT79Qvby0keWw" + "\n" +
                        "UuGgUpsLi/mFVwf0JguAcDOfHwx48gIhO6jizMq4x5lTtXvoj6X+PuqTClyZzRkI" + "\n" +
                        "coGHrDH240i7+XUPRLs+teVmqg6JAlVh2t3WjI7967I1wgzywchFWMSTftilULjl" + "\n" +
                        "7NKZEn0anDIJoN4Rgy0KSX9pzHHaEMmkD9bdpx/XlaXjaWpEfLB9frl8XEweixo8" + "\n" +
                        "R55Hpowk/Q4Qk73+xTvUmSO1XktyEWAumGgF1jWla5Z/5D3CadSgfJcfeePMXxCy" + "\n" +
                        "YC7TSxECgYEA6G/nOnrzNMe0dem8nt++86LOPn1wKqr0FV+AelNM2gzsoMtTUazX" + "\n" +
                        "C5Jyt6Xdq8+Y/xQ8WXmRziXlAS5753RenHbR9pY5XXP0AeMvMW4tlngGtKk0c/Rt" + "\n" +
                        "2W+eF7EI+sgI9g9TMRPjbvUGQDZl8gr2a94Q6u15tgYFJpjx8A3d1akCgYEAycR/" + "\n" +
                        "XCwVnGRqQTRA3cRhfz0usF3pyESqOoVfzzK0bSyOJAv7FOTd0V1MNhzwoWPwdTrl" + "\n" +
                        "5CGzTGxyaHZpi+sRVg5X/XolYwjgXrFKCckxmnVYlYFY30iegJfu3GR6phyTlwgc" + "\n" +
                        "nSQ2vfSseLnHTFDlrBEy/56H2MoQ4qXad5Sh7I8CgYEAsbDijyVxCbdl8QJ37OjV" + "\n" +
                        "vMGIc+NHPYclQ7WXrWxDAysANshZcMX2O+WAB38ooHD64H3iyPAUFAmKMUYM+NtQ" + "\n" +
                        "fMKlLqKXRicfsdWwvVQiS7aEQdZcwAxrcd9Pd4Mifz0vBJSgn5M5uhhc5/fuJYRV" + "\n" +
                        "8A561m4nLo0ZoPEpe7/OB8kCgYBvsplzNHCOUMTF7iCO5N24q+1B8+utU94NYbLF" + "\n" +
                        "qONboRPbfsp0KbNm6Uh8mI7aOdJvg7irD8EL6Ol5TTxnGi5RvsUVbV5vMgXMRkef" + "\n" +
                        "nUMZqCbvNVk22yPsOrAgUHvZo+5M6U+16stnY6FrgCWF6S8Mj8T04BWCfXLVlk2Y" + "\n" +
                        "b68onwKBgQDN2yjNq+B3FBI91DQGoHPWqsSbMPed/TJ6pGiSftKeckyA35tQVMAO" + "\n" +
                        "JoBCLU0G5Z2KWuqIFphjcg+YjR/Agsu/0v7ZRVyU1bDjmHW7MtBOLXPTB2QB8DF7" + "\n" +
                        "SeCrYJLUItyJaqQmxAaGQtDp8+dqm0BywAwoB2AgqDjXhph1xWMQIA==" + "\n";

        String code = Signer.signWithKey(key, id, nonce, 3600);
        LoginDelegate.promptAuthCode(code);
    }
}
