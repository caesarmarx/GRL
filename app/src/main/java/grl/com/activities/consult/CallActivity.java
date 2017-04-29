package grl.com.activities.consult;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcUeDb;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;

import java.util.Locale;

import grl.com.App;
import grl.com.configuratoin.Constant;
import grl.com.dataModels.MessageModel;
import grl.wangu.com.grl.R;

public class CallActivity extends Activity implements View.OnClickListener{

    private static final int CALL_WAIT = 0;
    private static final int CALL_START = 1;
    private static final int CALL_END = 2;

    private int state;
    private BroadcastReceiver receiver;

    private String toUserId = "";
    private boolean isVideo = false;

    private TextView stateView;
    private ImageView backView;

    private BroadcastReceiver mtcCallIncomingReceiver;
    private BroadcastReceiver mtcCallDidTermedReceiver;
    private BroadcastReceiver mtcCallTermedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        toUserId = intent.getStringExtra(Constant.USER_ID);
        isVideo = intent.getBooleanExtra("IsVideo", true);

        getViewById();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callVideo();
            }
        };
        sendRequest();

        registerReceivers();
    }

    private void getViewById() {
        stateView = (TextView) findViewById(R.id.tv_wait_state);
        backView = (ImageView) findViewById(R.id.iv_call_background);
    }
    private void setOnListener() {
        backView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(App.getInstance()).registerReceiver((receiver),
                new IntentFilter(Constant.CALL_VIDEO)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }

    public void sendRequest() {
        state = CALL_WAIT;
        updateUI();
        String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, toUserId);

        MessageModel model = new MessageModel();
        model.setMsgToUserId(toUserId);
        model.setMsgFromUserId(model.getMsgToUserId());
        model.setMsgText("Request");
        model.setMsgType(Constant.MSG_CALL_TYPE);
        model.setGroupType(Constant.CHAT_CONSULT);
        model.setbRead(true);

        String info = String.format(Locale.getDefault(), "{\"MtcImDisplayNameKey\":\"%s\"}", MtcUeDb.Mtc_UeDbGetUserName());
        String type = String.format("%s-%s", model.getGroupType(), model.getMsgType());
        int ret = MtcIm.Mtc_ImSendInfo(0, userUri, type, model.getMsgText(), info);
        if (ret != MtcConstants.ZOK) {
        } else {
        }
    }
    public void callVideo() {
        MtcCallDelegate.call(toUserId, null, null, isVideo, null);
    }

    private void updateUI() {
        switch (state) {
            case CALL_START:
                stateView.setText("Call Start");
                break;
            case CALL_WAIT:
                stateView.setText("Call Wait");
                break;
            case CALL_END:
                stateView.setText("Call End");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (state == CALL_END)
            finish();
    }

    private void registerReceivers() {
        mtcCallIncomingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                state = CALL_START;
                updateUI();
            }
        };
        mtcCallDidTermedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                state = CALL_END;
                updateUI();
            }
        };
        mtcCallTermedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                state = CALL_END;
                updateUI();
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mtcCallIncomingReceiver, new IntentFilter(MtcCallConstants.MtcCallIncomingNotification));
        broadcastManager.registerReceiver(mtcCallDidTermedReceiver, new IntentFilter(MtcCallConstants.MtcCallDidTermNotification));
        broadcastManager.registerReceiver(mtcCallTermedReceiver, new IntentFilter(MtcCallConstants.MtcCallTermedNotification));
    }

    private void unregisterReceivers() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(mtcCallIncomingReceiver);
        broadcastManager.unregisterReceiver(mtcCallDidTermedReceiver);
        broadcastManager.unregisterReceiver(mtcCallTermedReceiver);
        mtcCallIncomingReceiver = null;
        mtcCallDidTermedReceiver = null;
        mtcCallTermedReceiver = null;
    }
}
