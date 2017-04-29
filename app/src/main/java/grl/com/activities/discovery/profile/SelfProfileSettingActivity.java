package grl.com.activities.discovery.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.FileDownloadTask;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.discovery.profile.UserProfileGetTask;
import grl.com.httpRequestTask.discovery.profile.UserProfileSetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/28/2016.
 */
public class SelfProfileSettingActivity extends Activity implements View.OnClickListener {

    // view
    ImageButton btnPreview;
    ImageButton btnSetting;
    ImageView imgPreview;
    RelativeLayout layoutPreview;

    // task
    UserProfileSetTask userProfileSetTask;
    UserProfileGetTask userProfileGetTask;

    // response
    String videoPath;

    // value
    static final int REQUEST_VIDEO_CAPTURE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_profile);

        getParamsFromIntent();
        getViewID();
        iniViews();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {

    }

    public void getViewID () {
        btnPreview = (ImageButton) findViewById(R.id.btnPreview);
        btnSetting = (ImageButton) findViewById(R.id.btnSet);
        layoutPreview = (RelativeLayout) findViewById(R.id.layoutPreview);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
    }

    public void iniViews () {
        // view들의 크기를 재설정한다.
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (int) ((displaymetrics.widthPixels - GlobalVars.dp2px(this, 60)) / 2);
        int width = height;

        RelativeLayout.LayoutParams layoutParams1 =(RelativeLayout.LayoutParams)new RelativeLayout.LayoutParams(width, height);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT); //ALIGN_PARENT_RIGHT / LEFT etc.
        layoutPreview.setLayoutParams(layoutParams1);
        RelativeLayout.LayoutParams layoutParams2 =(RelativeLayout.LayoutParams)new RelativeLayout.LayoutParams(width, height);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); //ALIGN_PARENT_RIGHT / LEFT etc.
        btnSetting.setLayoutParams(layoutParams2);
    }
    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.setting_title));
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.profile_title));
    }

    public void initializeData () {
        this.videoPath = "";

        getUserProfile();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);

        findViewById(R.id.btnPreview).setOnClickListener(this);
        findViewById(R.id.btnSet).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                             // 되돌이 단추를 누를때의 처리 진행
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btnPreview:                           // 미리보기단추를 누를 때의 처리 진행
                Utils.start_Activity(this, VideoPlayActivity.class,
                        new BasicNameValuePair(Constant.VIDEO_PATH, videoPath));
                break;
            case R.id.btnSet:                               // 설정단추를 누를 때의 처리 진행
                dispatchTakeVideoIntent();
                break;
        }
    }

    public void getUserProfile () {
        userProfileGetTask = new UserProfileGetTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                String path = result.get("video_path").getAsString();
                downloadVideo(path);
            }
        });
    }

    public  void downloadVideo (String path) {
        if(path.equals(""))
            return;
        new FileDownloadTask(this, path, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag)
                {
                    GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                    return;
                }

                videoPath = (String) Response;
                setThumbnail(videoPath);
            }
        });
    }

    private void dispatchTakeVideoIntent () {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                Uri videoUri = data.getData();
                String path = GlobalVars.getRealPathFromURI(this, videoUri);
                setThumbnail(path);

                uploadVideo(Uri.parse(path));
                break;
        }
    }

    public void setThumbnail (String path) {
        videoPath = path;
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        imgPreview.setImageBitmap(bMap);
    }

    public void uploadVideo(Uri videoUri) {
        if (videoUri == null) {
            return;
        }
        new FileUploadTask(this, videoUri.getPath(), Constant.MSG_VIDEO_TYPE, new HttpCallback() {

            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    int success = result.getInt("upload_result");
                    if(success == 1) {
                        String filePath = result.getString("file_path");
                        updateUserProfile(filePath);
                    } else {
                        GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                        return;
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    // profile 정보설정 진행
    public void updateUserProfile (String videoPath) {
        userProfileSetTask = new UserProfileSetTask(this, videoPath, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res = result.get("result").getAsBoolean();
                if(res) {                               // success

                } else {                                // failure
                    GlobalVars.showErrAlert(SelfProfileSettingActivity.this);
                }
            }
        });
    }
}
