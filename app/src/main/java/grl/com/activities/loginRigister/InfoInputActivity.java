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
import grl.com.httpRequestTask.user.RegisterInfoTask;
import grl.com.httpRequestTask.user.RegisterTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class InfoInputActivity extends Activity implements View.OnClickListener {

    Button nextBtn;

    EditText userNameEdit;
    EditText userSkillEdit;

    ImageView imgBack;
    TextView textBack;

    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_register);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("userPhone");

        userNameEdit = (EditText)findViewById(R.id.et_user_name);
        userSkillEdit = (EditText)findViewById(R.id.et_user_skill);
        nextBtn = (Button)findViewById(R.id.btn_next);

        //add actions
        nextBtn.setOnClickListener(this);

        userSkillEdit.setOnKeyListener(new OnKeyListener() {
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

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_back);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                nextAction();
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
        final String userName = userNameEdit.getText().toString();
        final String userSkill = userSkillEdit.getText().toString();
        if (userName.isEmpty() || userSkill.isEmpty())
            return;


        new RegisterInfoTask(this, phoneNumber, userName, userSkill, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(InfoInputActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    Boolean register_result = result.getBoolean("register_result");
                    if(!register_result) {
                        GlobalVars.showErrAlert(InfoInputActivity.this);
                        return;
                    }

                    Intent intent = new Intent(InfoInputActivity.this, PassInputActivity.class);
                    intent.putExtra("userPhone", phoneNumber);
                    startActivity(intent);

                } catch (Exception e) {

                }
            }
        });

    }
}
