package grl.com.activities.imageManage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import grl.com.App;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.ImageUtils;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Administrator on 7/2/2016.
 */
public class OtherDesImageActivity extends Activity implements View.OnClickListener {

    // view
    ImageViewTouch mImage;

    // require
    String strBackTitle;
    String strTitle;
    String strPath;

    // value
    File cameraFile =  null;
    static final int CAMERA_REQUEST =  333;
    static final int IMAGE_CROP_REQUEST = 777;
    static final int RESULT_LOAD_IMAGE = 888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo_setting);

        getParamsFromIntent();
        getViewsFromID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        strPath = intent.getStringExtra(Constant.PHOTO_PATH);
        if(strTitle  == null || strTitle.equals(""))
            strTitle = getString(R.string.photo_title);
    }

    public void getViewsFromID () {
//        imgUserPhoto = (MyImageView) findViewById(R.id.user_photo);
        mImage = (ImageViewTouch) findViewById(R.id.image);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_right).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.txt_right)).setText(getString(R.string.more_title));
        ((TextView) findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView) findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {

        // init view
        mImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        if(!ImageUtils.setImageFromPath(strPath, mImage))
            mImage.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                                 // 되돌이단추를 누를 때의 처리 진행
            case R.id.txt_left:
                Intent intent = new Intent();
                intent.putExtra(Constant.PHOTO_PATH, strPath);
                setResult(RESULT_OK, intent);
                Utils.finish(this);
                break;
            case R.id.txt_right:                                // 설정단추를 누를 때의 처리 진행
                showSettingPopup();
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    // 설정 popup menu를 현시한다.
    public void showSettingPopup () {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.txt_right));
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.photo_setting_popup, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_take_photo:                  // 사진을 찍는 경우
                        onCameraAction();
                        return true;
                    case R.id.menu_select_photo:                // 이미 있는 화일을 불러들이는 경우
                        onImagePickerAction();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the menu
        popup.show();
    }

    public void onCameraAction () {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFile =getOutputMediaFile(CAMERA_REQUEST);
        cameraFile.getParentFile().mkdirs();
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void onImagePickerAction () {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAMERA_REQUEST:                             // take photo
                if (cameraFile != null && cameraFile.exists()) {
                    strPath = cameraFile.getAbsolutePath();
                    ImageUtils.setImageFromPath(strPath, mImage);
                }
                break;
            case RESULT_LOAD_IMAGE:                         // 그림을 불러들이는 경우
                Uri selectedImage = data.getData();
                strPath = ImageUtils.getRealPathFromURI(selectedImage);
                ImageUtils.setImageFromPath(strPath, mImage);
                break;
        }
    }

    public static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "JoshuaTree");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("JoshuaTree", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == CAMERA_REQUEST){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

}
