package grl.com.httpRequestTask.discovery.crash;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/30/2016.
 */
public class CrashQuitTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    public CrashQuitTask(Activity context, final String userID, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Ion.with(context)
                    .load(GlobalVars.CRASH_QUIT)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", userID)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            try {
                                JsonObject jsonObj = GlobalVars.getGJsonDataFromString(s);
                                JsonObject jsonResultData = jsonObj.getAsJsonObject("result_data");
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
