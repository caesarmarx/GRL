package grl.com.httpRequestTask.search;

import android.app.Activity;
import android.app.ProgressDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 7/1/2016.
 */
public class SearchPhoneTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public SearchPhoneTask(Activity context, final JsonObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(context);
            Ion.with(context)
                    .load(GlobalVars.SEARRCH_PHONE)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", params.get("user_id").getAsString())
                    .setBodyParameter("search_phone_num", params.get("search_phone_num").getAsString())
                    .setBodyParameter("skip", params.get("skip").getAsString())
                    .setBodyParameter("limit",params.get("limit").getAsString())
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
                            } catch (JSONException e1) {
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
