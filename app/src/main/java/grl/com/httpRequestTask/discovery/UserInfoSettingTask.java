package grl.com.httpRequestTask.discovery;

import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.App;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/27/2016.
 */
public class UserInfoSettingTask  extends Object implements HttpCallback {
    HttpCallback callback = null;

    ProgressDialog waitingDialog;
    public UserInfoSettingTask(final JSONObject params, final HttpCallback callback) {
        this.callback = callback;
        try {
            Ion.with(App.getInstance())
                    .load(GlobalVars.USER_SETTING)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", params.getString("user_id"))
                    .setBodyParameter("user_name", params.getString("user_name"))
                    .setBodyParameter("user_sex", params.getString("user_sex"))
                    .setBodyParameter("user_nickname", params.getString("user_nickname"))
                    .setBodyParameter("user_job", params.getString("user_job"))
                    .setBodyParameter("user_area", params.getString("user_area"))
                    .setBodyParameter("user_photo", params.getString("user_photo"))
                    .setBodyParameter("user_skill", params.getString("user_skill"))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
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
