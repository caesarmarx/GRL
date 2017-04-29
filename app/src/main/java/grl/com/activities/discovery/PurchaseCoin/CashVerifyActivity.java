package grl.com.activities.discovery.PurchaseCoin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/7/2016.
 */
public class CashVerifyActivity extends Activity implements View.OnClickListener {
    // view
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_verify);

        getParamsFromIntent();
        getViewByIDs();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {

    }

    public void getViewByIDs () {
        tvUserName = (TextView) findViewById(R.id.tvUserName);
    }

    public void initNavBar () {
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.purchas_order_plate));
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.cash_verify_title));
    }

    public void initializeData () {
        // init view
        tvUserName.setText(SelfInfoModel.userName);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        findViewById(R.id.btnVerify).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                             // 되돌이 단추를 누를 때의 처리 진행
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.btnVerify:                            // 확인단추를 누를 때의 처리 진행
                break;
        }
    }
}
