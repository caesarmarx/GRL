package grl.com.activities.discovery.crash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.TGroupMemberModel;
import grl.com.httpRequestTask.discovery.crash.CrashListTask;
import grl.com.httpRequestTask.discovery.crash.CrashQuitTask;
import grl.com.httpRequestTask.discovery.crash.CrashRequestTask;
import grl.com.httpRequestTask.discovery.crash.CrashResultTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.Crash.CrashSettingDialog;
import grl.com.subViews.dialogues.PhotoCardDialog;
import grl.com.subViews.view.Crash.CrashItemView;
import grl.com.subViews.view.Crash.ExtraButView;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class CrashActivity extends Activity implements View.OnClickListener, View.OnTouchListener{

    // view
    ViewGroup layoutContent;
    CrashItemView mineView;
    CrashItemView acceptView;
    ExtraButView customView;

    // task
    CrashListTask crashListTask;
    CrashRequestTask crashRequestTask;
    CrashQuitTask crashQuitTask;
    CrashResultTask crashResultTask;

    // response
    JsonArray visibleCrashListData;

    // value
    TGroupMemberModel tGroupMemberModel;
    JsonArray invisibleCrashListData;
    JsonArray acceptResult;
    List<CrashItemView> othersArray;
    int otherSex;
    int otherArea;
    final String PREFER_NAME = "my_pref";
    final String GENDER_TYPE = "gender_type";
    final String AREA_TYPE = "area_type";
    int visibleCnt = 5;
    private int _xDelta;
    private int _yDelta;
    Boolean isAnim;
    Boolean isAccepted;
    Point oldPos;
    RelativeLayout.LayoutParams oldPrams;
    RelativeLayout.LayoutParams acceptedViewParams;

    public static int minesize = 70;
    public static int othersize = 50;

    public static int ANIM_IN_TIME = 100;
    public static int ANIM_OUT_TIME = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        this.getDataFromIntent();
        this.getViewByID();
        this.initNavBar();
        this.initializeData();
        this.setOnListener();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences pref = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(GENDER_TYPE, otherSex);
        editor.putInt(AREA_TYPE, otherArea);
        editor.commit();

        quitCrash();
        super.onDestroy();
    }

    public void getDataFromIntent () {
        Intent inten = this.getIntent();
    }

    public void getViewByID () {
        layoutContent = (ViewGroup) findViewById(R.id.layoutContent);
    }

    public void initNavBar () {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(getString(R.string.tab_discorvery_title));
        ((TextView)findViewById(R.id.txt_title)).setText(getString(R.string.crash_title));
        ((ImageView)findViewById(R.id.img_right)).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.img_right)).setImageDrawable(getResources().getDrawable(R.drawable.setting_btn_icon));

    }

    public void initializeData () {
        // init value
        tGroupMemberModel = new TGroupMemberModel(this, SelfInfoModel.userID);
        acceptResult = new JsonArray();
        isAnim = false;
        isAccepted = false;
        // get setting data from pref
        SharedPreferences prefs = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        otherSex = prefs.getInt(GENDER_TYPE, 2);
        otherArea = prefs.getInt(AREA_TYPE, 2);

        mineView = new CrashItemView(this);
        mineView.initView(true);
        mineView.showAnim(1000, 0);
        int height = (int) GlobalVars.dp2px(this, minesize);
        this.layoutContent.addView(mineView);
        // center in parent
        layoutContent.invalidate();
        layoutContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mineView.getLayoutParams();
                int marginLeft =(layoutContent.getWidth() - mineView.getWidth()) / 2;
                int marginTop = (layoutContent.getHeight() - mineView.getHeight()) / 2;
                params.leftMargin = marginLeft;
                params.topMargin = marginTop;
                mineView.setLayoutParams(params);
                // generate others and mine

                layoutContent.invalidate();
                mineView.setUserPhoto(SelfInfoModel.userPhoto);
                oldPos  = new Point(marginLeft, marginTop);
            }
        }, 1);

