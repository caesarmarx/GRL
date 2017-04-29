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

import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

public class PNumberActivity extends Activity implements View.OnClickListener{

    EditText phoneField;
    Button nextBtn;
    ImageView imgBack;
    TextView textBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnumber);

        phoneField = (EditText)findViewById(R.id.userPNumberPhone);
        nextBtn = (Button)findViewById(R.id.pnumberNextBtn);
        nextBtn.setOnClickListener(this);

        phoneField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    String phoneNumber = phoneField.getText().toString();
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
            case R.id.pnumberNextBtn:
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
        String phoneNumber = phoneField.getText().toString();
        String verfiyCode = "1234";
        if (!nextBtn.isEnabled())
            return;
        if (phoneNumber.length() != 11)
            return;
        Intent intent = new Intent(this, PassResetActivity.class);
        intent.putExtra("userPhone", phoneNumber);
        intent.putExtra("verifyCode", verfiyCode);
        startActivity(intent);
    }
}
