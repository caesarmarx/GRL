package grl.com.subViews.star;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.dataModels.UserModel;
import grl.com.subViews.dialogues.PhotoCardDialog;

/**
 * Created by macdev001 on 6/27/16.
 */

public class PlanetShowView extends RelativeLayout {

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

    public HorizontalScrollView horizontalView;
    public RelativeLayout layout;

    int nShowType = 0;

    public PlanetShowView(Context context) {
        super(context);
        parent = (Activity)context;
//        initView(context);
    }

    public PlanetShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parent = (Activity)context;
//        initView(context);
    }

    public void initView() {
        userList = new ArrayList<UserModel>();
        btnList = new ArrayList<StarButton>();

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


    }

    public void refreshData(ArrayList<UserModel> newList) {

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
                btnList.remove(i);
            }
        }

        for (int i = 0; i < newList.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < userList.size(); j++) {
                if (newList.get(i).userID.compareTo(userList.get(j).userID) == 0) {
                    flag = true;
                    userList.set(j, newList.get(i));
                }
            }
            if (flag == false) {
                userList.add(newList.get(i));
                StarButton button = new StarButton(parent);
                addView(button);
                final String userId= newList.get(i).userID;


                button.setOnClickListener(new OnClickListener() {
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

        updateUI();
    }

    public void updateUI() {
        int width = getWidth();
        int height = getHeight();

        ArrayList<Point> pointList = new ArrayList<Point>();
        pointList.add(new Point((int)(1.5 * width), (int)(1.4 * height)));
        pointList.add(new Point((int)(1.3 * width), (int)(0.7 * height)));
        pointList.add(new Point((int)(0.3 * width), (int)(0.3 * height)));
        pointList.add(new Point((int)(1.5 * width), (int)(0.5 * height)));
        pointList.add(new Point((int)(1.4 * width), (int)(0.4 * height)));
        pointList.add(new Point((int)(1.8 * width), (int)(1.7 * height)));
        pointList.add(new Point((int)(0.4 * width), (int)(1.2 * height)));
        pointList.add(new Point((int)(1.3 * width), (int)(1.3 * height)));
        pointList.add(new Point((int)(0.6 * width), (int)(0.5 * height)));

        for (int i = 0 ; i < Math.min(userList.size(), pointList.size()); i++) {
            StarButton button = btnList.get(i);
            button.moveViewPositionNoAnimate((int)pointList.get(i).x, (int)pointList.get(i).y, (int)btnWidth, (int)btnHeight);
            GlobalVars.loadImage(button, userList.get(i).userPhoto);
        }
    }

}
