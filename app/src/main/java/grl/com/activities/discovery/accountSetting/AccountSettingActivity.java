package grl.com.activities.discovery.accountSetting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import grl.com.adapters.discovery.AccountSettingAdapter;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.AccountModel;
import grl.com.httpRequestTask.discovery.account.WalletGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/7/2016.
 */
public class AccountSettingActivity extends Activity implements View.OnClickListener{

    // view
    RecyclerView recyclerView;

    // task
    WalletGetTask walletGetTask;

    // response
    List<AccountModel> coinArray;
    List<AccountModel> giftArray;
    List<AccountModel> exchangeArray;

    // value
    AccountSettingAdapter recyclerAdapter;
    int selectType;                     // 0: order_plate 1: gift 2: exchange
    int oldSelected;
    Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        getParamsFromIntent();
        getViewByIDs();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {

    }

    public void getViewByIDs () {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        buttons = new Button[]{(Button)findViewById(R.id.btn_plate),
                (Button)findViewById(R.id.btn_gift),
                (Button)findViewById(R.id.btn_exchange)};
    }

    public void initNavBar (){
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.my_info_title));
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.user_account_title));
    }

    public void initializeData () {
        // init value
        coinArray = new ArrayList<AccountModel>();
        giftArray = new ArrayList<AccountModel>();
        exchangeArray = new ArrayList<AccountModel>();
        selectType = 0;
        oldSelected = -1;

        // set up recycler view
        recyclerAdapter = new AccountSettingAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        getWalletData();
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        findViewById(R.id.btn_plate).setOnClickListener(this);
        findViewById(R.id.btn_gift).setOnClickListener(this);
        findViewById(R.id.btn_exchange).setOnClickListener(this);
    }

    public void getWalletData () {
        walletGetTask = new WalletGetTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(AccountSettingActivity.this);
                    return;
                }
                try {
                    JsonArray result = (JsonArray) Response;
                    for(int i = 0; i < result.size(); i ++ ) {
                        JsonObject obj = result.get(i).getAsJsonObject();
                        Gson gson = new GsonBuilder().create();
                        AccountModel model = gson.fromJson(obj, AccountModel.class);
                        if(model.itemType == 0) {
                            coinArray.add(model);
                        } else {
                            giftArray.add(model);
                        }
                        exchangeArray.add(model);
                        selectType = 0;
                        showData();
                    }
                } catch (Exception e) {}
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                                 // 되돌이 단추를 누른 경우
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.btn_plate:                                // 령패
                oldSelected = selectType;
                selectType = 0;
                showData();
                break;
            case R.id.btn_gift:                                 // 선물
                oldSelected = selectType;
                selectType = 1;
                showData();
                break;
            case R.id.btn_exchange:                             // 교환
                oldSelected = selectType;
                selectType = 2;
                showData();
                break;
        }
    }

    public void showData () {
        if(oldSelected >= 0)
            buttons[oldSelected].setTextColor(getResources().getColor(R.color.light_dark_color));
        buttons[selectType].setTextColor(getResources().getColor(R.color.dark_gray_color));
        switch (selectType) {
            case 0:                                             // order_plate
                recyclerAdapter.notifyData(coinArray, selectType);
                break;
            case 1:                                             // gift
                recyclerAdapter.notifyData(giftArray, selectType);
                break;
            case 2:                                             // exchagne
                recyclerAdapter.notifyData(exchangeArray, selectType);
                break;
        }
    }
}
