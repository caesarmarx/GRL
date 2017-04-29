package grl.com.activities.otherRel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.io.File;
import java.util.List;

import grl.com.activities.imageManage.OtherDesImageActivity;
import grl.com.adapters.otherRel.RemarkPhoneAdapter;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.dbUtil.AliasDes;
import grl.com.configuratoin.dbUtil.DBManager;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class OtherDescriptionActivity extends Activity implements View.OnClickListener {

    // view
    TextView tvLeft;
    RecyclerView recyclerView;
    EditText etAlias;
    EditText etDes;
    ViewGroup layoutImage;
    ImageView imgPhoto;
    // require
    String userID;

    // value
    RemarkPhoneAdapter remarkPhoneAdapter;
    String desImagePath;
    AliasDes dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_des);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    public void getDataFromIntent () {
        Intent intent = this.getIntent();
        userID = intent.getStringExtra("user_id");
    }

    public void getViewByID () {
        tvLeft = (TextView) findViewById(R.id.txt_left);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        etAlias = (EditText) findViewById(R.id.etAlias);
        etDes = (EditText) findViewById(R.id.etDescription);
        layoutImage = (ViewGroup) findViewById(R.id.layoutImage);
        imgPhoto = (ImageView) findViewById(R.id.imgDes);
    }

    public void initNavBar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.cancel));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.other_description));
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(this.getString(R.string.save));
     }

    public void initializeData () {
        // 자료기지로부터 자료얻기
        AliasDes temp = DBManager.getAliaDes(userID);
        if(temp == null) {
            AliasDes aliasDes = new AliasDes(userID);
            aliasDes.save();
            this.dataModel = DBManager.getAliaDes(userID);
        } else {
            this.dataModel = temp;
        }
        // set value in views
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)this.tvLeft.getLayoutParams();
        params.setMargins(10, 0, 0, 0); //substitute parameters for left, top, right, bottom
        tvLeft.setLayoutParams(params);

        // set up recyclerAdapter
        remarkPhoneAdapter = new RemarkPhoneAdapter(this);
        RecyclerView.LayoutManager mLayooutMananger = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayooutMananger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(remarkPhoneAdapter);

        // init views
        layoutImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutImage.getLayoutParams();
                int width = displayMetrics.widthPixels;
                layoutParams.height = width;
                layoutImage.setLayoutParams(layoutParams     );
            }
        }, 1);
        List<String> lst = dataModel.getMobileLst();
        this.etAlias.setText(this.dataModel.getAlias());
        this.etDes.setText(this.dataModel.getDesText());
        remarkPhoneAdapter.notifyData(lst);
        setImageFromPath(dataModel.getDesImage());

    }

    public void setOnListener () {
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
        layoutImage.setOnClickListener(this);
    }

    public void onSaveData () {
        this.dataModel.setAlias(etAlias.getText().toString());
        this.dataModel.setDesText(etDes.getText().toString());
        this.dataModel.setMobileList(remarkPhoneAdapter.getPhoneList());
        this.dataModel.save();
        Utils.finish(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.txt_left:                     // 취소 단추를 누른 경우
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("警报");
                builder.setMessage("保存本次编辑");
                builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveData();
                    }
                });
                builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.finish(OtherDescriptionActivity.this);
                    }
                });
                builder.create().show();
                break;
            case R.id.txt_right:                    // 보관 단추를 누른 경우
                this.onSaveData();
                break;
            case R.id.layoutImage:                  // 그림 설정 진행
                Utils.start_ActivityForResult(this, OtherDesImageActivity.class, R.id.layoutImage,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, "备注"),
                        new BasicNameValuePair(Constant.TITLE_ID, "图片"),
                        new BasicNameValuePair(Constant.PHOTO_PATH, dataModel.getDesImage()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layoutImage:
                String str = data.getStringExtra(Constant.PHOTO_PATH);
                dataModel.setDesImage(str);
                setImageFromPath(str);
                break;
        }
    }

    public Boolean setImageFromPath (String path){
        File imgFile = new File(path);
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imgPhoto.setImageBitmap(myBitmap);

            return  true;

        }
        return  false;
    }

}
