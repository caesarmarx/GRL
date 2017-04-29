package grl.com.configuratoin;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import grl.com.App;
import grl.com.activities.discovery.StarSettingActivity;
import grl.com.activities.discovery.crash.CrashActivity;
import grl.com.activities.energy.NewEnergyActivity;
import grl.com.httpRequestTask.pushNotification.DeleteNotificationTask;
import grl.com.httpRequestTask.pushNotification.GetNotificationDataTask;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 7/13/2016.
 */
public class NotificationManager {
    // value
    public static List<String> energyNotificationData;

    // notification의 확장자료를 얻는다.
    public static void getNotificationData () {
        new GetNotificationDataTask(App.curActivity, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(App.curActivity);
                    return;
                }
                energyNotificationData = new ArrayList<String>();
                JsonArray temp = (JsonArray) Response;
                BadgeUtils.setBadge(App.getInstance(), temp.size());
                for (int i = 0; i < temp.size(); i ++) {
                    JsonObject notifyItem = temp.get(i).getAsJsonObject();
                    Integer notificationType = notifyItem.get("type").getAsInt();
                    String notificationID = notifyItem.get("_id").getAsJsonObject().get("$id").getAsString();
                    JsonObject cutomData ;
                    try {
                        cutomData = notifyItem.get("data").getAsJsonObject();
                    } catch (Exception e) {
                        cutomData = new JsonObject();
                    }
                    switch (notificationType) {
                        case 0:                             // default
                            break;
                        case 201:                           // Update Real Time Order
                            if (cutomData != null && (App.isAppIsInBackground() || GlobalVars.bFirstNotify == true)) {
                                GlobalVars.addBackgroundOrder(cutomData, true);
                                GlobalVars.loadRealTimeOrder(true);
                            } else {
                                GlobalVars.addBackgroundOrder(cutomData, false);
                                GlobalVars.loadRealTimeOrder(true);

                            }
                            deleteNotification(notificationID);
                            break;
                        case 202:                           // Accept Order
                            deleteNotification(notificationID);
                            break;
                        case 203:                           // Discuss Order
                            deleteNotification(notificationID);
                            break;
                        case 204:                           // Resend Order
                            deleteNotification(notificationID);
                            break;
                        case 205:                           // Update Real Time Order
                            deleteNotification(notificationID);
                            break;
                        case 206:                           // Solution select
                            deleteNotification(notificationID);
                            break;
                        case 401:                           // New Energy Required
                            energyNotificationData.add(notificationID);
                            break;
                        case 402:                           // New Energy Accept
                            if(App.curActivity instanceof NewEnergyActivity) {
                                ((NewEnergyActivity) App.curActivity).refreshData();
                                deleteNotificatioin(Arrays.asList(notificationID));
                            }
                            break;
                        case 5051:                          // Planet Accepted
                            if(App.curActivity instanceof StarSettingActivity) {
                                ((StarSettingActivity) App.curActivity).refreshData();
                                deleteNotificatioin(Arrays.asList(notificationID));
                            }
                            break;
                        case 5041:                          // Enter in the Crash
                            if(App.curActivity instanceof CrashActivity) {
                                ((CrashActivity) App.curActivity).addNewItem(cutomData);
                                deleteNotificatioin(Arrays.asList(notificationID));
                            }
                            break;
                        case 5042:                          // Request Crash
                            if(App.curActivity instanceof CrashActivity) {
                                ((CrashActivity) App.curActivity).receiveRequest(cutomData.get("user_id").getAsString());
                                deleteNotificatioin(Arrays.asList(notificationID));
                            }
                            break;
                        case 5043:                          // Crash Result
                            if(App.curActivity instanceof CrashActivity) {
                                ((CrashActivity) App.curActivity).crashResult(cutomData);
                                deleteNotificatioin(Arrays.asList(notificationID));
                            }
                            break;
                        case 4031:                          // 친구계 도움요청이 들어온 경우
                            break;
                    }
                }
                GlobalVars.bFirstNotify = false;
                energyRequestNotification();
            }
        });
    }

    // 신능량 요청인 경우의 처리
    public static void energyRequestNotification () {
        if(App.curActivity == null)
            return;
        if(energyNotificationData != null && energyNotificationData.size() > 0) {
            if(App.curActivity instanceof NewEnergyActivity) {
                deleteNotificatioin(energyNotificationData);
                energyNotificationData = new ArrayList<String>();
            }
            Intent intent = new Intent(Constant.NEW_ENERGY);
            LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(intent);
        }
    }

    public static void deleteNotification(String notificationId) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(notificationId);
        deleteNotificatioin(list);
    }

    //  읽어진 통보를 삭제한다.
    public static void deleteNotificatioin (List<String> notificationIDs) {
        if(notificationIDs == null || notificationIDs.size() == 0)
            return;


        JsonArray IDs = new JsonArray();
        for (int i = 0; i < notificationIDs.size(); i++) {
            String strTemp = notificationIDs.get(i);
            IDs.add(new JsonPrimitive(strTemp));
        }
        JsonObject params = new JsonObject();
        params.addProperty("session_id", SelfInfoModel.sessionID);
        params.add("notification_id", IDs);
        new DeleteNotificationTask(App.curActivity, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if (!flag || Response == null) {
                    GlobalVars.showErrAlert(App.curActivity);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                boolean res_flag = result.get("result").getAsBoolean();
                if (res_flag) {                          // success

                } else {                                // failure
                    GlobalVars.showErrAlert(App.curActivity);
                }
            }
        });

    }

}
