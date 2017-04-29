package grl.com.subViews.dialogues;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;

import grl.com.activities.discovery.order.DiscoverOrderAcceptActivity;
import grl.com.activities.discovery.order.DiscoverOrderSolveActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.ViewHolder;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.wangu.com.grl.R;

/**
 * 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class PopularityEstDialog extends PopupWindow implements View.OnClickListener{
	private Context mContext;

	private Boolean bLeft;
	// 列表弹窗的间隔
	protected final int LIST_PADDING = 10;

	// 实例化一个矩形
	private Rect mRect = new Rect();

	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];

	// 屏幕的宽度和高度
	private int mScreenWidth, mScreenHeight;

	// 判断是否需要添加或更新列表子类项
	private boolean mIsDirty;

	// 位置不在中心
	private int popupGravity = Gravity.NO_GRAVITY;

	// 弹窗子类项选中时的监听
	private OnItemOnClickListener mItemOnClickListener;

	RelativeLayout mPraiseLayout;
	RelativeLayout mEstimateLayout;

	public PopularityEstDialog(Context context) {
		// 设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public PopularityEstDialog(Context context, int width, int height) {
		this.mContext = context;

		// 设置可以获得焦点
		setFocusable(true);
		// 设置弹窗内可点击
		setTouchable(true);
		// 设置弹窗外可点击
		setOutsideTouchable(true);

		// 获得屏幕的宽度和高度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();

		bLeft = false;
		// 设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

		setBackgroundDrawable(new BitmapDrawable());

		// 设置弹窗的布局界面
		setContentView(LayoutInflater.from(mContext).inflate(
				R.layout.layout_menu_popularity, null));
		setAnimationStyle(R.style.AnimLeft);
		initUI();
	}

	private void initUI() {
		mPraiseLayout = (RelativeLayout) getContentView().findViewById(R.id.fl_praise_layout);
		mEstimateLayout = (RelativeLayout) getContentView().findViewById(R.id.fl_estimate_layout);
		mPraiseLayout.setOnClickListener(this);
		mEstimateLayout.setOnClickListener(this);
	}

	public PopularityEstDialog(Context context, int width, int height, Boolean left) {
		this.mContext = context;

		// 设置可以获得焦点
		setFocusable(true);
		// 设置弹窗内可点击
		setTouchable(true);
		// 设置弹窗外可点击
		setOutsideTouchable(true);

		// 获得屏幕的宽度和高度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();

		// 设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

		bLeft = left;

		setBackgroundDrawable(new BitmapDrawable());


		setContentView(LayoutInflater.from(mContext).inflate(
				R.layout.layout_menu_popularity, null));

		setAnimationStyle(R.style.AnimLeft);

	}


	/**
	 * 显示弹窗列表界面
	 */
	public void show(final View view) {
		// 获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);

		//		showAtLocation(view, popupGravity, mLocation[0] - getWidth() - 70, mLocation[1]);
		View pupLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_menu_popularity, null);
		pupLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		int width = pupLayout.getMeasuredWidth();
		showAtLocation(view, Gravity.NO_GRAVITY, mScreenWidth - width - view.getWidth() - 10, mLocation[1]);

	}

	@Override
	public void onClick(View v) {
		if (mItemOnClickListener == null)
			return;
		switch (v.getId()) {
			case R.id.fl_estimate_layout:
				mItemOnClickListener.onItemClick(R.id.fl_estimate_layout);
				break;
			case R.id.fl_praise_layout:
				mItemOnClickListener.onItemClick(R.id.fl_praise_layout);
				break;
		}
		dismiss();
	}

	public void setItemOnClickListener(
			OnItemOnClickListener onItemOnClickListener) {
		this.mItemOnClickListener = onItemOnClickListener;
	}

	public static interface OnItemOnClickListener {
		public void onItemClick(int resId);
	}

}