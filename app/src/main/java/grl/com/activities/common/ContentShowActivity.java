package grl.com.activities.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class ContentShowActivity extends Activity implements View.OnClickListener{

    // views
    TextView tvContent;
    TextView tvTitle;

    // required
    String strContent;
    String strTitle;
    String strBackTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_show);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        this.strTitle = intent.getStringExtra(Constant.TITLE_ID);
        this.strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        this.strContent = intent.getStringExtra(Constant.CONTENT_VIEW_KEY);
    }

    public void getViewByID () {
        tvTitle = (TextView)findViewById(R.id.txt_title);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        //set value in views
        tvContent.setText(this.strContent);
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }
}
