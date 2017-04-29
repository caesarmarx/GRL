package grl.com.activities.otherRel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grl.com.activities.consult.ChatActivity;
import grl.com.activities.discovery.order.OtherOrderActivity;
import grl.com.activities.discovery.popularity.OtherPopularityActivity;
import grl.com.activities.energy.tGroup.TGroupShowActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.UserModel;
import grl.com.httpRequestTask.order.PlanetUserTask;
import grl.com.httpRequestTask.otherRel.IsRelationTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.star.PlanetShowView;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/9/2016.
 */
public class OtherMainActivity extends Activity implements View.OnClickListener {

    // view
    PlanetShowView planetView;
    ImageView imgUserPhoto;

    // task
    IsRelationTask isRelationTask;

    // response
    String isConnected;
    private ArrayList<UserModel> planetUsers;

    // required parameters
    String strTitle;
    String strBackTitle;
    String userID;
    String userName;
    String userPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_main);

        this.getParamsFromIntent();
        this.getViewByID();
        this.initNabVar();
        this.initializeData();
        this.setOnListener();
    }

    public void getParamsFromIntent () {
        // get title of back and main title of navgation bar
        Intent intent = this.getIntent();
        this.strTitle = intent.getStringExtra(Constant.TITLE_ID);
        this.strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        this.userID = intent.getStringExtra("user_id");
        this.userName = intent.getStringExtra("user_name");
        this.userPhotoPath = intent.getStringExtra("user_photo");
    }
    public void getViewByID () {
        this.planetView = (PlanetShowView)findViewById(R.id.planet_view);
        this.imgUserPhoto = (ImageView)findViewById(R.id.img_user_photo);
    }

    public void initNabVar () {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(this.strTitle);
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(this.getString(R.string.more_title));
    }

    public void initializeData () {
        // init value
        isConnected = Constant.REL_DISCONNECTED;
        planetUsers = new ArrayList<UserModel>();

        // 상대방과의 관계가 존재하는가를 얻는다.
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("other_userid", userID);
        isRelationTask = new IsRelationTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(OtherMainActivity.this);
                    return;
                }
                try {
                    JsonObject result = (JsonObject) Response;
                    Boolean rel_flag = result.get("result").getAsBoolean();
                    if(rel_flag)
                        isConnected = Constant.REL_CONNECTED;
                    else
                        isConnected = Constant.REL_DISCONNECTED;
                }catch (Exception e) {}
            }
        });

        // init view
        GlobalVars.loadImage(imgUserPhoto, userPhotoPath);
        planetView.initView();
        refreshPlanet();
    }

    private void refreshPlanet() {
        new PlanetUserTask(OtherMainActivity.this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object response) {
                if(!flag || response == null) {                 //failure
                    GlobalVars.showErrAlert(OtherMainActivity.this);
                    return;
                }

                try {
                    JSONArray result = (JSONArray) response;
                    planetUsers.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject object = result.getJSONObject(i);
                        UserModel model = new UserModel();
                        model.parseFromJson(object);
                        model.state = "";
                        planetUsers.add(model);
                    }
                } catch (Exception ex) {

                }
                planetView.refreshData(planetUsers);
            }
        });
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);

        findViewById(R.id.tvOtherInfo).setOnClickListener(this);
        findViewById(R.id.tvOtherOrder).setOnClickListener(this);
        findViewById(R.id.tvOtherTGroup).setOnClickListener(this);
        findViewById(R.id.tvOtherCheer).setOnClickListener(this);
        findViewById(R.id.btnChat).setOnClickListener(this);
        findViewById(R.id.btnRequest).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:                     // 더보기 단추를 누른 경우
                Utils.start_Activity(this, OtherSettingActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair(Constant.REL_FLAG, isConnected),
                        new BasicNameValuePair("user_id", userID));
                break;
            case R.id.tvOtherInfo:                   // 상대방의 정보보기 단추를 누른 경우
                Utils.start_Activity(this, OtherInfoViewActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair("user_id",userID),
                        new BasicNameValuePair("user_id",userID));
                break;
            case R.id.tvOtherOrder:                  // 상대방의 령보기 단추를 누른 경우
                Utils.start_Activity(this, OtherOrderActivity.class,
                        new BasicNameValuePair(Constant.USER_ID,userID));
                break;
            case R.id.tvOtherTGroup:                  // 상대방위 스승제자계 단추를 누른 경우
                Utils.start_Activity(this, TGroupShowActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair(Constant.TITLE_ID, strTitle + getString(R.string.tgroup_title)),
                        new BasicNameValuePair("user_id", userID),
                        new BasicNameValuePair("user_photo", userPhotoPath));
                break;
            case R.id.tvOtherCheer:                   // 상대방의 인기보기 단추를 누른 경우
                Utils.start_Activity(this, OtherPopularityActivity.class,
                        new BasicNameValuePair(Constant.USER_ID,userID));
                break;
            case R.id.btnChat:                        // 상대방과의 대화 진행 단추
                Utils.start_Activity(this, ChatActivity.class,
                        new BasicNameValuePair(Constant.USER_ID,userID),
                        new BasicNameValuePair(Constant.USER_NAME, userName),
                        new BasicNameValuePair(Constant.USER_PHOTO, userPhotoPath));
                break;
            case R.id.btnRequest:                     // 상대방에게 능량요청단추
                Utils.start_Activity(this, EnergyRequestActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, strTitle),
                        new BasicNameValuePair("other_userid", userID));
                break;
        }
    }
}
