package grl.com.httpRequestTask.pushNotification;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import grl.com.App;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 7/13/2016.
 */
public class SendNotificationTask extends Object implements HttpCallback {
    HttpCallback callback = null;

    public SendNotificationTask(Context context, String userId, String message, final HttpCallback callback) {
        this.callback = callback;
        try {
            Ion.with(App.getInstance())
                    .load(GlobalVars.SEND_NOTIFICATION)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", SelfInfoModel.userID)
                    .setBodyParameter("to_userid", userId)
                    .setBodyParameter("message", message)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {

                            try {
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                JSONObject jsonResultData = jsonObj.getJSONObject("result_data");
                                Boolean result = jsonObj.getBoolean("result_code");
                                callback.onResponse(result, jsonResultData);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public void onResponse(Boolean flag, Object Response) {

    }
}
