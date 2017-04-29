package grl.com.activities;

import android.app.Activity;
import android.location.LocationManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;

import org.bitlet.weupnp.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.microedition.khronos.opengles.GL;

import grl.com.App;
import grl.com.activities.discovery.DiscoverySettingActivity;
import grl.com.activities.discovery.order.DiscoverOrderSolveActivity;
import grl.com.activities.map.MapSelectActivity;
import grl.com.activities.order.ContactsSelectActivity;
import grl.com.activities.order.RealTimeOrderActivity;
import grl.com.activities.order.UserRankActivity;
import grl.com.activities.search.GpsSearchActivity;
import grl.com.activities.search.PhoneSearchActivity;
import grl.com.activities.search.QRScan.QRScanActivity;
import grl.com.activities.search.QRViewActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.NotificationManager;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.BackgroundOrderModel;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.RealTimeTask;
import grl.com.httpRequestTask.pushNotification.InsertTokenTask;
import grl.com.httpRequestTask.user.UpdateLocationTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.ActionItem;
import grl.com.subViews.dialogues.DirectionDialog;
import grl.com.subViews.dialogues.RealTimeDialog;
import grl.com.subViews.dialogues.TitlePopup;
import grl.com.subViews.dialogues.TitlePopup.OnItemOnClickListener;
import grl.com.subViews.fragment.Fragment_chat;
import grl.com.subViews.fragment.Fragment_discovery;
import grl.com.subViews.fragment.Fragment_energy;
import grl.com.subViews.fragment.Fragment_order;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/7/2016.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener, AMapLocationListener, RealTimeDialog.OnItemOnClickListener {

    private ImageView img_right;
    private ImageView img_left;

    private TitlePopup titlePopup;
    private RealTimeDialog realTimeDialog;

    //tab bars
    private Fragment[] fragments;
    public Fragment_order fragmentOrder;
    private Fragment_chat fragmentChat;
    public Fragment_energy fragmentEnergy;
    private Fragment_discovery fragmentDiscovery;

    // Real Time Order
    private LinearLayout realOrderLayout;
    private TextView realThumbView;
    private Button realDropBtn;

    //unreadable textview
    private TextView unreadOrder;
    private TextView unreadChat;
    private TextView unreadEnergy;

    //tab bar views
    private ImageView[] imagebuttons;
    private TextView[] textviews;

    private int index;
    private int currentTabIndex;

    private AMapLocationClientOption locationOption = null;
    private AMapLocationClient locationClient = null;

    private final int DIRECTION_REQUEST = 0;
    private final int MAP_REQUEST = 1;
    private final int CONTACT_REQUEST = 2;

    private BroadcastReceiver receiver;

    private PushAgent mPushAgent;               // android push notification

    // task
    InsertTokenTask insertTokenTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        initNotification();
        initViews();
        initTabView();
        setOnListener();
        initReceiver();
        initLocation();
        initPopWindow();
        registerBroadcast();

        GlobalVars.loadRealTimeOrder(false);

        // Update Chat Receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("Message");
//                if (message.compareTo(Constant.UPDATE_CONSULT_CHAT) == 0)
                fragmentChat.refresh();
//                if (message.compareTo(Constant.UPDATE_REAL_ORDER) == 0)
                updateRealTimeUI();

            }
        };

        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE );
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您的位置似乎被禁用，你要启用?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        GlobalVars.bFirstNotify = true;
        NotificationManager.getNotificationData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver((receiver),
                new IntentFilter(Constant.UPDATE_MAIN_UI)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        fragmentEnergy.initializeData();
        updateRealTimeUI();
        if (fragmentOrder != null && fragmentOrder.starView != null)
            fragmentOrder.starView.goToCenter();
        super.onResume();
    }

    // broadcast receiver
    public void registerBroadcast() {
        // 새로운 능량요청이 들어온 경우
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNewenergyReq, new IntentFilter(Constant.NEW_ENERGY));
    }
    public void unregisterBroadcast () {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNewenergyReq, new IntentFilter(Constant.NEW_ENERGY));
    }
    private BroadcastReceiver mNewenergyReq = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            updateUnreadLabel();
            fragmentEnergy.sectionAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (realTimeDialog != null)
            realTimeDialog.setWidth(findViewById(R.id.ll_real_order).getWidth());
        if (index == 0)
            fragmentOrder.onWindowFocusChanged(hasFocus);
    }

    private void findViewById() {
        img_left = (ImageView) findViewById(R.id.img_left);
        img_right = (ImageView) findViewById(R.id.img_right);

        realOrderLayout = (LinearLayout) findViewById(R.id.ll_real_order);
        realThumbView = (TextView) findViewById(R.id.tv_real_thumb);
        realDropBtn = (Button) findViewById(R.id.btn_real_drop);
    }
    private void initViews() {
        // 设置消息页面为初始页面
    }

    private void initTabView() {
        unreadOrder = (TextView) findViewById(R.id.unread_order_number);
        unreadChat = (TextView) findViewById(R.id.unread_chat_number);
        unreadEnergy = (TextView) findViewById(R.id.unread_newenergy_number);

        fragmentOrder = new Fragment_order();
        fragmentChat = new Fragment_chat();
        fragmentEnergy = new Fragment_energy();
        fragmentDiscovery = new Fragment_discovery();
        fragments = new Fragment[] { fragmentOrder, fragmentChat,
                fragmentEnergy, fragmentDiscovery };
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_order);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_chat);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_newenergy);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_discovery);

        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_order);
        textviews[1] = (TextView) findViewById(R.id.tv_chat);
        textviews[2] = (TextView) findViewById(R.id.tv_newenergy);
        textviews[3] = (TextView) findViewById(R.id.tv_discovery);
        textviews[0].setTextColor(this.getResources().getColor(R.color.yellowBack));
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentOrder)
                .add(R.id.fragment_container, fragmentChat)
                .add(R.id.fragment_container, fragmentEnergy)
                .add(R.id.fragment_container, fragmentDiscovery)
                .hide(fragmentChat).hide(fragmentEnergy)
                .hide(fragmentDiscovery).show(fragmentOrder).commit();
        updateUnreadLabel();
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_order:
                img_right.setVisibility(View.VISIBLE);
                index = 0;
                if (fragmentOrder != null) {
//                    fragmentOrder.refresh();
                }
                img_right.setImageResource(R.drawable.icon_user_list);
                break;
            case R.id.re_chat:
                index = 1;
                fragmentChat.refresh();
                img_right.setImageResource(R.drawable.icon_user_list);
