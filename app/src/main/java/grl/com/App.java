package grl.com;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.justalk.cloud.juscall.JusCallConfig;
import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.juslogin.LoginDelegate;
import com.justalk.cloud.juspush.MiPush;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcUeDb;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;
import com.justalk.cloud.lemon.MtcUtil;
import com.justalk.cloud.lemon.MtcVer;
import com.justalk.cloud.zmf.Zmf;
import com.orm.SugarContext;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import grl.com.configuratoin.BadgeUtils;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.FileUtils;
import grl.com.configuratoin.NotificationManager;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.com.dataModels.MessageModel;
import grl.com.httpRequestTask.FileDownloadBackgroundTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class App extends MultiDexApplication {
    private static Context _context;

//    private BroadcastReceiver mtcImTextDidReceiveReceiver;
    private BroadcastReceiver mtcImInfoDidReceiveReceiver;
    private BroadcastReceiver mtcImFileDidReceiveReceiver;
    private BroadcastReceiver mtcImFetchingReceiver;
    private BroadcastReceiver mtcImFetchOkReceiver;
    private BroadcastReceiver mtcImFetchDidFailReceiver;

    private List<MessageModel> msgList;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notification();
        _context = getApplicationContext();

        // set orientation
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        LoginDelegate.setAutoLogin(false);
        if (LoginDelegate.init(this, getString(R.string.JusTalkCloud_AppKey)) != LoginDelegate.InitStat.MTC_INIT_SUCCESS) return;
        MtcCallDelegate.init(this);
        JusCallConfig.setBackIntentAction("com.justalk.cloud.sample.call.action.backfromcall");
        MiPush.setCallPushParm();

//        MiPush.setImTextPushParm("${Sender}", "${Text}");
//        MiPush.setImImagePushParm("${Sender}", "${Sender}" + " sent a image to you.");
//        MiPush.setImVoicePushParm("${Sender}", "${Sender}" + " sent you a voice message.");
//        MiPush.setImVideoPushParm("${Sender}", "${Sender}" + " sent you a video message.");
//        MiPush.setImFilePushParm("${Sender}", "${Sender}" + " sent a file to you.");

        String avatarVer = MtcVer.Mtc_GetAvatarVersion();
        String lemonVer = MtcVer.Mtc_GetLemonVersion();
        String melonVer = MtcVer.Mtc_GetMelonVersion();
        String mtcVer = MtcVer.Mtc_GetVersion();
        String zmfVer = Zmf.getVersion();
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "avatarVer" + avatarVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "lemonVer" + lemonVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "melonVer" + melonVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "mtcVer" + mtcVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "zmfVer" + zmfVer);

        msgList = new ArrayList<MessageModel>();

        SugarContext.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    public static Context getInstance() {
        return _context;
    }

    public void registerReceivers() {

        mtcImInfoDidReceiveReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                MessageModel model = new MessageModel();
                try {
                    String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                    JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                    String userUri = json.getString(MtcImConstants.MtcImUserUriKey);
                    String username = MtcUser.Mtc_UserGetId(userUri);
                    String infoType = json.getString(MtcImConstants.MtcImInfoTypeKey);
                    String content = json.getString(MtcImConstants.MtcImInfoContentKey);

                    model.setMsgFromUserId(username);
                    model.setMsgToUserId(SelfInfoModel.userID);
                    model.setMsgFromMe(false);
                    model.setMsgText(content);
                    model.setbRead(false);

                    String[] list = infoType.split("-");
                    model.setGroupType(list[0]);
                    if (list.length > 1)
                        model.setMsgType(list[1]);
                    else
                        model.setMsgType(Constant.MSG_TEXT_TYPE);
                    if (list.length > 2)
                        model.setMsgId(list[2]);

                    if (model.getGroupType().compareTo(Constant.CHAT_CONSULT) != 0) {
                        String userData = json.getString(MtcImConstants.MtcImUserDataKey);
                        model.setGroupId(userData);   // Group Chat 인 경우 Group Id 를 FromUserId 로
                    }

                    if (model.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
                        model.setSoundLength(Float.valueOf(model.getMsgText()));
                    }

                    if (model.getMsgType().compareTo(Constant.MSG_DELETE_TYPE) == 0) {
                        String msgId = model.getMsgText();
                        DBManager.removeMessageByMsgId(msgId);
                        updateUI(model.getGroupType());
                        return;
                    }

                    if (model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
                        String[] strArr = model.getMsgText().split("-");
                        double latitude = Double.valueOf(strArr[0]);
                        double longitude = Double.valueOf(strArr[1]);
                        float zoom = Float.valueOf(strArr[2]);
                        // Create Location Thumb Image
                    }

                    if (model.getMsgType().compareTo(Constant.MSG_CALL_TYPE) == 0) {
                        // Voice / Video Chat
                        if (model.getMsgText().compareTo("Response") == 0) {
                            // Call
                            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(_context);
                            Intent mainIntent = new Intent(Constant.CALL_VIDEO);
                            broadcaster.sendBroadcast(mainIntent);
                        }
                        if (model.getMsgText().compareTo("Request") == 0) {
                            // Send Response Message
                            MessageModel _replyMsg = new MessageModel();
                            _replyMsg.setMsgToUserId(model.getMsgFromUserId());
                            _replyMsg.setMsgFromUserId(model.getMsgToUserId());
                            _replyMsg.setMsgText("Response");
                            _replyMsg.setMsgType(model.getMsgType());
                            _replyMsg.setGroupType(model.getGroupType());
                            _replyMsg.setbRead(true);

                            String _info = String.format(Locale.getDefault(), "{\"MtcImDisplayNameKey\":\"%s\"}", MtcUeDb.Mtc_UeDbGetUserName());
                            String _userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, _replyMsg.getMsgToUserId());
                            String type = String.format("%s-%s", model.getGroupType(), model.getMsgType());
                            int ret = MtcIm.Mtc_ImSendInfo(0, _userUri, type, _replyMsg.getMsgText(), _info);
                            if (ret != MtcConstants.ZOK) {
                            } else {
                            }
                            return;
                        }
                        return;
                    }

                    model.setMsgStatus("received!");

                    model = DBManager.insertMessage(model);

                    final long sqlID = model.getSqlId();
                    if (model.getGroupType().compareTo(Constant.CHAT_CONSULT) != 0 &&
                            model.getMsgType().compareTo(Constant.MSG_TEXT_TYPE) != 0 &&
                            model.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) != 0 &&
                            model.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) != 0) {

                        new FileDownloadBackgroundTask(_context, model.getMsgText(), new HttpCallback() {
                            @Override
                            public void onResponse(Boolean flag, Object Response) throws JSONException {
                                String filePath, msgStatus;
                                if(!flag)
                                {
                                    filePath = "";
                                    msgStatus = Constant.MSG_RECEIVE_FAIL;
                                } else {
                                    filePath = (String)Response;
                                    msgStatus = Constant.MSG_RECEIVE_SUCCESS;
                                }
                                DBManager.updateMessageFilePath(filePath, sqlID);
                                DBManager.updateMessageState(msgStatus, sqlID);
                            }
                        });
                    }
                    updateUI(model.getGroupType());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