//        this.getCrashLst();
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.img_right).setOnClickListener(this);

        mineView.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                         // 되돌이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.img_right:                        // 설정단추를 누른 경우
                showCrashSettingDailog();
                break;
            case R.id.btnConsider:                      // 스승/제자모시기 단추를 누른 경우
                setResultType(false);
                break;
            case R.id.btnGiveup:                        // 포기단추를 누른 경우
                setResultType(true);
                break;
        }
    }

    public void getCrashLst () {
        // init value
        visibleCrashListData = new JsonArray();
        invisibleCrashListData = new JsonArray();
        othersArray = new ArrayList<CrashItemView>();
        // request params
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("other_sex", otherSex);
        params.addProperty("other_area", otherArea);
        crashListTask = new CrashListTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(CrashActivity.this);
                    return;
                }
                visibleCrashListData = (JsonArray) Response;
                generateOtherBeing(true);
            }
        });
    }

    // 부딪치기에 참가한 사용자들을 현시한다.
    public void generateOtherBeing (Boolean isInit) {
        int[] layoutPos = new int[2];
        layoutContent.getLocationOnScreen(layoutPos);
        float posX, posY;
        int xMax = (int) (this.layoutContent.getWidth() - (mineView.getWidth() + GlobalVars.dp2px(this, 5)));
        int yMax = (int) (this.layoutContent.getHeight() - (mineView.getWidth() + GlobalVars.dp2px(this, 60)));
        int otherCnt = visibleCrashListData.size();
        long duration = 1500;
        long delay = 0;
        float limit = mineView.getWidth() + GlobalVars.dp2px(this, 15);
        int start = 0;
        if(!isInit) {                       // insert new item
            start = otherCnt - 1;
        }
        for(int i = start; i < otherCnt; i ++) {
            posX = (float) (Math.random() * xMax + GlobalVars.dp2px(this, 25));
            posY = (float) (Math.random() * yMax + GlobalVars.dp2px(this, 100));
            int k = i;
            for(int j = 0; j < i; j ++) {
                CrashItemView item = othersArray.get(j);
                int[] tempPos = new int[2];
                item.getLocationOnScreen(tempPos);

                int otherX = tempPos[0] + item.getWidth() / 2 - layoutPos[0];
                int otherY = tempPos[1] + item.getHeight() / 2 - layoutPos[1];
                float distance = getDistance(new Point((int)posX, (int)posY), new Point(otherX, otherY));
                if(distance < limit) {
                    k --;
                    break;
                }
            }
            if(k != i) {
                i = k;
                continue;
            }

            if(getDistance(new Point(mineView.getLeft(), mineView.getTop()), new Point((int)posX, (int)posY)) < limit) {
                i --;
                continue;
            }

            int[] myPos = new int[2];
            mineView.getLocationOnScreen(myPos);
            int myX = myPos[0] + mineView.getWidth() / 2 - layoutPos[0];
            int myY = myPos[1] + mineView.getHeight() / 2 - layoutPos[1];
            if(getDistance(new Point((int)posX, (int)posY), new Point(myX, myY)) < limit) {
                i --;
                continue;
            }

            // generate item
            CrashItemView other = new CrashItemView(this);
            other.setData(visibleCrashListData.get(i).getAsJsonObject());

            if(tGroupMemberModel.isMemberofGroup(other.userId)) {
                other.isCrashed = 2;
                other.initView(false);
                other.showStatus();
            }
            layoutContent.addView(other);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) other.getLayoutParams();
            params.leftMargin = (int) (posX - layoutPos[0]);
            params.topMargin = (int) (posY - layoutPos[1]);
            other.setLayoutParams(params);
            other.showAnim(duration, delay);
            delay += duration;

            // add item
            othersArray.add(other);
            final String otherUserID = other.userId;
            other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 사진을 눌렀을 때 두상카드를 현시한다.
                    Intent intent = new Intent(CrashActivity.this, PhotoCardDialog.class);
                    intent.putExtra("user_id", otherUserID);
                    intent.putExtra(Constant.BACK_TITLE_ID, getString(R.string.crash_title));
                    startActivity(intent);
                }
            });
        }
    }

    // 접수가 들어왔을 때의 처리 진행
    public void acceptAnimation (final String otherUserID) {
        int i ;
        final int acceptedCnt = acceptResult.size();
        String status = "";
        if(acceptedCnt > 0)
            status = String.valueOf(acceptedCnt);
        mineView.setTxtStatus(status);
        for(i = 0; i < visibleCrashListData.size(); i ++) {
            JsonObject item = visibleCrashListData.get(i).getAsJsonObject();
            if(otherUserID.equals(item.get("user_id").getAsString())) {
                acceptView = othersArray.get(i);
                break;
            }
        }
        if(i > visibleCrashListData.size())
            return;
        float dstX = 0, dstY = 0;
        Point otherPos = new Point(acceptView.getLeft(), acceptView.getTop());
        Point minePos = new Point(mineView.getLeft(), mineView.getTop());

        final float d = GlobalVars.dp2px(this, (minesize + othersize)/2);
        if(otherPos.x < minePos.x && otherPos.y != minePos.y){
            dstX = minePos.x - d;
            dstY = minePos.y;
        } else if(otherPos.x > minePos.x && otherPos.y != minePos.y){
            dstX = minePos.x + d;
            dstY = minePos.y;
        } else if(otherPos.x == minePos.x && otherPos.y > minePos.y){
            dstY = minePos.y + d;
            dstX = minePos.x;
        } else if(otherPos.x == minePos.x && otherPos.y < minePos.y){
            dstY = minePos.y - d;
            dstX = minePos.x;
        }
        // animation
        float deltaX = dstX - otherPos.x;
        float deltaY = dstY - otherPos.y;
        final float finalDstX = deltaX + GlobalVars.dp2px(this, (minesize - othersize) / 2);
        final float finalDstY = deltaY + GlobalVars.dp2px(this, (minesize - othersize) / 2);
        acceptedViewParams = (RelativeLayout.LayoutParams) acceptView.getLayoutParams();
        TranslateAnimation anim = new TranslateAnimation(0.0f, finalDstX, 0.0f, finalDstY);
        anim.setRepeatCount(0);
        anim.setDuration(1000);
//        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                acceptedViewParams = (RelativeLayout.LayoutParams) acceptView.getLayoutParams();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                acceptedViewParams.leftMargin = (int) (acceptedViewParams.leftMargin + finalDstX);
                acceptedViewParams.topMargin = (int) (acceptedViewParams.topMargin + finalDstY);
                acceptView.setLayoutParams(acceptedViewParams);
//
//                float distance = getDistance(new Point((int)finalDstX, (int)finalDstY), new Point(mineView.getLeft(), mineView.getTop()));
//                if(distance > d) {
//                    acceptAnimation(otherUserID);
//                    return;
//                }
                isAccepted = true;
//
                // add buttons to control
                customView = new ExtraButView(CrashActivity.this);
                customView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        float x, y;
                        x = acceptView.getLeft();
                        float otherwidth = GlobalVars.dp2px(CrashActivity.this, othersize);
                        if(acceptView.getTop() > layoutContent.getHeight() / 2)
                            y = acceptView.getTop() - (otherwidth + customView.getHeight());
                        else
                            y = acceptView.getTop() + (otherwidth + customView.getHeight());
                        RelativeLayout.LayoutParams cusParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        cusParams.leftMargin = (int) x;
                        cusParams.topMargin = (int) y;
                        customView.setLayoutParams(cusParams);

                        // consider title
                        String title = "";
                        if(mineView.energyQuality >= acceptView.energyQuality) {
                            title = getString(R.string.consider_disciple);
                        } else {
                            title = getString(R.string.consider_teacher);
                        }
                        customView.setConsiderTitle(title);

                        layoutContent.addView(customView);

                        // 두상카드를 눌렀을 때의 사건처리 진행
                    }
                }, 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        acceptView.startAnimation(anim);
    }

    public void showCrashSettingDailog () {
        CrashSettingDialog dialog = new CrashSettingDialog(this);
        dialog.setData(this.otherSex, this.otherArea);
        dialog.setTitle(getString(R.string.time_setting_dialog_title));
        AlertDialog dlg = dialog.create();
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 확인 단추를 누를 때의 처리 진행
                Dialog dlg = Dialog.class.cast(dialog);
                RadioGroup genderGroup = (RadioGroup) dlg.findViewById(R.id.genderGroup);
                RadioGroup areaGroup = (RadioGroup) dlg.findViewById(R.id.areaGroup);
                otherSex = genderGroup.indexOfChild(genderGroup.findViewById(genderGroup.getCheckedRadioButtonId()));
                otherArea = areaGroup.indexOfChild(areaGroup.findViewById(areaGroup.getCheckedRadioButtonId()));

                getCrashLst();
            }
        });
        dlg.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소단추를 누를 때의 처리 진행
            }
        });
        dlg.getWindow().setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT);
        dlg.show();

    }

    public float getDistance (Point firstPos, Point secondPos) {
        float result = (float) Math.sqrt(Math.abs((firstPos.x - secondPos.x) * (firstPos.x - secondPos.x) + (firstPos.y - secondPos.y) * (firstPos.y - secondPos.y)));
        return result;
    }

    @Override
    public boolean onTouch(final View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        if(isAccepted)
            return true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                for(int i = 0; i < othersArray.size(); i ++) {
                    CrashItemView temp = othersArray.get(i);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) temp.getLayoutParams();
                    int[] sizes = new int[2];
                    view.getLocationOnScreen(sizes);
                    float distance = getDistance(new Point(params.leftMargin + (int)GlobalVars.dp2px(this, othersize / 2), params.topMargin + (int)GlobalVars.dp2px(this, othersize / 2)),
                            new Point(layoutParams.leftMargin + (int)GlobalVars.dp2px(this, minesize / 2), layoutParams.topMargin + (int)GlobalVars.dp2px(this, minesize / 2)));
                    float limit = GlobalVars.dp2px(this, (minesize + othersize) / 2);
                    if(distance - 1 < limit) {
                        // 튀여나기
                        if(isAnim)
                            return true;
                        isAnim = true;
                        final float xDelta = (oldPos.x - layoutParams.leftMargin) * 2;
                        final float yDelta = (oldPos.y - layoutParams.topMargin) * 2;

                        TranslateAnimation inAnim = new TranslateAnimation(0.f, xDelta, 0.f, yDelta);
                        inAnim.setDuration(ANIM_OUT_TIME);
                        inAnim.setRepeatCount(0);
                        inAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isAnim = true;
                                oldPrams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                oldPrams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                view.setLayoutParams(oldPrams);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                oldPrams.leftMargin = (int) (oldPrams.leftMargin + xDelta);
                                oldPrams.topMargin = (int) (oldPrams.topMargin + yDelta);
                                view.setLayoutParams(oldPrams);
                                isAnim = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mineView.startAnimation(inAnim);

                        // 상대방 진동동
                        Animation shake;
                        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_anim);
                        temp.startAnimation(shake);
                        if(temp.isCrashed == 0) {
                            sendCrashRequest(temp.userId);
                            temp.isCrashed = 1;
                            temp.showStatus();
                        }
                        return true;
                    }

               }

                oldPos = new Point(layoutParams.leftMargin, layoutParams.topMargin);
                int leftMargin = X - _xDelta;
                int topMargin = Y - _yDelta;
                LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) layoutContent.getLayoutParams();

                Rect rectangle = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                int left_limit = rectangle.width() - leftMargin;
                int top_limit = (int) (rectangle.height()- topMargin - GlobalVars.dp2px(this, 48));
                if(left_limit <= GlobalVars.dp2px(this, minesize) || top_limit <= GlobalVars.dp2px(this, minesize) || leftMargin <= 0 || topMargin <= 0)
                    return true;
                layoutParams.leftMargin = leftMargin;
                layoutParams.topMargin = topMargin;
                view.setLayoutParams(layoutParams);

                break;
        }
        layoutContent.invalidate();
        return true;
    }

    public void sendCrashRequest(String userID) {
        crashRequestTask = new CrashRequestTask(this, userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(CrashActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res = result.get("req_result").getAsBoolean();
                if(res) {           // success

                } else {            // failure

                }
            }
        });
    }

    //  부딪치기에서 탈퇴하는 경우의 처리 진행
    public void quitCrash () {
        crashQuitTask = new CrashQuitTask(this, SelfInfoModel.userID, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(CrashActivity.this);
                    return;
                }
                JsonObject result = (JsonObject) Response;
                Boolean res = result.get("quit_result").getAsBoolean();
                if(res) {               // success

                } else {                // failure

                }
            }
        });
    }

    // 알림통보를 받았을 때의 처리 진행
    public void addNewItem (JsonObject data) {
        for(int i = 0; i < visibleCrashListData.size(); i ++) {
            JsonObject info = visibleCrashListData.get(i).getAsJsonObject();
            String otherUserID = info.get("user_id").getAsString();
            if(otherUserID.equals(data.get("user_id").getAsString()))
                return;
        }
        visibleCrashListData.add(data);
        generateOtherBeing(false);
    }
    public void receiveRequest (String otherUserID) {
        for(int i = 0; i < visibleCrashListData.size(); i ++) {
            JsonObject item = visibleCrashListData.get(i).getAsJsonObject();
            if(otherUserID.equals(item.get("user_id").getAsString()))
            {
                acceptResult.add(visibleCrashListData.get(i).getAsJsonObject());
                break;
            }
        }
        if(visibleCrashListData.size() == 1)
            acceptAnimation(otherUserID);
    }
    public void crashResult (JsonObject result) {
        try {
            String  otherUserID = result.get("user_id").getAsString();
            Integer resultType = result.get("result").getAsInt();
            for(int i = 0; i < othersArray.size(); i ++) {
                CrashItemView temp = othersArray.get(i);
                if(otherUserID.equals(temp.userId)) {
                    switch (resultType) {
                        case 0:                                     // 부결
                            temp.isCrashed = 0;
                            temp.showStatus();
                            break;
                        case 1:                                     // 스승받아들이기
                        case 2:                                     // 제자받아들이기
                            temp.isCrashed = 2;
                            temp.showStatus();
                            return;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) { }
    }

    // 부딪치기 결과를 돌려준다.
    public void setResultType(Boolean isRefus) {
        JsonObject params = new JsonObject();
        String fromUserID = acceptView.userId;
        int result_type = 0;
        if(mineView.energyQuality >= acceptView.energyQuality)
            result_type = 2;
        else
            result_type = 1;
        if(isRefus)
            result_type = 0;
        params.addProperty("to_userid", SelfInfoModel.userID);
        params.addProperty("from_userid", fromUserID);
        params.addProperty("result", result_type);
        crashResultTask = new CrashResultTask(this, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(CrashActivity.this);
                    return;
                }
                JsonObject result = (JsonObject)Response;
                Boolean res = result.get("result").getAsBoolean();
                if(res) {                   // success

                } else {                    // failure

                }
            }
        });

        // doing any thing after request
        layoutContent.removeView(customView);
        isAccepted = false;
        acceptView.isCrashed = 1;
        acceptView.showStatus();
        acceptResult.remove(0);
        if(acceptResult.size() > 0) {        // 아직 부딪치기요청들오온것이 있는 경우
            JsonObject dic = acceptResult.get(0).getAsJsonObject();
            String otherUserID = dic.get("user_id").getAsString();
            acceptAnimation(otherUserID);
        } else {
            mineView.setTxtStatus("");
        }
    }

}
