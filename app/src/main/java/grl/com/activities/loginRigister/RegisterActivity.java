package grl.com.activities.loginRigister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.user.RegisterTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class RegisterActivity extends Activity implements View.OnClickListener {

    Button nextBtn;
    Button grlRuleBtn;
    Button serviceAgreeBtn;
    EditText userPhone;
    ImageView imgBack;
    TextView textBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userPhone = (EditText)findViewById(R.id.userRegisterPhone);
        nextBtn = (Button)findViewById(R.id.registerNextBtn);
        grlRuleBtn = (Button)findViewById(R.id.userGRLRule);
        serviceAgreeBtn = (Button)findViewById(R.id.userServerAgree);

        //add actions
        nextBtn.setOnClickListener(this);
        grlRuleBtn.setOnClickListener(this);
        serviceAgreeBtn.setOnClickListener(this);

        userPhone.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    String phoneNumber = userPhone.getText().toString();
                    if (!phoneNumber.startsWith("1")) {
                        nextBtn.setEnabled(false);
                    }
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        nextAction();
                        return true;
                    }
                }
                return false;
            }
        });

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_back);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerNextBtn:
                nextAction();
                break;
            case R.id.userGRLRule:
                break;
            case R.id.userServerAgree:
                break;
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_back:
                Utils.finish(this);
                break;
        }
    }

    public void nextAction() {
        final String phoneNumber = userPhone.getText().toString();
        if (phoneNumber.length() != 11)
            return;
        if (!nextBtn.isEnabled())
            return;

        HashMap<String, String> regArea = new HashMap<String, String>();
        regArea.put("citycode", "");
        regArea.put("adcode", "");
        regArea.put("province", "");
        regArea.put("city", "");
        regArea.put("district", "");

        new RegisterTask(this, phoneNumber, regArea, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(RegisterActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    Boolean register_result = result.getBoolean("register_result");
                    if(!register_result) {
                        GlobalVars.showErrAlert(RegisterActivity.this);
                        return;
                    }
                    String verifyCode = result.getString("user_code");

                    Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                    intent.putExtra("userPhone", phoneNumber);
                    intent.putExtra("verifyCode", verifyCode);
                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        });

    }
}
