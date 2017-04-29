package grl.com.subViews.star;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.util.Size;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/27/16.
 */

public class StarButton extends ImageView {

    private Context parent;
    public boolean bInit = false;
    public float latitude;
    public float longitude;
    public float distance;
    public Rect parentRect;
    float angleParent;

    Point currentOffset;
    Point centerParent;
    Point centerView;
    RelativeLayout.LayoutParams btnLayout = null;
    int btnType;
    boolean bAlways = false;

    public boolean bNotShown = false;

    Point oldPoint;

    public StarButton(Context context) {
        super(context);
        parent = context;
        oldPoint = new Point();
//        setScaleType(ScaleType.FIT_XY);
        setBackgroundResource(R.drawable.star_btn_normal);
    }


    public void moveViewPositionNoAnimate(int x, int y, int width, int height) {
        setPosition(x, y, width, height);
        if (oldPoint.x == x && oldPoint.y == y)
            return;
        oldPoint.x = x;
        oldPoint.y = y;
    }

    public void moveViewPosition(int x, int y, int width, int height) {

        if (oldPoint.x == x && oldPoint.y == y) {
            setPosition(x, y, width, height);
            return;
        }
        oldPoint.x = x;
        oldPoint.y = y;

        setPosition(x, y, width, height);

//        TranslateAnimation animation = new TranslateAnimation(btnLayout.leftMargin, btnLayout.topMargin, x - currentOffset.x, y - currentOffset.y);
//        animation.setDuration(5000);
//        animation.setFillAfter(false);
//        animation.setAnimationListener(new MoveAnimationListener());
//
//        startAnimation(animation);
    }

    private class MoveAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationEnd(Animation animation) {
            setPosition(oldPoint.x, oldPoint.y, getWidth(), getHeight());
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }

    private void setPosition(int x, int y, int width, int height) {

        int newX = x - currentOffset.x; int newY = y - currentOffset.y;

        Point center = new Point(parentRect.width() / 2,  parentRect.height() / 2);

        if ((newX < 0 || newX > (parentRect.width())) ||
                (newY < 0 || newY > parentRect.height())) {
            // 보임령역을 벗어나는 경우

            double offsetX = center.x - newX;
            double offsetY = center.y - newY;
            double scaleBtnX = parentRect.width() / 2 / Math.abs(offsetX);
            double scaleBtnY = parentRect.height() / 2 / Math.abs(offsetY);
            double scaleBtn = Math.min(scaleBtnX, scaleBtnY);
            bNotShown = true;

            newX = (int)(center.x + (newX - center.x) * scaleBtn);
            newY = (int)(center.y + (newY - center.y) * scaleBtn);
        } else {
            bNotShown = false;
        }

        if (btnLayout == null || newX != btnLayout.leftMargin || newY != btnLayout.topMargin) {
            btnLayout = new RelativeLayout.LayoutParams(width, height);

            if (newX > parentRect.width() - width) {
                btnLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                btnLayout.rightMargin = parentRect.width() - newX - width / 2;
            } else {
                btnLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                btnLayout.leftMargin = newX - width / 2;
            }
            if (newY > parentRect.height() - height) {
                btnLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                btnLayout.bottomMargin = parentRect.height() - newY - height / 2;
            } else {
                btnLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                btnLayout.topMargin = newY - height / 2;
            }

            setLayoutParams(btnLayout);
        }
    }

    public boolean isbAlways() {
        return bAlways;
    }

    public void setbAlways(boolean bAlways) {
        if (this.bAlways != bAlways) {
            this.bAlways = bAlways;
            if (bAlways)
                setBackgroundResource(R.drawable.star_btn_always);
            else
                setBackgroundResource(R.drawable.star_btn_normal);
        }
    }
}
