package grl.com.network;

import org.json.JSONException;

/**
 * Created by Administrator on 6/6/2016.
 */
public interface HttpCallback {
    public void onResponse(Boolean flag, Object Response) throws JSONException;
}
