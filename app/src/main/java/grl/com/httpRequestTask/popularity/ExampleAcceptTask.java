package grl.com.httpRequestTask.popularity;

/**
 * Created by Administrator on 6/6/2016.
 */

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

public class ExampleAcceptTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public ExampleAcceptTask(Activity context, String userId, String toUserId, String estName, int index, int acceptValue, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(parent);
            Ion.with(context)
                    .load(GlobalVars.EXAMPLE_ACCEPT_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", userId)
                    .setBodyParameter("from_userid", toUserId)
                    .setBodyParameter("est_name", estName)
                    .setBodyParameter("example_index", String.valueOf(index))
                    .setBodyParameter("accept_value", String.valueOf(acceptValue))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            GlobalVars.hideWaitDialog();
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
            GlobalVars.hideWaitDialog();
        }
    }

    @Override
    public void onResponse(Boolean flag, Object response) {
      Log.e("login response: ", response.toString());
    }
}
