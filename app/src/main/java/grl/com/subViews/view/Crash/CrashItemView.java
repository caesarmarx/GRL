package grl.com.subViews.view.Crash;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.makeramen.roundedimageview.RoundedImageView;

import grl.com.App;
import grl.com.activities.discovery.crash.CrashActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.subViews.dialogues.PhotoCardDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/29/2016.
 */
public class CrashItemView extends RelativeLayout implements View.OnClickListener{

    // view
    RoundedImageView imgUserPhoto;
    ImageView imgStatus;
    TextView tvStatus;
    Activity context;

    // require
    public String userId;
    public String userPhoto;
    public int energyQuality;
    // value
    Boolean isYourself;
    public int isCrashed;

    public CrashItemView(Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public CrashItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    public CrashItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = (Activity) context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_crash_item, this);

        this.imgUserPhoto = (RoundedImageView) findViewById(R.id.imgUserPhoto);
        this.imgStatus = (ImageView) findViewById(R.id.imgStatus);
        this.tvStatus = (TextView) findViewById(R.id.tvStatus);
    }

    public void setTxtStatus (String status) {
        tvStatus.setText(status);
    }
    public void initView(Boolean isYourself) {
        this.isYourself = isYourself;
        if(isYourself) {                    // 자기자신을 나타낼 때
            setYourSelfView();
        } else {                            // 상대방을 나타낼 때
            setOtherView();
        }
    }

    public void setYourSelfView () {
        int size = (int) GlobalVars.dp2px(App.getInstance(), CrashActivity.minesize);
        this.setLayoutParams(new LinearLayout.LayoutParams(size, size));

        RelativeLayout.LayoutParams params = (LayoutParams) imgUserPhoto.getLayoutParams();
        params.width = size;
        params.height = params.width;
        imgUserPhoto.setLayoutParams(params);
        imgUserPhoto.setBorderColor(getResources().getColor(R.color.yellowBack));

        tvStatus.setText("");
        imgStatus.setVisibility(View.GONE);

    }


    public void showStatus () {
        switch (isCrashed) {
            case 0:                             // 기정
                imgUserPhoto.setBorderColor(getResources().getColor(R.color.crash_status_normal));
                imgStatus.setVisibility(View.GONE);
                break;
            case 1:                             // 이미요청진행
                imgUserPhoto.setBorderColor(getResources().getColor(R.color.crash_status_waiting));
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.crash_wait_icon));
                break;
            case 2:                             // 이미등록
                imgUserPhoto.setBorderColor(getResources().getColor(R.color.yellowBack));
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.crash_accept_icon));
                break;
        }

        if(isCrashed != 1) {
//            RelativeLayout.LayoutParams params = (LayoutParams) imgStatus.getLayoutParams();
//            params.addRule(ALIGN_PARENT_BOTTOM);
//            params.addRule(ALIGN_PARENT_LEFT);
//            params.width = (int) GlobalVars.dp2px(App.getInstance(), 68);
//            params.height = (int) GlobalVars.dp2px(App.getInstance(), 18);
//            params.bottomMargin = (int) GlobalVars.dp2px(App.getInstance(),8);
//            imgStatus.setLayoutParams(params);
        }

        invalidate();
    }
    public void setOtherView () {
        int h = (int) GlobalVars.dp2px(App.getInstance(), CrashActivity.othersize);
        int w = (int) GlobalVars.dp2px(App.getInstance(), 68);
        this.setLayoutParams(new RelativeLayout.LayoutParams(w, h));

        RelativeLayout.LayoutParams params = (LayoutParams) imgUserPhoto.getLayoutParams();
        params.width = h;
        params.height = params.width;
        imgUserPhoto.setLayoutParams(params);
        imgUserPhoto.setBorderColor(getResources().getColor(R.color.crash_status_normal));

        tvStatus.setVisibility(View.GONE);
        imgStatus.setVisibility(View.GONE);
    }

    public void setUserPhoto (String userPhoto) {
        if(userPhoto.equals("")) {
            imgUserPhoto.setImageDrawable(getResources().getDrawable(R.drawable.user_default));
        } else {
            GlobalVars.loadImage(imgUserPhoto, userPhoto);
        }
    }

    public void showAnim (long duration, long delay) {
        AlphaAnimation animation = new AlphaAnimation(0.0f , 1.0f);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(false);
        this.startAnimation(animation);
    }

    public void setData (JsonObject data) {
        this.userId = data.get("user_id").getAsString();
        this.userPhoto = data.get("user_photo").getAsString();
        this.energyQuality = data.get("energy_quality").getAsInt();
        isYourself  = false;
        initView(false);
        setUserPhoto(userPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgUserPhoto:                   // 화상을 누른경우 두상카드를 현시한다.
                if(!userId.equals(SelfInfoModel.userID))
                    Utils.start_Activity(context, PhotoCardDialog.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.crash_title)),
                        new BasicNameValuePair("user_id", userId));
                break;
        }
    }
}