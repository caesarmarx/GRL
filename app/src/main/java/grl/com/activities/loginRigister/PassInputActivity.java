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

public class PassInputActivity extends Activity implements View.OnClickListener{
    String phoneNumber;
    Button nextBtn;
    Button loginBtn;
    EditText passField;
    EditText confirmField;

    ImageView imgBack;
    TextView textBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("userPhone");

        passField = (EditText)findViewById(R.id.et_password);
        confirmField = (EditText)findViewById(R.id.et_repass);

        nextBtn = (Button)findViewById(R.id.passNextBtn);
        loginBtn = (Button)findViewById(R.id.passLoginBtn);

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_back);

        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);

        setOnListener();
    }

    void setOnListener() {
        confirmField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        passInput();
                        return true;
                    }
                }
                return false;
            }
        });
        nextBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    void passInput() {
        String password = passField.getText().toString();
        String confirm = confirmField.getText().toString();
        if (password.length() == 0 || confirm.length() == 0)
            return;
        if (password.compareTo(confirm) != 0)
            return;
        new SetPasswordTask(this, phoneNumber, password, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PassInputActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    Boolean login_result = result.getBoolean("register_result");
                    if(!login_result) {
                        GlobalVars.showErrAlert(PassInputActivity.this);
                        return;
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(PassInputActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passField.getApplicationWindowToken(), 0);

                    nextBtn.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);

                } catch (Exception e) {

                }

            }
        });

    }

    void login() {
        String password = passField.getText().toString();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("userPhone", phoneNumber);
        intent.putExtra("userPassword", password);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passNextBtn:
                passInput();
                break;
            case R.id.passLoginBtn:
                login();
                break;
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_back:
                Utils.finish(this);
                break;
        }
    }
}
