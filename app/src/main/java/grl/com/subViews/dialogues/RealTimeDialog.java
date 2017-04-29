package grl.com.subViews.dialogues;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;

import grl.com.activities.discovery.order.DiscoverOrderAcceptActivity;
import grl.com.activities.discovery.order.DiscoverOrderSolveActivity;
import grl.com.activities.order.RealTimeOrderActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.configuratoin.ViewHolder;
import grl.com.dataModels.BackgroundOrderModel;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.wangu.com.grl.R;

/**
 * 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class RealTimeDialog extends PopupWindow {
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

	// 定义列表对象
	private ListView mListView;

	// 定义弹窗子类项列表
	private ArrayList<OrderEntireModel> mListItems = new ArrayList<OrderEntireModel>();

	public RealTimeDialog(Context context) {
		// 设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public RealTimeDialog(Context context, int width, int height) {
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
				R.layout.layout_menu_realtime, null));
		setAnimationStyle(R.style.AnimCenterHead);
		initUI();
	}

	public RealTimeDialog(Context context, int width, int height, Boolean left) {
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
				R.layout.layout_menu_realtime, null));

		setAnimationStyle(R.style.AnimHead);
		initUI();
	}
	/**
	 * 初始化弹窗列表
	 */
	private void initUI() {

		mListView = (ListView) getContentView().findViewById(R.id.title_list);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// 点击子类项后，弹窗消失
				dismiss();

//				OrderEntireModel model = mListItems.get(index);
				mItemOnClickListener.onItemClick(index);
			}
		});
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view) {
		// 获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);

		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),
				mLocation[1] + view.getHeight());

		// 判断是否需要添加或更新列表子类项
		if (mIsDirty) {
			populateActions();
		}
		showAtLocation(view, popupGravity, mRect.centerX() - getWidth() / 2, mRect.bottom);

	}

	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions() {
		mIsDirty = false;

		// 设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.layout_realtime_row, parent, false);
				}
				TextView nameView = ViewHolder.get(convertView, R.id.tv_user_name);
				ImageView photoView = ViewHolder.get(convertView, R.id.img_user_photo);
				TextView stateView = ViewHolder.get(convertView, R.id.tv_user_state);
				View circleView = ViewHolder.get(convertView, R.id.iv_circle_red);

				OrderEntireModel model = mListItems.get(position);
				nameView.setText(GlobalVars.getOrderString(model.contentModel.ordType, model.contentModel.ordContent));
				GlobalVars.loadImage(photoView, model.userModel.userPhoto);
				stateView.setText(model.userModel.state);

				circleView.setVisibility(View.INVISIBLE);
				for (int i = 0;i < GlobalVars.bkOrderList.size(); i++) {
					BackgroundOrderModel backModel = GlobalVars.bkOrderList.get(i);
					if (backModel.orderId.compareTo(model.orderModel.orderId) == 0) {
						circleView.setVisibility(View.VISIBLE);
						break;
					}

				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mListItems.get(position);
			}

			@Override
			public int getCount() {
				return mListItems.size();
			}
		});
	}

	public void setList(ArrayList<OrderEntireModel> list) {
		mListItems = list;
		mIsDirty = true;
	}
	/**
	 * 添加子类项
	 */
	public void addModel(OrderEntireModel model) {
		if (model != null) {
			mListItems.add(model);
			mIsDirty = true;
		}
	}

	/**
	 * 清除子类项
	 */
	public void clearModel() {
		if (mListItems.isEmpty()) {
			mListItems.clear();
			mIsDirty = true;
		}
	}

	/**
	 * 根据位置得到子类项
	 */
	public OrderEntireModel getAction(int position) {
		if (position < 0 || position > mListItems.size())
			return null;
		return mListItems.get(position);
	}

	/**
	 * 设置监听事件
	 */
	public void setItemOnClickListener(
			OnItemOnClickListener onItemOnClickListener) {
		this.mItemOnClickListener = onItemOnClickListener;
	}

	/**
	 * @author yangyu 功能描述：弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener {
		public void onItemClick(int position);
	}
}