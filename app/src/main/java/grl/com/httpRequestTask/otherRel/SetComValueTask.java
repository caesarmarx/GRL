package grl.com.httpRequestTask.otherRel;

import android.app.Activity;
import android.app.ProgressDialog;

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
public class SetComValueTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public SetComValueTask(Activity context, final JsonObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Ion.with(context)
                    .load(GlobalVars.SET_COM_VALUE)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", params.get("user_id").getAsString())
                    .setBodyParameter("other_userid", params.get("other_userid").getAsString())
                    .setBodyParameter("block", params.get("block").getAsString())
                    .setBodyParameter("dnd", params.get("dnd").getAsString())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            try {
                                JsonObject jsonObj = GlobalVars.getGJsonDataFromString(s);
                                JsonObject jsonResultData = jsonObj.getAsJsonObject("result_data");
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
