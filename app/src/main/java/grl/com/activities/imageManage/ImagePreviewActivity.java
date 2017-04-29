package grl.com.activities.imageManage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.wangu.com.grl.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Administrator on 6/30/2016.
 */
public class ImagePreviewActivity  extends Activity implements View.OnClickListener {

    // view
    ImageViewTouch mImage;

    boolean bLocal = false;
    // require
    String strPath;
    // value
    String uploadFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        getParamsFromIntent();
        getViewsFromID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strPath = intent.getStringExtra(Constant.PHOTO_PATH);
        if (intent.hasExtra("local")) {
            bLocal = Boolean.valueOf(intent.getStringExtra("local"));
        }

    }

    public void getViewsFromID () {
        mImage = (ImageViewTouch) findViewById(R.id.image);
    }

    public void initNavBar () {
    }

    public void initializeData () {
        // init value
        uploadFilePath = "";

        // init view
        mImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        if (bLocal) {
            // Local File
            Bitmap bmp = BitmapFactory.decodeFile(strPath);
            mImage.setImageBitmap(bmp);
        } else {
            // Server File
            if(!GlobalVars.loadImage(mImage, strPath))
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.user_default));
        }
    }

    public void setOnListener () {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
