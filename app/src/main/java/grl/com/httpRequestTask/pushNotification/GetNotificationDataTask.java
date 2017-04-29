package grl.com.httpRequestTask.pushNotification;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import grl.com.App;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 7/13/2016.
 */
public class GetNotificationDataTask extends Object implements HttpCallback {
    HttpCallback callback = null;

    public GetNotificationDataTask(Context context, final HttpCallback callback) {
        this.callback = callback;
        try {
            Ion.with(App.getInstance())
                    .load(GlobalVars.GET_NOTIFICATION_DATA)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", SelfInfoModel.userID)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {

                            try {
                                JsonObject jsonObj = GlobalVars.getGJsonDataFromString(s);
                                JsonArray jsonResultData = jsonObj.getAsJsonArray("result_data");
                                Boolean result = jsonObj.get("result_code").getAsBoolean();
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
