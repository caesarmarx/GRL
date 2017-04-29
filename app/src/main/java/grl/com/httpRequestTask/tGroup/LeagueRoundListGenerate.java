package grl.com.httpRequestTask.tGroup;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/26/2016.
 */
public class LeagueRoundListGenerate extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    public LeagueRoundListGenerate(Activity context, final JsonObject params, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            GlobalVars.showWaitDialog(this.parent);
            Ion.with(context)
                    .load(GlobalVars.LEAGUE_ROUND_LIST)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("teacher_id", params.get("teacher_id").getAsString())
                    .setBodyParameter("start_time", params.get("start_time").getAsString())
                    .setBodyParameter("end_time", params.get("end_time").getAsString())
                    .setBodyParameter("league_name", params.get("league_name").getAsString())
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            GlobalVars.hideWaitDialog();

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
