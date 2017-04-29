package grl.com.activities.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class QRViewActivity extends Activity implements View.OnClickListener {

    // view
    ImageView imgUserPhoto;
    ImageView imgQRCode;
    ImageView imgUserGender;
    TextView tvUserName;
    TextView tvUserJob;

    // require
    String strBackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_view);

        getParamsFromIntent();
        getViewsByID();
        initNavBar();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
    }

    public void getViewsByID () {
        imgUserPhoto = (ImageView) findViewById(R.id.user_photo);
        imgQRCode = (ImageView) findViewById(R.id.img_code);
        imgUserGender = (ImageView) findViewById(R.id.imgGender);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserJob = (TextView) findViewById(R.id.tvUserJob);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);

        ((TextView)findViewById(R.id.txt_title)).setText(R.string.qrcode_view_title);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
    }

    public void initializeData () {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (int) (displaymetrics.widthPixels - GlobalVars.dp2px(this, 100));
        int width = height;
        LinearLayout.LayoutParams params =(LinearLayout.LayoutParams)new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        imgQRCode.setLayoutParams(params);
        // init views
        tvUserName.setText(SelfInfoModel.userName);
        int gender = SelfInfoModel.userGender;
        switch (gender) {
            case 0:                     // male
                imgUserGender.setImageDrawable(getResources().getDrawable(R.drawable.male_icon_head));
                break;
            case 1:                     // female
                imgUserGender.setImageDrawable(getResources().getDrawable(R.drawable.female_icon_head));
                break;
            default:
                break;
        }
        tvUserJob.setText(SelfInfoModel.userJob);
        GlobalVars.loadImage(imgUserPhoto, SelfInfoModel.userPhoto);

        // generate qr qrcode
        String content = "com.wangu.GRL" + String.format("%n") + SelfInfoModel.userID;
        Bitmap qrcode = generateQRCode(content);
        imgQRCode.setImageBitmap(qrcode);
    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                         // 되돌이 단추를 누를 때의 처리 진행
            case R.id.txt_left:
                Utils.finish(this);
                break;
        }
    }

    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private Bitmap generateQRCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            // MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
                    500, 500);
            return bitMatrix2Bitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
