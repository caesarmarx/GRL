package grl.com.httpRequestTask.order;

/**
 * Created by Administrator on 6/6/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

public class RealTimeTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public RealTimeTask(Context context, String userId, int limit, final HttpCallback callback) {
        this.callback = callback;

        try {
//            GlobalVars.showWaitDialog(parent);
            Ion.with(context)
                    .load(GlobalVars.REAL_TIME_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", userId)
                    .setBodyParameter("limit", String.format("%d", limit))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
//                            GlobalVars.hideWaitDialog();
                            try {
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                JSONArray jsonResultData = jsonObj.getJSONArray("result_data");
                                Log.e("json result", jsonResultData.toString());
                                Boolean result = jsonObj.getBoolean("result_code");
                                callback.onResponse(result, jsonResultData);
                                int a;
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
//            GlobalVars.hideWaitDialog();
        }
    }

    @Override
    public void onResponse(Boolean flag, Object response) {
      Log.e("login response: ", response.toString());
    }
}
