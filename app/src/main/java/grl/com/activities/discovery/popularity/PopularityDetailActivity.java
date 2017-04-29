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

import org.json.JSONObject;
import org.w3c.dom.Text;

import grl.com.activities.order.ContactsSelectActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.FileUploadTask;
import grl.com.httpRequestTask.popularity.NewPopularityTask;
import grl.com.httpRequestTask.popularity.SureAcceptTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

public class PopularityDetailActivity extends Activity implements View.OnClickListener {

    ImageView userPhotoView;
    TextView userNameView;

    TextView descriptionView;
    ImageView photoView;

    Button acceptBtn;
    Button declineBtn;

    String twitterId;

    String userName;
    String userPhoto;

    String description;
    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_popularity);

        Intent intent = getIntent();

        twitterId = intent.getStringExtra("twitter_id");
        userName = intent.getStringExtra("user_name");
        userPhoto = intent.getStringExtra("user_photo");
        description = intent.getStringExtra("describe_content");
        photoPath = intent.getStringExtra("photo_path");

        getViewByID();
        initNavBar();
        setOnListener();
        initializeData();
    }

    private void getViewByID () {
        descriptionView = (TextView)findViewById(R.id.et_description);
        photoView = (ImageView)findViewById(R.id.img_photo);
        userPhotoView = (ImageView) findViewById(R.id.img_user_photo);
        userNameView = (TextView) findViewById(R.id.tv_user_name);
        acceptBtn = (Button) findViewById(R.id.btn_accept_sure);
        declineBtn = (Button) findViewById(R.id.btn_decline_sure);
    }

    private void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        acceptBtn.setOnClickListener(this);
        declineBtn.setOnClickListener(this);
    }

    private void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText("");
    }

    private void initializeData () {
        userNameView.setText(userName);
        GlobalVars.loadImage(userPhotoView, userPhoto);

        if (photoPath == null || photoPath.isEmpty())
            photoView.setVisibility(View.GONE);
        descriptionView.setText(description);

    }

    private void acceptSureAction() {
        sendAcceptRequest(1);
    }

    private void declineSureAction() {
        sendAcceptRequest(0);
    }

    private void sendAcceptRequest(int value) {
        new SureAcceptTask(this, SelfInfoModel.userID, twitterId, value, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {

                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(PopularityDetailActivity.this);
                    return;
                }
                try {
                    JSONObject result = (JSONObject) response;
                    Boolean success = GlobalVars.getBooleanFromJson(result, "result");
                    if (success) {
                        finish();
                    } else {
                        GlobalVars.showErrAlert(PopularityDetailActivity.this);
                    }

                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.btn_accept_sure:
                acceptSureAction();
                break;
            case R.id.btn_decline_sure:
                declineSureAction();
                break;
        }
    }
}
