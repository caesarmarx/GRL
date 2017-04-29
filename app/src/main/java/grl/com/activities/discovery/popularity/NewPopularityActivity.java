package grl.com.activities.discovery.popularity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.builder.Builders;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import grl.com.activities.order.ContactsSelectActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.popularity.NewPopularityTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class NewPopularityActivity extends Activity implements View.OnClickListener {

    ImageView imgBack;
    TextView textBack;
    TextView textTitle;

    String filePath = "";

    EditText descriptionEdit;
    Button addImgBtn;
    ImageView photoView;
    TextView sureManView;
    Button newBtn;

    String sureMans = "";
    String estName = "";

    private final int SELECT_PHOTO = 1;
    private final int CONTACT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_popularity);

        Intent intent = getIntent();
        estName = intent.getStringExtra("est_name");

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    public void getViewByID () {
        descriptionEdit = (EditText)findViewById(R.id.et_description);
        addImgBtn = (Button)findViewById(R.id.btn_add_img);
        photoView = (ImageView)findViewById(R.id.img_photo);
        sureManView = (TextView)findViewById(R.id.tv_sure_man);
        newBtn = (Button)findViewById(R.id.btn_new_popularity);
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        addImgBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
        sureManView.setOnClickListener(this);
    }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.getString(R.string.popularity));
    }

    public void initializeData () {

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_add_img:
                addImage();
                break;
            case R.id.btn_new_popularity:
                uploadImage();
                break;
            case R.id.tv_sure_man:
                startContactActivity();
                break;
        }
    }

    public void addImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public void startContactActivity() {
        Intent intent = new Intent(this, ContactsSelectActivity.class);
        intent.putExtra(Constant.ORDER_DIRECTION, sureMans);
        startActivityForResult(intent, CONTACT_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case SELECT_PHOTO:
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap selectedBitmap = BitmapFactory.decodeFile(path);

                        addImgBtn.setVisibility(View.GONE);
                        photoView.setVisibility(View.VISIBLE);
                        photoView.setImageBitmap(selectedBitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CONTACT_REQUEST:
                    sureMans = data.getStringExtra(Constant.PHONE_CONTACT);
                    sureManView.setText(sureMans);
                    break;
            }
        }

    }

    public void uploadImage() {
        if (filePath == null || filePath.isEmpty()) {
            createPopularity("");
            return;
        }
        new FileUploadTask(this, filePath, "", new HttpCallback() {

            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(NewPopularityActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    int success = result.getInt("upload_result");
                    if(success == 1) {
                        String filePath = result.getString("file_path");
                        createPopularity(filePath);
                    } else {
                        GlobalVars.showErrAlert(NewPopularityActivity.this);
                        return;
                    }
                } catch (Exception e) {

                }

            }
        });
    }
    public void createPopularity(String photoPath) {

        new NewPopularityTask(this, descriptionEdit.getText().toString(), photoPath, sureMans, estName, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(NewPopularityActivity.this);
                    return;
                }

                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "publish_result");
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(NewPopularityActivity.this);
                    }

                } catch (Exception e) {

                }

            }
        });
    }
}
