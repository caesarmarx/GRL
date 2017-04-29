package grl.com.activities.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.activities.discovery.profile.SelfProfileSettingActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderContentModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.FileDownloadTask;
import grl.com.httpRequestTask.order.OrderAcceptTask;
import grl.com.httpRequestTask.order.TGroupOrderDetailTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class RealTimeOrderActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    RoundedImageView userPhotoView;
    TextView userNameView;
    TextView distanceView;
    TextView orderTypeView;
    TextView orderContentView;
    TextView orderBudgetView;

    String userId;
    String orderId;
    Boolean bTGroupOrder = false;
    Boolean isTeacher;

    Button acceptBtn;
    Button playBtn;
    Button stopBtn;

    OrderEntireModel orderData;
    String soundPath;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_order);
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        orderId = intent.getStringExtra("order_id");

        getViewByID();

        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        userPhotoView = (RoundedImageView)findViewById(R.id.img_user_photo);
        userNameView = (TextView)findViewById(R.id.tvUserName);
        distanceView = (TextView)findViewById(R.id.tvDistance);
        orderTypeView = (TextView)findViewById(R.id.tvOrderType);
        orderContentView = (TextView)findViewById(R.id.tvOrderContent);
        orderBudgetView = (TextView)findViewById(R.id.tvBudget);
        acceptBtn = (Button)findViewById(R.id.btn_order_accept);
        playBtn = (Button)findViewById(R.id.btn_play_sound);
        playBtn.setVisibility(View.GONE);
        stopBtn = (Button)findViewById(R.id.btn_stop_sound);
        stopBtn.setVisibility(View.GONE);
    }

    public void setOnListener() {
        userPhotoView.setOnClickListener(this);
        acceptBtn.setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        playBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    public void initializeData () {
        // set up Adapter
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        refresh();
        orderData = new OrderEntireModel();
    }

    public void refresh() {
        new TGroupOrderDetailTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(RealTimeOrderActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;

                    OrderModel orderModel = new OrderModel();
                    orderModel.parseFromJson(result.getJSONObject("order"));
                    OrderContentModel contentModel = new OrderContentModel();
                    contentModel.parseFromJson(result.getJSONObject("ord_content"));
                    UserModel userModel = new UserModel();
                    userModel.parseFromJson(result.getJSONObject("user_info"));

                    orderData.orderModel = orderModel;
                    orderData.userModel = userModel;
                    orderData.contentModel = contentModel;

                    if (orderData.contentModel.ordSound == null || orderData.contentModel.ordSound.isEmpty()) {
                        playBtn.setVisibility(View.GONE);
                        stopBtn.setVisibility(View.GONE);
                    } else {
                        playBtn.setVisibility(View.VISIBLE);
                        stopBtn.setVisibility(View.GONE);
                        new FileDownloadTask(RealTimeOrderActivity.this, orderData.contentModel.ordSound, new HttpCallback() {
                            @Override
                            public void onResponse(Boolean flag, Object Response) throws JSONException {
                                if(!flag)
                                {
                                    GlobalVars.showErrAlert(RealTimeOrderActivity.this);
                                    return;
                                }
                                soundPath = (String) Response;
                            }
                        });
                    }

                } catch (Exception e) {

                }
                updateUI();
            }
        });
    }

    void updateUI() {
        GlobalVars.loadImage(userPhotoView, orderData.userModel.userPhoto);
        userNameView.setText(orderData.userModel.userName);
        distanceView.setText(getString(R.string.card_distance_title) + GlobalVars.getDistanceString(SelfInfoModel.latitude, SelfInfoModel.longitude, orderData.userModel.latitude, orderData.userModel.longitude));
        orderBudgetView.setText(getString(R.string.order_budget) + ": " + String.format("%d", orderData.orderModel.orderBudget));
        orderTypeView.setText(orderData.contentModel.ordType);
        orderContentView.setText(orderData.contentModel.ordContent);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.img_user_photo:               // 3 인자창
                Utils.start_Activity(this, OtherMainActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.TITLE_ID, orderData.userModel.userName),
                        new BasicNameValuePair(Constant.USER_ID, orderData.userModel.userID),
                        new BasicNameValuePair(Constant.USER_NAME, orderData.userModel.userName),
                        new BasicNameValuePair(Constant.USER_PHOTO, orderData.userModel.userPhoto));
                break;
            case R.id.btn_order_accept:
                acceptAction();
                break;
            case R.id.btn_play_sound:
                playSound();
                break;
            case R.id.btn_stop_sound:
                stopSound();
                break;
            case R.id.btn_cancel:
                Utils.finish(this);
                break;
        }
    }

    public void acceptAction() {
        new OrderAcceptTask(this, userId, orderId, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                 if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(RealTimeOrderActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    GlobalVars.loadRealTimeOrder(false);
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(RealTimeOrderActivity.this);
                    }

                } catch (Exception e) {

                }
            }
        });
    }

    private void playSound() {
        playBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(soundPath);
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setVisibility(View.VISIBLE);
                    stopBtn.setVisibility(View.GONE);
                }

            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stopSound() {
        playBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);

        if (mediaPlayer == null)
            return;

        mediaPlayer.stop();
        mediaPlayer.release();

    }
    public void discussAction() {

    }

}
