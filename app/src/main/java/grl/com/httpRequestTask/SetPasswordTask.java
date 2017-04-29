package grl.com.httpRequestTask;

/**
 * Created by Administrator on 6/6/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.network.HttpCallback;

public class SetPasswordTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public SetPasswordTask(Activity context, final String userPhone, final String userPassword, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("user_phone",userPhone);
            requestJson.addProperty("user_pw",userPassword);

            Log.e("json request: ", requestJson.toString());

            GlobalVars.showWaitDialog(this.parent);

            Ion.with(context)
                    .load(GlobalVars.SET_PASSWORD_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("user_phone", userPhone)
                    .setBodyParameter("user_pw", userPassword)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            GlobalVars.hideWaitDialog();
                            if(waitingDialog != null)
                            {
                                waitingDialog.dismiss();
                            }
                            try {
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                JSONObject jsonResultData = jsonObj.getJSONObject("result_data");
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

        }
    }

    @Override
    public void onResponse(Boolean flag, Object response) {
      Log.e("login response: ", response.toString());
    }
}
