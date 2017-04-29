package grl.com.activities.loginRigister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.sdp.Info;

import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

public class VerifyActivity extends Activity implements View.OnClickListener {

    String phoneNumber;
    String verfiyCode;
    TextView commentView;
    EditText verifyCodeField;
    Button nextBtn;
    ImageView imgBack;
    TextView textBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("userPhone");
        verfiyCode = intent.getStringExtra("verifyCode");

        commentView = (TextView)findViewById(R.id.verfiyComment);
        commentView.setText(String.format("+86%s。请在下方输入", phoneNumber));
        nextBtn = (Button)findViewById(R.id.verifyNextBtn);
        verifyCodeField = (EditText)findViewById(R.id.userVerifyCode);

        verifyCodeField.setText(verfiyCode);
        verifyCodeField.setSelection(verifyCodeField.getText().length());

        imgBack = (ImageView)findViewById(R.id.img_back);
        textBack = (TextView)findViewById(R.id.txt_back);
        imgBack.setOnClickListener(this);
        textBack.setOnClickListener(this);
        setOnListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyNextBtn:
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

    private void setOnListener() {
        //add actions
        verifyCodeField.setOnKeyListener(new View.OnKeyListener() {
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

        nextBtn.setOnClickListener(this);
    }
    void nextAction() {
        String inputVerifyCode = verifyCodeField.getText().toString();
        if (inputVerifyCode.compareTo(verfiyCode) != 0) {
            return;
        }
        Intent intent = new Intent(this, InfoInputActivity.class);
        intent.putExtra("userPhone", phoneNumber);
        startActivity(intent);
    }
}
