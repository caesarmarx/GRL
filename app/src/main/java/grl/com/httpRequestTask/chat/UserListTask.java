package grl.com.httpRequestTask.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/24/2016.
 */
public class UserListTask extends Object implements HttpCallback {
    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public UserListTask(Activity context, final String[] userIDs, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {

//            GlobalVars.showWaitDialog(this.parent);
//            Gson gson = new Gson();
//            String usersString = gson.toJson(userIDs);
//            usersString = "Array([0] => 5753ad310f3163ea775126a2\n)";
//            JSONArray array = new JSONArray();
//            array
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            params.put("session_id", Arrays.asList(SelfInfoModel.sessionID));
            params.put("user_id", Arrays.asList(SelfInfoModel.userID));
//            for (int i = 0; i < userIDs.size(); i++) {
//                params.put("to_userIds[]", Arrays.asList(userIDs.get(i)));
//            }
            params.put("to_userIds[]", Arrays.asList(userIDs));

            Ion.with(context)
                    .load(GlobalVars.USER_LIST_URL)
                    .setTimeout(Constant.HTTP_TIME_OUT)
                    .setBodyParameters(params)
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
