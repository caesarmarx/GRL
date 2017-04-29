package grl.com.activities.otherRel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import grl.com.activities.discovery.profile.VideoPlayActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.FileDownloadTask;
import grl.com.httpRequestTask.discovery.profile.UserProfileCertUpdate;
import grl.com.httpRequestTask.discovery.profile.UserProfileGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/30/2016.
 */
public class OtherProfileActivity extends Activity implements View.OnClickListener {

    // view
    ImageView imgThumb;
    ImageButton btnPlay;

    // task
    UserProfileGetTask userProfileGetTask;
    UserProfileCertUpdate userProfileCertUpdate;

    // require
    String strTitle;
    String strUserID;

    // value
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        getParamsFromInten();
        getViewsByID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromInten () {
        Intent intent = getIntent();
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        strUserID = intent.getStringExtra("user_id");
    }

    public void getViewsByID () {
        imgThumb = (ImageView) findViewById(R.id.imgThumb);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_right).setVisibility(View.VISIBLE);
        findViewById(R.id.img_right).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.other_info_title));
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);
        ((TextView)findViewById(R.id.txt_right)).setText(getString(R.string.user_cert_title));

        btnPlay.setOnClickListener(this);
    }

    public void initializeData () {
        // init value
        videoPath = "";

        // send request
        userProfileGetTask = new UserProfileGetTask(this, strUserID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherProfileActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                String path = result.get("video_path").getAsString();
                downloadVideo(path);
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_left:                     // 되돌이 단추를 누를 때의 처리 진행
            case R.id.img_back:
                Utils.finish(this);
                break;
            case R.id.txt_right:                    // 인증단추를 누를 때의 처리 진행
                confirmProfile();
                break;
            case R.id.btnPlay:                      // 미리보기단추를 누를 때의 처리 진행
                Utils.start_Activity(this, VideoPlayActivity.class,
                        new BasicNameValuePair(Constant.VIDEO_PATH, videoPath));
                break;
        }
    }

    public  void downloadVideo (String path) {
        if(path.equals(""))
            return;
        new FileDownloadTask(this, path, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag)
                {
                    GlobalVars.showErrAlert(OtherProfileActivity.this);
                    return;
                }
                videoPath = (String) Response;
                setThumbnail(videoPath);
            }
        });
    }

    public void setThumbnail (String path) {
        videoPath = path;
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        imgThumb.setImageBitmap(bMap);
    }

    // 사용자 프로화일을 인증하였을 때의 처리 진행
    public void confirmProfile () {
        userProfileCertUpdate = new UserProfileCertUpdate(this, this.strUserID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherProfileActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res = result.get("result").getAsBoolean();
                if(res) {       // success
                    GlobalVars.showCommonAlertDialog(OtherProfileActivity.this, getString(R.string.UPDATE_PROFILE_CERT), "");
                } else {        // failure
                    GlobalVars.showErrAlert(OtherProfileActivity.this);
                }
            }
        });
    }
}
