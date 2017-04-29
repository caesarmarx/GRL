package grl.com.httpRequestTask.tGroup;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/26/2016.
 */
public class LeagueSettingUpdateTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    public LeagueSettingUpdateTask(final Activity context, final JsonObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Ion.with(context)
                    .load(GlobalVars.LEAGUE_SETTING_UPDATE)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setStringBody(params.toString())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            JsonObject jsonObj = GlobalVars.getGJsonDataFromString(s);
                            JsonObject jsonResultData = jsonObj.getAsJsonObject("result_data");
                            Boolean result = jsonObj.get("result_code").getAsBoolean();
                            try {
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
