package grl.com.activities.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

public class WorshipActivity extends Activity implements View.OnClickListener {

    Button worshipBtn;
    EditText nameEdit;
    EditText phoneEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worship);
        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        worshipBtn = (Button)findViewById(R.id.btn_worship);
        nameEdit = (EditText)findViewById(R.id.et_user_name);
        phoneEdit = (EditText)findViewById(R.id.et_phone_number);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.btn_worship).setOnClickListener(this);

    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.friend_group_title));
    }

    public void initializeData () {
        // set up Adapter
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_worship:
                break;
        }
    }

}
