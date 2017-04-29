package grl.com.httpRequestTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import java.io.File;

import grl.com.configuratoin.GlobalVars;
import grl.com.network.HttpCallback;

/**
 * Created by Administrator on 6/29/2016.
 */
public class FileDownloadTask extends Object implements HttpCallback {

    HttpCallback callback = null;
    Activity parent = null;

    ProgressDialog waitingDialog;
    public FileDownloadTask(Activity context, String path, final HttpCallback callback) {
        this.callback = callback;
        this.parent = context;
        try {
            final String fileName = "download";
            String result = "";
            if(path.equals(""))
                return ;
            path = GlobalVars.SEVER_ADDR + "/" + path;
            // get extension from path
            String[] split = path.split("\\.");
            String ext = "";
            if(split.length > 0) {
                ext = split[split.length - 1];
            }
            // generate saved path
            final String savePath = context.getFilesDir() + "/" + fileName + "." + ext;
            GlobalVars.showWaitDialog(this.parent);
            Ion.with(context)
                    .load(path)
                    .progressDialog(new ProgressDialog(context))
                    .write(new File(savePath))
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            GlobalVars.hideWaitDialog();
                            try {
                                Boolean flag;
                                if(result.exists())
                                    flag = true;
                                else
                                    flag = false;
                                callback.onResponse(flag, savePath);
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
