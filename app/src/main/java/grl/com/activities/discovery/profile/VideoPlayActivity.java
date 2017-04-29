package grl.com.activities.discovery.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import grl.com.configuratoin.Constant;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/28/2016.
 */
public class VideoPlayActivity extends Activity {

    // view
    VideoView vidview;

    // require
    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        getParamsFromIntent();
        getViewByID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        videoPath = intent.getStringExtra(Constant.VIDEO_PATH);
        Log.e("video path", videoPath);
    }

    public void getViewByID () {
        vidview = (VideoView) findViewById(R.id.myVideo);
    }

    public void initNavBar () {

    }

    public void initializeData () {
//        Uri vidUri = Uri.parse(videoPath);
//        vidview.setVideoURI(vidUri);
        vidview.setVideoPath(videoPath);

        // media controller
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidview);
        vidview.setMediaController(vidControl);
        //
        vidview.start();
    }

    public void setOnListener () {

    }
}
