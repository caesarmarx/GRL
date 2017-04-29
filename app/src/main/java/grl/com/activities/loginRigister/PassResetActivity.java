package grl.com.activities.loginRigister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.user.SetPasswordTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class PassResetActivity extends Activity implements View.OnClickListener{
    String phoneNumber;
    EditText passwordField;
    EditText confirmField;
    Button nextBtn;
    Button loginBtn;
    TextView repassComment;
    ImageView imgBack;
    TextView textBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repass);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("userPhone");

        passwordField = (EditText)findViewById(R.id.repass_password);
        confirmField = (EditText)findViewById(R.id.repass_confrim);
        nextBtn = (Button)findViewById(R.id.repassNextBtn);
        loginBtn = (Button)findViewById(R.id.repassLoginBtn);
        repassComment = (TextView)findViewById(R.id.repassComment);
        repassComment.setText(String.format("+86 %s + 贵人令密吗登录贵人令", phoneNumber));

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_back);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);
        setOnListener();
    }

    void setOnListener() {
        nextBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        confirmField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        nextAction();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repassNextBtn:
                nextAction();
                break;
            case R.id.repassLoginBtn:
                loginAction();
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
        String password = passwordField.getText().toString();
        String confirm = confirmField.getText().toString();
        if (password.compareTo(confirm) != 0) {
            return;
        }
        new SetPasswordTask(this, phoneNumber, password, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PassResetActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    Boolean login_result = result.getBoolean("register_result");
                    if(!login_result) {
                        GlobalVars.showErrAlert(PassResetActivity.this);
                        return;
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(PassResetActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmField.getApplicationWindowToken(), 0);
                    imm.hideSoftInputFromWindow(passwordField.getApplicationWindowToken(), 0);

                    nextBtn.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);

                } catch (Exception e) {

                }

            }
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(confirmField.getApplicationWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordField.getApplicationWindowToken(), 0);

        nextBtn.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
    }

    public void loginAction() {
        String password = passwordField.getText().toString();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userPhone", phoneNumber);
        intent.putExtra("userPassword", password);
        startActivity(intent);
    }
}
