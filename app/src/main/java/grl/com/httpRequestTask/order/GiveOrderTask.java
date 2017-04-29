package grl.com.httpRequestTask.order;

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

public class GiveOrderTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public GiveOrderTask(Activity context,
                         String orderSound,
                         String orderType,
                         String orderContent,
                         String orderSkill,
                         String orderSex,
                         String orderArea,
                         String phoneNumber,
                         String reward,
                         int submitTime,
                         double latitude,
                         double longitude,
                         int minDistance,
                         int maxDisatnce,
                         int direction, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(parent);
            Ion.with(context)
                    .load(GlobalVars.GIVE_ORDER_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", SelfInfoModel.userID)
                    .setBodyParameter("ord_sound", orderSound)
                    .setBodyParameter("ord_type", orderType)
                    .setBodyParameter("ord_content", orderContent)
                    .setBodyParameter("otherU_sex", orderSex)
                    .setBodyParameter("otherU_skill", orderSkill)
                    .setBodyParameter("otherU_area", orderArea)
                    .setBodyParameter("other_user", phoneNumber)
                    .setBodyParameter("ord_budget", reward)
                    .setBodyParameter("submit_time", String.valueOf(submitTime))
                    .setBodyParameter("latitude", String.valueOf(latitude))
                    .setBodyParameter("longitude", String.valueOf(longitude))
                    .setBodyParameter("max_distance", String.valueOf(maxDisatnce))
                    .setBodyParameter("min_distance", String.valueOf(minDistance))
                    .setBodyParameter("direction", String.valueOf(direction))
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
