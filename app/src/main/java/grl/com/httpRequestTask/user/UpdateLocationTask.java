package grl.com.httpRequestTask.user;

/**
 * Created by Administrator on 6/6/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

public class UpdateLocationTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public UpdateLocationTask(Activity context, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Gson gson = new Gson();
            String areaString = gson.toJson(SelfInfoModel.posArea);
            Ion.with(context)
                    .load(GlobalVars.UPDATE_LOCATION_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", SelfInfoModel.userID)
                    .setBodyParameter("latitude", String.valueOf(SelfInfoModel.latitude))
                    .setBodyParameter("longitude", String.valueOf(SelfInfoModel.longitude))
                    .setBodyParameter("pos_area", areaString)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            if(waitingDialog != null)
                            {
                                waitingDialog.dismiss();
                            }
                            try {
                                Log.d("Error", s);
                                JSONObject jsonObj = GlobalVars.getJsonDataFromString(s);
                                Boolean jsonResultData = jsonObj.getBoolean("result_data");
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
