package grl.com.activities.energy;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.App;
import grl.com.activities.search.GpsSearchActivity;
import grl.com.activities.search.PhoneSearchActivity;
import grl.com.activities.search.QRScan.QRScanActivity;
import grl.com.activities.search.QRViewActivity;
import grl.com.adapters.energy.NewEnergyRecyclerAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.NotificationManager;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.newEnergy.EnergyTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.ActionItem;
import grl.com.subViews.dialogues.TitlePopup;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */
public class NewEnergyActivity extends Activity implements View.OnClickListener{

    // view
    private RecyclerView recyclerView;
    private ImageView imgRight;
    private TitlePopup titlePopup;

    // value
    private NewEnergyRecyclerAdapter newEnergyRecyclerAdapter;

    // task
    private EnergyTask energyTask;

    // result
    private JSONArray resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newenergy);

        this.getViewByID();
        this.initNavBar();
        this.setOnListener();
        initPopWindow();
        this.initializeData();
    }

    public void getViewByID() {
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        imgRight = (ImageView) findViewById(R.id.img_right);
    }
    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.tab_energy_title));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.new_energy_title));
        findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.img_right)).setImageDrawable(getResources().getDrawable(R.drawable.icon_user_search));
    }

    private void initPopWindow() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        titlePopup.setItemOnClickListener(onitemClick);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, R.string.menu_gps_search,
                R.drawable.icon_gps_search));
        titlePopup.addAction(new ActionItem(this, R.string.menu_phone_search,
                R.drawable.icon_phone_search));
        titlePopup.addAction(new ActionItem(this, R.string.menu_qr_search,
                R.drawable.icon_qr_scan));
        titlePopup.addAction(new ActionItem(this, R.string.menu_qr_show,
                R.drawable.icon_my_qr));
    }

    private TitlePopup.OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {

        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 위치 검색
                    Utils.start_Activity(NewEnergyActivity.this, GpsSearchActivity.class);
                    break;
                case 1:// 전화번호 검색
                    Utils.start_Activity(NewEnergyActivity.this, PhoneSearchActivity.class);
                    break;
                case 2:// QR코드 검색
                    Utils.start_Activity(NewEnergyActivity.this, QRScanActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, App.getInstance().getString(R.string.new_energy_title)));
                    break;
                case 3:// 자신의 QR코드 보기

                    Utils.start_Activity(NewEnergyActivity.this, QRViewActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, App.getInstance().getString(R.string.new_energy_title)));
                    break;
                default:
                    break;
            }
        }
    };

    public void initializeData() {
        // init notification
        NotificationManager.deleteNotificatioin(NotificationManager.energyNotificationData);
        NotificationManager.energyNotificationData = new ArrayList<String>();
        // set up adatpers
        newEnergyRecyclerAdapter = new NewEnergyRecyclerAdapter(this);
        newEnergyRecyclerAdapter.strTitle = getString(R.string.new_energy_title);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(newEnergyRecyclerAdapter);
        // get data from
        JSONObject params = new JSONObject();
        try {
            params.put("session_id", SelfInfoModel.sessionID);
            params.put("user_id", SelfInfoModel.userID);
            params.put("con_type", 0);
            params.put("type", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        energyTask = new EnergyTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) {
                if(!flag || Response == null) {                 //failure
                    GlobalVars.showErrAlert(NewEnergyActivity.this);
                    return;
                }
                try {
                    resultData = new JSONArray();
                    JSONArray results = (JSONArray)Response;
                    for(int i = 0; i < results.length(); i ++) {
                        JSONObject data = results.getJSONObject(i);
                        String user_id = data.getString("user_id");
                        JSONObject temp = new JSONObject();
                        if(user_id.equals("-1")) {
                            temp.put("user_phone", "-1");
                            temp.put("user_id", "");
                            temp.put("user_photo", "");
                            temp.put("user_name", "");
                            temp.put("energy_tyep", -1);
                            temp.put("request_teacher", -1);
                            temp.put("request_disciple", -1);
                            temp.put("request_grl", -1);
                            temp.put("fgroup_id", "");
                            temp.put("fgroup_name", "");
                            temp.put("energy_quality", 0);
                            temp.put("request_flags", 1);
                        } else {
                            temp.put("user_phone", data.getString("user_phone"));
                            temp.put("user_id", data.getString("user_id"));
                            temp.put("user_photo", data.getString("user_photo"));
                            temp.put("user_name", data.getString("user_name"));
                            temp.put("request_teacher", data.getInt("request_teacher"));
                            temp.put("request_disciple", data.getInt("request_disciple"));
                            temp.put("request_grl", data.getInt("request_grl"));
                            temp.put("fgroup_id", data.getString("fgroup_id"));
                            temp.put("fgroup_name", data.getString("fgroup_name"));
                            temp.put("energy_quality", data.getInt("energy_quality"));
                            temp.put("request_flags", data.getInt("request_type"));
                            int energy_type = data.getInt("energy_type");
                            switch (energy_type) {
                                case 0:
                                    energy_type = Constant.ENERGY_ACCEPT;
                                    break;
                                case 1:
                                    energy_type = Constant.ENERGY_ACCEPTED;
                                    break;
                                case 2:
                                    energy_type = Constant.ENERGY_REQUIRED;
                                    break;
                            }
                            temp.put("energy_type", energy_type);
                        }

                        resultData.put(temp);
                    }
                    newEnergyRecyclerAdapter.myList = resultData;
                    newEnergyRecyclerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.layout_contacts).setOnClickListener(this);
        imgRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.layout_contacts:              // 주소록목록보기 단추를 누른 경우
                Utils.start_Activity(this, PhoneContactsActivity.class);
                break;
            case R.id.img_right:                    // 사용자추가 진행
                titlePopup.show(findViewById(R.id.layout_bar));
                break;
        }
    }

    public void refreshData () {
        this.initializeData();
    }

}
