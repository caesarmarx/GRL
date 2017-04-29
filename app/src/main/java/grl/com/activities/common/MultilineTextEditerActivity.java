package grl.com.activities.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/24/2016.
 */
public class MultilineTextEditerActivity extends Activity implements View.OnClickListener {

    // view
    private EditText etView;
    private TextView tvDescription;
    private TextView tvTitle;
    private TextView tvLeft;

    // required
    String strTitle;
    String strDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muliti_editer);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = this.getIntent();
        this.strTitle = intent.getStringExtra(Constant.TITLE_ID);
        this.strDes = intent.getStringExtra(Constant.TEXTFIELD_CONTENT);
    }
    public void getViewByID () {
        this.etView = (EditText) findViewById(R.id.et_common);
        this.tvDescription = (TextView) findViewById(R.id.txt_description);
        this.tvTitle = (TextView) findViewById(R.id.txt_title);
        this.tvLeft = (TextView) findViewById(R.id.txt_left);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.cancel));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle );
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(this.getString(R.string.save));
    }

    public void initializeData () {
        // set value in views
        this.tvTitle.setText(this.strTitle);
        this.etView.setText(this.strDes);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)this.tvLeft.getLayoutParams();
        params.setMargins(10, 0, 0, 0); //substitute parameters for left, top, right, bottom
        tvLeft.setLayoutParams(params);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    public void onSaveData (Boolean isSave) {
        Intent intent = new Intent();
        intent.putExtra(Constant.TEXTFIELD_CONTENT, etView.getText().toString());
        if(isSave)
            setResult(Activity.RESULT_OK, intent);
        else
            setResult(Activity.RESULT_CANCELED, intent);
        Utils.finish(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.txt_left:                     // 취소 단추를 누른 경우
                this.onSaveData(false);
                break;
            case R.id.txt_right:                    // 보관 단추를 누른 경우
                this.onSaveData(true);
        }
    }

    @Override
    public void onBackPressed() {
        this.onSaveData(false);
    }
}
