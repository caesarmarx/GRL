package grl.com.httpRequestTask.tGroup;

import android.app.Activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/24/2016.
 */
public class LessonMaterialGetTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    public LessonMaterialGetTask(final Activity context, final String userId, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            Ion.with(context)
                    .load(GlobalVars.LESSON_MATERIAL_GET_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameter("session_id", SelfInfoModel.sessionID)
                    .setBodyParameter("user_id", userId)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String s) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = GlobalVars.getJsonDataFromString(s);
                                Object jsonResultData = jsonObj.get("result_data");
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
