package grl.com.activities.search.QRScan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.activities.search.QRScan.camera.CameraManager;
import grl.com.activities.search.QRScan.decoding.CaptureActivityHandler;
import grl.com.activities.search.QRScan.decoding.InactivityTimer;
import grl.com.activities.search.QRScan.view.ViewfinderView;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.discovery.UserInfoGetTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class QRScanActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private boolean isScanned;

    private TextView mTitle;
    private ImageView mGoHome;
    private ImageView imgScan;
    private boolean isNoCute = true;

    TranslateAnimation scanAnim;

    // require
    String strBackTitle;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qr_scan);
        CameraManager.init(getApplication());
        getParamsFromIntent();
        initControl();
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("isNoCute")) {
            isNoCute = true;
        } else {
            isNoCute = false;
        }
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isNoCute) {
                Utils.finish(QRScanActivity.this);
            } else {
//                Utils.start_Activity(QRScanActivity.this, SplashActivity.class);
//                Utils.finish(CaptureActivity.this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initControl() {
        initNavBar();
        // get view
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        imgScan = (ImageView) findViewById(R.id.imgScan);

        // start animation
        startScanAnim();
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.menu_qr_search));

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        startScanAnim();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        if (inactivityTimer != null)
            inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 扫描正确后的震动声音,如果感觉apk大了,可以删除
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public void handleDecode(com.google.zxing.Result result, Bitmap barcode) {
        inactivityTimer.onActivity();

        final String resultString = result.getText();
        String[] split = resultString.split(String.format("%n"));
        if(split.length == 2) {
            String bundle = split[0];
            if(!bundle.equals("com.wangu.GRL"))
                return;
        } else {
            return;
        }

        if(isScanned)
            return;
        stopScanAnim();
        isScanned = true;
        playBeepSoundAndVibrate();
        String userID = split[1];
        showOtherInfo(userID);
    }

    public void showOtherInfo (final String userID) {
        new UserInfoGetTask(this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                isScanned = false;

                if(!flag || Response == null)
                {
                    GlobalVars.showErrAlert(QRScanActivity.this);
                    return;
                }
                JSONObject result = (JSONObject) Response;
                if(result.length() > 0) {
                    String userName = result.getString("user_name");
                    String userPhoto = result.getString("user_photo");

                    Utils.start_Activity(QRScanActivity.this, OtherMainActivity.class,
                            new BasicNameValuePair(Constant.BACK_TITLE_ID, getString(R.string.menu_qr_search)),
                            new BasicNameValuePair(Constant.TITLE_ID, userName),
                            new BasicNameValuePair("user_id", userID),
                            new BasicNameValuePair("user_photo", userPhoto));
                } else {
                    startScanAnim();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                         // 되돌이단추를 누를 때의 처린 진행
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    public void startScanAnim () {
        if(scanAnim != null) {
            scanAnim.reset();
            imgScan.clearAnimation();
            scanAnim = null;
        }
        viewfinderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect rect = viewfinderView.getFrameRect();
                scanAnim = new TranslateAnimation(0.0f, 0.0f, rect.top - imgScan.getHeight() / 2, rect.bottom - imgScan.getHeight() / 2);
                scanAnim.setDuration(2000);
                scanAnim.setRepeatCount(-1);
                imgScan.startAnimation(scanAnim);
            }
        }, 1);
    }

    public void stopScanAnim () {
        if(scanAnim == null)
            return;
        scanAnim.reset();
        imgScan.clearAnimation();
        scanAnim = null;
    }
}
