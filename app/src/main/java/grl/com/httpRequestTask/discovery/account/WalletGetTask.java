package grl.com.httpRequestTask.discovery.account;

import android.app.Activity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 7/7/2016.
 */
public class WalletGetTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    public WalletGetTask(Activity context, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(this.parent);
            Ion.with(context)
                    .load(GlobalVars.GET_WALLET)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", SelfInfoModel.userID)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            GlobalVars.hideWaitDialog();

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