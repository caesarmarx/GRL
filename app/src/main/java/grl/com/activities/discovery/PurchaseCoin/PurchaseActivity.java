package grl.com.activities.discovery.PurchaseCoin;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import grl.com.App;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/7/2016.
 */
public class PurchaseActivity extends Activity implements View.OnClickListener{

    ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        getParamsFromIntent();
        getViewByIDs();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {

    }

    public void getViewByIDs () {
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
    }

    public void initNavBar () {
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.purchas_order_plate));
        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.setting_title));
    }

    public void initializeData () {
        // init view
        imgPhoto.postDelayed(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = (int) (displaymetrics.widthPixels - GlobalVars.dp2px(App.getInstance(), 20));
                int height = width * 5 / 8;

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgPhoto.getLayoutParams();
                params.width = width;
                params.height = height;
                imgPhoto.setLayoutParams(params);
            }
        }, 1);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        findViewById(R.id.btnPurchase).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                     // 되돌이 단추를 누를 때의 처리 진행
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.btnPurchase:                  // 구입단추를 누를 때의 처리 진행
                Utils.start_Activity(this, CashVerifyActivity.class);
                break;
        }
    }
}
