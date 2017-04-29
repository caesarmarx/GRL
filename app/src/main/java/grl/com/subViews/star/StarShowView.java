package grl.com.subViews.star;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.UserModel;
import grl.com.subViews.dialogues.PhotoCardDialog;
import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/27/16.
 */

public class StarShowView extends RelativeLayout {

    private ScaleGestureDetector mScaleDetector;

    Activity parent;

    ArrayList<UserModel> userList;
    ArrayList<StarButton> btnList;
    int smallRadius = 0;

    Boolean bFirstShow = true;
    Boolean bCenter = true;

    float maxDistance = 0;
    float minDistance = Float.MAX_VALUE;
    float scale = 1;
    float savedScale = 1;

    float btnWidth = Constant.BIG_STAR_WIDTH;
    float btnHeight = Constant.BIG_STAR_HEIGHT;

    Point contentOffset;
    float screenDistance;
    float screenRate;

//    public HScroll horizontalView;
//    public RelativeLayout layout;

    int nShowType = -1;

    public StarShowView(Context context) {
        super(context);
        parent = (Activity)context;
    }

    public StarShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parent = (Activity)context;
    }

    public void initView() {
        userList = new ArrayList<UserModel>();
        btnList = new ArrayList<StarButton>();
        contentOffset = new Point();

        Display display = parent.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (width >= 1024) {
            btnWidth = Constant.BIG_STAR_WIDTH;
            btnHeight = Constant.BIG_STAR_HEIGHT;
        } else if (width >= 640) {
            btnWidth = Constant.NORMAL_STAR_WIDTH;
            btnHeight = Constant.NORMAL_STAR_HEIGHT;
        } else {
            btnWidth = Constant.SMALL_STAR_WIDTH;
            btnHeight = Constant.SMALL_STAR_HEIGHT;
        }

        scale = 1.0f;
        savedScale = 1.0f;
        bCenter = true;
        maxDistance = 0;
        minDistance = Integer.MAX_VALUE;

        mScaleDetector = new ScaleGestureDetector(parent, new ScaleListener());

    }

    public void goToCenter() {
        scale = 1.0f;
        savedScale = 1.0f;
        bCenter = true;
        maxDistance = 0;
        minDistance = Integer.MAX_VALUE;
        updateUI(false);
    }

    public void refreshData(ArrayList<UserModel> newList, int radius) {
        if (SelfInfoModel.latitude < Double.MIN_NORMAL ||
                SelfInfoModel.longitude < Double.MIN_NORMAL)
            return;

        for (int i = newList.size() - 1; i >= 0; i--) {
            UserModel model = newList.get(i);
            if (model.latitude < Float.MIN_NORMAL || model.longitude < Float.MIN_NORMAL)
                newList.remove(i);
        }

        for (int i = userList.size() - 1; i >= 0; i--) {
            UserModel model = userList.get(i);
            Boolean flag = false;
            for (int j = 0; j < newList.size(); j++) {
                if (model.userID.compareTo(newList.get(j).userID) == 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                userList.remove(i);
                removeView(btnList.get(i));
                btnList.remove(i);
            }
        }

        for (int i = 0; i < newList.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < userList.size(); j++) {
                if (newList.get(i).userID.compareTo(userList.get(j).userID) == 0) {
                    flag = true;
                    userList.set(j, newList.get(i));
                    continue;
                }
            }
            if (flag == false) {
                userList.add(newList.get(i));
                StarButton button = new StarButton(parent);
//                layout.addView(button);
                addView(button);
                final String userId= newList.get(i).userID;


                button.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  // 사진을 눌렀을 때 두상카드를 현시한다.
                                                  Intent intent = new Intent(parent, PhotoCardDialog.class);
                                                  intent.putExtra("user_id", userId);
                                                  intent.putExtra(Constant.BACK_TITLE_ID, "");
                                                  parent.startActivity(intent);
                                              }
                                          });

                btnList.add(button);
            }
        }

        smallRadius = radius;
        updateUI(true);
    }

    public void updateUI(Boolean animate) {

        int maxSize = Math.min(getWidth(), getHeight());

        int maxWidth = maxSize;
        int maxHeight = maxSize;

        float curLatitude = (float)SelfInfoModel.latitude;
        float curLongitude = (float)SelfInfoModel.longitude;

        for (int i = 0; i < userList.size(); i++) {
            UserModel model = userList.get(i);
            float distance = GlobalVars.getDistanceDouble(curLatitude, curLongitude, model.latitude, model.longitude);
            if (minDistance > distance) minDistance = distance;
            if (maxDistance < distance) maxDistance = distance;
        }

        screenDistance = 40 * 1000;
        if (maxDistance < screenDistance)
            screenDistance = maxDistance;

        screenRate = (float)(maxSize + btnWidth)/ screenDistance;

        float contentWidth = maxDistance * screenRate * scale * 2 + 2 * btnWidth + smallRadius;
        float contentHeight = maxDistance * screenRate * scale * 2 + 2 * btnHeight + smallRadius;

        if (!bFirstShow) {
            // 령역이 줄어든 경우

        }

        Point center = new Point();
        center.set((int)(contentWidth / 2), (int)(contentHeight /2));

        if (bCenter) {
            contentOffset.set(center.x - getWidth() / 2, center.y - getHeight() / 2);
            savedScale = scale = 1;
        }

        if (bCenter == false && savedScale != scale && savedScale != 0) {
            float x = (contentOffset.x + getWidth() / 2 - smallRadius) /savedScale * scale;
            float y = (contentOffset.y + getHeight() / 2  - smallRadius)/savedScale * scale;
            contentOffset.x = (int)(x - getWidth() / 2 + smallRadius);
            contentOffset.y = (int)(y - getHeight() / 2 + smallRadius);
        }

        Log.e("Error", "Show View Start");

        for (int i = 0; i < userList.size(); i++) {
            UserModel model = userList.get(i);
            float distance = GlobalVars.getDistanceDouble(curLatitude, curLongitude, model.latitude, model.longitude);
            float offsetLatitude = (float)(model.latitude - curLatitude);
            float offsetLongitude = (float)(model.longitude - curLongitude);

            float angle;

            if (offsetLatitude == 0 || offsetLongitude == 0)
                angle = 0;
            else {
                angle = (float)Math.acos(offsetLongitude / Math.sqrt(offsetLongitude * offsetLongitude + offsetLatitude * offsetLatitude));
                if (offsetLatitude < 0)
                    angle = -angle;
            }

            StarButton button = btnList.get(i);
            if (!button.bInit) {
                button.distance = distance;
                button.latitude = model.latitude;
                button.longitude = model.longitude;
                button.angleParent = angle;
                button.parentRect = new Rect();
                button.parentRect.set(0, 0, getWidth(), getHeight());
            } else {
                if (Math.abs(distance - button.distance) < 10) {
                    angle = button.angleParent;
                    distance = button.distance;
                } else {
                    button.latitude = model.latitude;
                    button.longitude = model.longitude;
                    button.distance = distance;
                    button.angleParent = angle;
                }
            }

            int x = 0, y = 0;
            x = (int)(Math.cos(angle) * distance * maxWidth * scale / screenDistance);
            y = (int)(Math.sin(angle) * distance * maxWidth * scale / screenDistance);
            x += (int)(smallRadius / 2 * Math.cos(angle));
            y += (int)(smallRadius / 2 * Math.sin(angle));

            if (y < 0) {
                y -= btnHeight;
            }

            if (x < 0) {
                x -= btnWidth;
            }

            x += center.x;
            y += center.y;

            Point curCenter = new Point();
            curCenter.set(contentOffset.x + getWidth() / 2, contentOffset.y + getHeight() / 2);

            button.currentOffset = contentOffset;
            button.centerParent = curCenter;
            button.centerView = new Point((int)(x + btnWidth / 2), (int)(y + btnHeight / 2));

            if (button.bInit == false || scale != savedScale || animate == false) {
                button.moveViewPositionNoAnimate(x, y, (int)btnWidth, (int)btnHeight);
            } else {
                button.moveViewPosition(x, y, (int)btnWidth, (int)btnHeight);
            }

            boolean bAlways = false;
            if (nShowType == 0) {
                // 주변 회원인 경우
                if (model.userStatus > 0)
                    bAlways = true;
            } else {
                if (model.orderStatus > 0)
                    bAlways = true;
            }
            button.setbAlways(bAlways);
            button.bInit = true;
        }

        savedScale = scale;
        bCenter = false;
        bFirstShow = false;
    }

    private void updateButtonUI() {

        Point center = new Point();
        center.set((int)(screenRate * maxDistance * scale + btnWidth + smallRadius / 2),
                (int)(screenRate * maxDistance * scale + btnHeight + smallRadius / 2));

        for (int i = 0; i < userList.size(); i++) {
            UserModel model = userList.get(i);

            StarButton button = btnList.get(i);

            Point curCenter = new Point();
            curCenter.set(contentOffset.x + getWidth() / 2, contentOffset.y + getHeight() / 2);

            button.currentOffset = contentOffset;
            button.centerParent = curCenter;

            int x= (int)(button.centerView.x - btnWidth / 2);
            int y= (int)(button.centerView.y - btnHeight / 2);

            boolean bAlways = false;
            if (nShowType == 0) {
                // 주변 회원인 경우
                if (model.userStatus > 0)
                    bAlways = true;
            } else {
                if (model.orderStatus > 0)
                    bAlways = true;
            }

            button.moveViewPositionNoAnimate(x, y, (int)btnWidth, (int)btnHeight);
            button.setbAlways(bAlways);
        }

    }
    public void scrollToPoint(int x, int y) {
//        scrollTo(0, y);
//        horizontalView.scrollTo(x, 0);
    }

    public void scrollByPosition(int x, int y) {
//        scrollBy(x, y);
//        horizontalView.scrollBy(x, y);
        contentOffset.x += x;
        contentOffset.y += y;
        updateButtonUI();
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            Log.d("Expand", String.format("%f", scale));
            if (scale < 0.5) scale = 1;
            if (scale > 20) scale = 20;
            updateUI(false);
            return true;
        }
    }

    float mx, my;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curX, curY;
        mScaleDetector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mx = event.getX();
                my = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                scrollByPosition((int) (mx - curX), (int) (my - curY));
                mx = curX;
                my = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                scrollByPosition((int) (mx - curX), (int) (my - curY));
                break;
        }

        return true;
    }

    public int getnShowType() {
        return nShowType;
    }

    public void setnShowType(int nShowType) {
        if (this.nShowType != nShowType) {
            this.nShowType = nShowType;
            goToCenter();
            return;
        }

    }
}
