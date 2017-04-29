package grl.com.httpRequestTask.discovery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/19/2016.
 */
public class UserStatusSetTask  extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public UserStatusSetTask(Activity context,JSONObject params ,final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Ion.with(context)
                    .load(GlobalVars.USER_STATUS_SET)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", params.getString("user_id"))
                    .setBodyParameter("user_status", params.getString("user_status"))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            if(waitingDialog != null)
                            {
                                waitingDialog.dismiss();
                            }
                            try {
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                JSONObject jsonResultData = jsonObj.getJSONObject("result_data");
                                Boolean result = jsonObj.getBoolean("result_code");
                                callback.onResponse(result, jsonResultData);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("exception: ", e.toString());
        }
    }

    @Override
    public void onResponse(Boolean flag, Object Response) {

    }
}
