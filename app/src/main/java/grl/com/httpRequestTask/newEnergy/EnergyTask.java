package grl.com.httpRequestTask.newEnergy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/17/2016.
 */
public class EnergyTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public EnergyTask(Activity context, final JSONObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(this.parent);
            Ion.with(context)
                    .load(GlobalVars.ENERGY_GET)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setStringBody(params.toString())
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
                                JSONArray jsonResultData = jsonObj.getJSONArray("result_data");
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
