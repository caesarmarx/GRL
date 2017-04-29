package grl.com.activities.imageManage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/27/2016.
 */
public class ImageCropActivity extends Activity implements View.OnClickListener,
        CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener{

    // view
    CropImageView cropImageView;
    TextView tvNavRight;

    // require
    String strBackTitle;
    String strTitle;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        getParamsFromIntent();
        initNavBar();
        getViewsFromID();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        imagePath = intent.getStringExtra(Constant.PHOTO_PATH);
    }

    public void getViewsFromID () {
        tvNavRight = (TextView) findViewById(R.id.txt_right);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_right).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.txt_right)).setText(getString(R.string.done));
        ((TextView) findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView) findViewById(R.id.txt_title)).setText(strTitle);
    }

    public void initializeData () {
        Bitmap selectedImage = null;
        if(new File(imagePath).length() > 2048 * 2048) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            selectedImage = BitmapFactory.decodeFile(imagePath, options);
        } else {
            selectedImage = BitmapFactory.decodeFile(imagePath);
        }
        cropImageView.setImageBitmap(selectedImage);
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);

        cropImageView.setOnSetImageUriCompleteListener(this);
        cropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                             // 되돌이 단추를 누를 때의 처리
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:                            // 보관단추를 누를 때의 처리 진행
                cropImageView.getCroppedImageAsync();
                break;
        }
    }

    /**
     * Set the initial rectangle to use.
     */
    public void setInitialCropRect() {
        cropImageView.setCropRect(new Rect(100, 300, 500, 1200));
    }

    /**
     * Reset crop window to initial rectangle.
     */
    public void resetCropRect() {
        cropImageView.resetCropRect();
    }


    /**
     * Set the image to show for cropping.
     */
    public void setImageUri(Uri imageUri) {
        cropImageView.setImageUriAsync(imageUri);
        //        CropImage.activity(imageUri)
        //                .start(getContext(), this);
    }

    private void handleCropResult(Uri uri, Bitmap bitmap, Exception error) {
        try {
            Intent intent = new Intent();

            String filename = "bitmap.png";
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

//            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream stream = openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream);
            stream.close();
            bitmap.recycle();

            intent.putExtra(Constant.PHOTO_PATH, filename);
            setResult(RESULT_OK, intent);
            Utils.finish(this);
        }catch (Exception e) {}
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {

        handleCropResult(null, bitmap, error);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "happy", null);
        return Uri.parse(path);
    }
}