//                img_right.setImageResource(R.drawable.icon_titleaddfriend);
                break;
            case R.id.re_energy:
                index = 2;
                img_right.setImageResource(R.drawable.icon_user_list);
                break;
            case R.id.re_discovery:
                index = 3;
                if(fragmentDiscovery != null) {

                }
                img_right.setImageResource(R.drawable.icon_user_profile);

                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        imagebuttons[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setTextColor(this.getResources().getColor(R.color.white));
        textviews[index].setTextColor(this.getResources().getColor(R.color.yellowBack));
        currentTabIndex = index;

        if (index != 0) {
            findViewById(R.id.submit_view).setVisibility(View.GONE);
            findViewById(R.id.layout_bar).setVisibility(View.VISIBLE);
        }
    }

    public void updateUnreadLabel() {
        int orderCount = 0;
        int chatCount = 0;
        int energyCount = 0;
        if(NotificationManager.energyNotificationData != null)
            energyCount = NotificationManager.energyNotificationData.size();

        //새로운 령
        orderCount = GlobalVars.bkOrderList.size();
        if (orderCount > 0) {
            unreadOrder.setText(String.valueOf(orderCount));
            unreadOrder.setVisibility(View.VISIBLE);
        } else {
            unreadOrder.setVisibility(View.INVISIBLE);
        }

        //신능량창에 새로운 요청이 있는 경우 그에 대한 통보를 현시한다.
        if (energyCount > 0) {
            unreadEnergy.setText(String.valueOf(energyCount));
            unreadEnergy.setVisibility(View.VISIBLE);
        } else {
            unreadEnergy.setVisibility(View.INVISIBLE);
        }
    }

    private void setOnListener() {
        img_left.setOnClickListener(this);
        img_right.setOnClickListener(this);
        findViewById(R.id.ll_real_order).setOnClickListener(this);
    }

    private void initPopWindow() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
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

        int height = 200;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        if (width >= 1024)
            height = 400;
        realTimeDialog = new RealTimeDialog(this, findViewById(R.id.ll_real_order).getWidth(), height);
        realTimeDialog.setItemOnClickListener(this);
    }

    private void initReceiver() {

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_left:
                titlePopup.show(findViewById(R.id.layout_bar));
                break;
            case R.id.img_right:
                if(index == 3) {
                    // 사용자 설정창으로 이행한다.
                    Utils.start_Activity(MainActivity.this, DiscoverySettingActivity.class);
                } else
                    Utils.start_Activity(this, UserRankActivity.class);
                break;
            case R.id.ll_real_order:
                updateRealTimeUI();
                realTimeDialog.show(findViewById(R.id.layout_bar));
                break;
            default:
                break;
        }
    }

    private OnItemOnClickListener onitemClick = new TitlePopup.OnItemOnClickListener() {

        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 위치 검색
                    Utils.start_Activity(MainActivity.this, GpsSearchActivity.class);
                    break;
                case 1:// 전화번호 검색
                    Utils.start_Activity(MainActivity.this, PhoneSearchActivity.class);
                    break;
                case 2:// QR코드 검색
                    Utils.start_Activity(MainActivity.this, QRScanActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, getCurTitle()));
                    break;
                case 3:// 자신의 QR코드 보기

                    Utils.start_Activity(MainActivity.this, QRViewActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, getCurTitle()));
                    break;
                default:
                    break;
            }
        }
    };

    public String getCurTitle () {
        String strBack = "";
        switch (index) {
            case 0:
                strBack = getString(R.string.tab_order_title);
                break;
            case 1:
                strBack = getString(R.string.tab_chat_title);
                break;
            case 2:
                strBack = getString(R.string.tab_energy_title);
                break;
            case 3:
                strBack = getString(R.string.tab_discorvery_title);
                break;
        }
        return strBack;
    }


    // Location Update

    private void initLocation() {
        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(this);
        locationClient.startLocation();

    }
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation.getLatitude() < Double.MIN_NORMAL ||
                aMapLocation.getLongitude() < Double.MIN_NORMAL)
            return;
        double offsetLatitude = SelfInfoModel.latitude - aMapLocation.getLatitude();
        double offsetLongitude = SelfInfoModel.longitude - aMapLocation.getLongitude();
        if (Math.abs(offsetLatitude) > 0.001 || Math.abs(offsetLongitude) > 0.001) {

            SelfInfoModel.latitude = aMapLocation.getLatitude();
            SelfInfoModel.longitude = aMapLocation.getLongitude();
            SelfInfoModel.posArea.put("citycode", aMapLocation.getCityCode());
            SelfInfoModel.posArea.put("adcode", aMapLocation.getAdCode());
            SelfInfoModel.posArea.put("province", aMapLocation.getProvince());
            SelfInfoModel.posArea.put("city", aMapLocation.getCity());
            SelfInfoModel.posArea.put("district", aMapLocation.getDistrict());
            updateLocation();
        }
    }
    public void updateLocation() {
        if (SelfInfoModel.userID == null || SelfInfoModel.userID.length() == 0)
            return;
        new UpdateLocationTask(this, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

            }
        });
    }


    // Real Time Order
    private void updateRealTimeUI() {
        realTimeDialog.setList(GlobalVars.realTimeOrder);
        if (GlobalVars.realTimeOrder.size() > 0) {
            OrderEntireModel firstModel = GlobalVars.realTimeOrder.get(0);
            Date now = new Date();
            if (now.getTime() - firstModel.orderModel.timeEnd * 1000 < 60000) {
                realOrderLayout.setBackgroundResource(R.drawable.round_fill_white_with_border);
                realThumbView.setTextColor(Color.BLACK);
                realThumbView.setText(GlobalVars.getOrderString(firstModel.contentModel.ordType,
                        firstModel.contentModel.ordContent));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateRealTimeUI();
                    }
                }, 60000);
            } else {
                realOrderLayout.setBackgroundResource(R.drawable.order_press_speak_btn);
                int color = getResources().getColor(R.color.place_holder_color);
                realThumbView.setTextColor(color);
                realThumbView.setText(getString(R.string.realtime_comment));
            }

        } else {
            realOrderLayout.setBackgroundResource(R.drawable.order_press_speak_btn);
            int color = getResources().getColor(R.color.place_holder_color);
            realThumbView.setTextColor(color);
            realThumbView.setText(getString(R.string.realtime_comment));
        }

        if (GlobalVars.getNotifyCount() > 0 && GlobalVars.bkOrderList.size() > 0) {
            BackgroundOrderModel model = GlobalVars.getFirstNotify();
            for (int i = 0; i < GlobalVars.realTimeOrder.size(); i ++) {
                OrderEntireModel order = GlobalVars.realTimeOrder.get(i);
                if (order.orderModel.orderId.compareTo(model.orderId) == 0) {
                    showRealTimeDetail(i);
                    break;
                }
            }
            GlobalVars.removeNotifyShow();
        }
        updateUnreadLabel();
    }

    public void onItemClick(int position) {
        showRealTimeDetail(position);
    }

    private void showRealTimeDetail(int orderIndex) {

        OrderEntireModel model = GlobalVars.realTimeOrder.get(orderIndex);
        GlobalVars.removeBKOrder(model.orderModel.orderId);
        if (model.orderModel.orderStatus == OrderModel.ORDER_ACCEPT_STATE ||
                model.orderModel.orderStatus == OrderModel.ORDER_DISCUSS_STATE) {
            Utils.start_Activity(this, DiscoverOrderSolveActivity.class,
                    new BasicNameValuePair("user_id", model.userModel.userID),
                    new BasicNameValuePair("order_id",model.orderModel.orderId),
                    new BasicNameValuePair("bTGroupOrder",String.valueOf(true)));
        } else {
            Intent intent = new Intent(this, RealTimeOrderActivity.class);
            intent.putExtra("user_id", model.userModel.userID);
            intent.putExtra("order_id", model.orderModel.orderId);
            startActivity(intent);
        }
    }

    public void startContactActivity(String number) {
        Intent intent = new Intent(this, ContactsSelectActivity.class);
        intent.putExtra(Constant.ORDER_DIRECTION, number);
        startActivityForResult(intent, CONTACT_REQUEST);
    }
    public void startDirectionActivity(int direction) {
        Intent intent = new Intent(this, DirectionDialog.class);
        intent.putExtra(Constant.ORDER_DIRECTION, direction);
        startActivityForResult(intent, DIRECTION_REQUEST);
    }
    public void startMapActivity(double latitude, double longitude, int zoom) {
        Intent intent = new Intent(this, MapSelectActivity.class);
        intent.putExtra(Constant.SAVED_LATITUDE, latitude);
        intent.putExtra(Constant.SAVED_LONGITUDE, longitude);
        intent.putExtra(Constant.SAVED_ZOOM, zoom);
        startActivityForResult(intent, MAP_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DIRECTION_REQUEST) {
                int direct = data.getIntExtra(Constant.ORDER_DIRECTION, -1);
                fragmentOrder.getUserDialog().setDirection(direct);
            }
            if (requestCode == MAP_REQUEST) {
                double latitude = data.getDoubleExtra(Constant.SAVED_LATITUDE, 0);;
                double longitude = data.getDoubleExtra(Constant.SAVED_LONGITUDE, 0);
                int zoom = data.getIntExtra(Constant.SAVED_ZOOM, 10);
                fragmentOrder.getUserDialog().setLatitude(latitude);
                fragmentOrder.getUserDialog().setLongitude(longitude);
                fragmentOrder.getUserDialog().setZoom(zoom);
            }
            if (requestCode == CONTACT_REQUEST) {
                String number = data.getStringExtra(Constant.PHONE_CONTACT);
                fragmentOrder.getUserDialog().setNumber(number);
            }
        }
    }

    // push notification

    public void initNotification () {
        mPushAgent = PushAgent.getInstance(this);
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        //应用程序启动统计
        //参考集成文档的1.5.1.2
        //http://dev.umeng.com/push/android/integration#1_5_1
        mPushAgent.enable();
        mPushAgent.onAppStart();

//        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

        String token_id = mPushAgent.getRegistrationId();

        // insert token
        setInsertToken(token_id);
    }

    // token을 봉사기에 등록한다.
    public void setInsertToken (String token_id) {
        JsonObject params = new JsonObject();
        params.addProperty("token_id", token_id);
        params.addProperty("phone_type", 1);
        insertTokenTask = new InsertTokenTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(MainActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                boolean res_flag = result.get("result").getAsBoolean();
                if(res_flag) {                              // success

                } else {                                    // failure

                }
            }
        });
    }
}