//                imTextDidReceive(model);
            }
        };

        mtcImFileDidReceiveReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                MessageModel model = new MessageModel();
                try {
                    String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                    JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                    String userUri = json.getString(MtcImConstants.MtcImUserUriKey);
                    int type = json.getInt(MtcImConstants.MtcImFileTypeKey);
                    String username = MtcUser.Mtc_UserGetId(userUri);
                    String fileUri = json.getString(MtcImConstants.MtcImFileUriKey);
                    String fileName = json.getString(MtcImConstants.MtcImFileNameKey);

                    model.setMsgFromUserId(username);
                    model.setMsgToUserId(SelfInfoModel.userID);
                    model.setbRead(false);
                    model.setMsgFromMe(false);
                    String[] list = fileName.split("-");
                    model.setGroupType(list[0]);
                    String path = FileUtils.getRecvDir(getApplicationContext()) + list[list.length - 1];
                    model.setFilePath(path);
                    model.setMsgId(list[1]);

                    msgList.add(model);
                    if (type == MtcImConstants.EN_MTC_IM_FILE_IMAGE) {
                        model.setMsgType(Constant.MSG_IMAGE_TYPE);
                        int cookie = msgList.indexOf(model);
                        int ret = MtcIm.Mtc_ImFetchFile(cookie, fileUri, path);
                        if (ret != MtcConstants.ZOK) {
                            model.setMsgStatus("fetch fail!");
                        }
                    }
                    if (type == MtcImConstants.EN_MTC_IM_FILE_VIDEO) {
                        model.setMsgType(Constant.MSG_VIDEO_TYPE);
                        int cookie = msgList.indexOf(model);
                        int ret = MtcIm.Mtc_ImFetchFile(cookie, fileUri, path);
                        if (ret != MtcConstants.ZOK) {
                            model.setMsgStatus("fetch fail!");
                        }
                    }
                    if (type == MtcImConstants.EN_MTC_IM_FILE_VOICE) {
                        model.setMsgType(Constant.MSG_VOICE_TYPE);
                        model.setSoundLength(Float.valueOf(list[2]));
                        int cookie = msgList.indexOf(model);
                        int ret = MtcIm.Mtc_ImFetchFile(cookie, fileUri, path);
                        if (ret != MtcConstants.ZOK) {
                            model.setMsgStatus("fetch fail!");
                        }
                    }
                    model = DBManager.insertMessage(model);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };


        mtcImFetchOkReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                if (cookie > -1) {
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info)
                                .nextValue();
                        String fileName = json.getString(MtcImConstants.MtcImFilePathKey);
                        MessageModel model = msgList.get(cookie);
                        model.setMsgStatus(Constant.MSG_RECEIVE_SUCCESS);

                        DBManager.updateMessageState(Constant.MSG_RECEIVE_SUCCESS,model.getSqlId());
                        updateUI(model.getGroupType());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        };

        mtcImFetchDidFailReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                if (cookie > -1) {
                    MessageModel model = msgList.get(cookie);
                    model.setMsgStatus(Constant.MSG_RECEIVE_FAIL);
                    DBManager.updateMessageState(Constant.MSG_RECEIVE_FAIL,model.getSqlId());
                    updateUI(model.getGroupType());
                }
            }
        };

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mtcImInfoDidReceiveReceiver, new IntentFilter(MtcImConstants.MtcImInfoDidReceiveNotification));
        broadcastManager.registerReceiver(mtcImFileDidReceiveReceiver, new IntentFilter(MtcImConstants.MtcImFileDidReceiveNotification));
        broadcastManager.registerReceiver(mtcImFetchOkReceiver, new IntentFilter(MtcImConstants.MtcImFetchOkNotification));
        broadcastManager.registerReceiver(mtcImFetchDidFailReceiver, new IntentFilter(MtcImConstants.MtcImFetchDidFailNotification));
    }

    private void unregisterReceivers() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(mtcImInfoDidReceiveReceiver);
        broadcastManager.unregisterReceiver(mtcImFileDidReceiveReceiver);
        broadcastManager.unregisterReceiver(mtcImFetchOkReceiver);
        broadcastManager.unregisterReceiver(mtcImFetchDidFailReceiver);
        mtcImInfoDidReceiveReceiver = null;
        mtcImFileDidReceiveReceiver = null;
        mtcImFetchingReceiver = null;
        mtcImFetchOkReceiver = null;
        mtcImFetchDidFailReceiver = null;
    }

    private void updateUI(String type) {
        // Update Chat UI
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(_context);
        if (type.compareTo(Constant.CHAT_CONSULT) == 0) {
            Intent mainIntent = new Intent(Constant.UPDATE_MAIN_UI);
            broadcaster.sendBroadcast(mainIntent);
            Intent chatIntent = new Intent(Constant.UPDATE_CONSULT_CHAT);
            broadcaster.sendBroadcast(chatIntent);
        }
        if (type.compareTo(Constant.CHAT_TGROUP) == 0) {
            Intent sendIntent = new Intent(Constant.UPDATE_TGROUP_CHAT);
            broadcaster.sendBroadcast(sendIntent);
        }
        if (type.compareTo(Constant.CHAT_FGROUP) == 0) {
            Intent sendIntent = new Intent(Constant.UPDATE_FGROUP_CHAT);
            broadcaster.sendBroadcast(sendIntent);
        }
        if (type.compareTo(Constant.CHAT_ORDER) == 0) {
            Intent sendIntent = new Intent(Constant.UPDATE_ORDER_CHAT);
            broadcaster.sendBroadcast(sendIntent);
        }
    }


    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            curActivity = activity;
        }

        @Override
        public void onActivityStarted(Activity activity) {
            curActivity = activity;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            curActivity = activity;
            BadgeUtils.clearBadge(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


    // push notification
    public void notification () {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);

        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        NotificationManager.getNotificationData();
                    }
                });
            }
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                NotificationManager.getNotificationData();
                switch (msg.builder_id) {
                    case 1:
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView)
//                                .setSmallIcon(getSmallIconId(context, msg))
//                                .setTicker(msg.ticker)
//                                .setAutoCancel(true);

//                        return builder.build();

                    default:
                        return super.getNotification(context, msg);
                }
            }

        };
        mPushAgent.setMessageHandler(messageHandler);

        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
//                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

    }

    // Check App is in background
    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) _context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(_context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(_context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    private PushAgent mPushAgent;
    public static Activity curActivity = null;
}
