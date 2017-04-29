package grl.com.httpRequestTask.friendGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/23/2016.
 */
public class FriendEstTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;

    public FriendEstTask(Activity context, final JSONObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(this.parent);
            Ion.with(context)
                    .load(GlobalVars.FRIEND_EST)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", params.getString("session_id"))
                    .setBodyParameter("userfrom_id", params.getString("userfrom_id"))
                    .setBodyParameter("userto_id", params.getString("userto_id"))
                    .setBodyParameter("group_id", params.getString("group_id"))
                    .setBodyParameter("est_content", params.getString("est_content"))
                    .setBodyParameter("est_type", params.getString("est_type"))
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            GlobalVars.hideWaitDialog();
                            if (waitingDialog != null) {
                                waitingDialog.dismiss();
                            }
                            try {
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                JSONObject jsonResultData = jsonObj.getJSONObject("result_data");
                                Log.e("json result", jsonResultData.toString());
                                Boolean result = jsonObj.getBoolean("result_code");
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