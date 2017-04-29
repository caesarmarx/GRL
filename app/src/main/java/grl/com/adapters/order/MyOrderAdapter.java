package grl.com.adapters.order;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.order.MyOrderDetailActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    String userId;

    public List<OrderModel> myList;
    private Activity ctx;
    private OnItemOnClickListener mItemOnClickListener;

    public MyOrderAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<OrderModel>();
    }

    public void setUserId(String str) {
        userId = str;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_order_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        OrderModel model = this.myList.get(position);

        holder.orderContentView.setText(model.orderContent);
        holder.orderDateView.setText(GlobalVars.getDateStringFromLong(model.timeStart, "yyyy-MM-dd"));
        holder.orderBudgetView.setText(String.format("%s : %d", ctx.getResources().getString(R.string.order_budget),model.orderBudget));

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                mItemOnClickListener.onItemClick(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        if (myList == null)
            return 0;
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView orderContentView;
        private final TextView orderDateView;
        private final TextView orderBudgetView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            orderContentView = (TextView) itemView.findViewById(R.id.tv_order_content);
            orderDateView = (TextView) itemView.findViewById(R.id.tv_order_date);
            orderBudgetView = (TextView) itemView.findViewById(R.id.tv_order_budget);
        }
    }

    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnClickListener {
        public void onItemClick(int position);
    }
}
