package grl.com.adapters.order;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.order.DiscoverOrderAcceptActivity;
import grl.com.activities.discovery.order.DiscoverOrderSolveActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderEntireModel;
import grl.com.dataModels.OrderModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class TGroupOrderAdapter extends RecyclerView.Adapter<TGroupOrderAdapter.MyViewHolder> {

    public List<OrderEntireModel> myList;
    private Activity ctx;
    public TGroupOrderAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<OrderEntireModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        OrderEntireModel model = this.myList.get(position);
        holder.userNameView.setText(model.userModel.userName);
        GlobalVars.loadImage(holder.userPhotoView, model.userModel.userPhoto);
        holder.orderContentView.setText(model.contentModel.ordContent);
        holder.grlNumberView.setText(GlobalVars.getDateStringFromLong(model.contentModel.timeStart, "yyyy-MM-dd"));
        if (model.orderModel.orderStatus == OrderModel.ORDER_INIT_STATE) {
            holder.acceptBtn.setVisibility(View.VISIBLE);
            holder.acceptedBtn.setVisibility(View.GONE);
        } else {
            holder.acceptBtn.setVisibility(View.GONE);
            holder.acceptedBtn.setVisibility(View.VISIBLE);
        }
        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                OrderEntireModel model = myList.get(position);
                if (model.orderModel.orderStatus == OrderModel.ORDER_INIT_STATE) {
                    Utils.start_Activity(ctx, DiscoverOrderAcceptActivity.class,
                            new BasicNameValuePair("user_id", model.userModel.userID),
                            new BasicNameValuePair("order_id",model.orderModel.orderId),
                            new BasicNameValuePair("bTGroupOrder",String.valueOf(true)));
                } else {
                    Utils.start_Activity(ctx, DiscoverOrderSolveActivity.class,
                            new BasicNameValuePair("user_id", model.userModel.userID),
                            new BasicNameValuePair("order_id",model.orderModel.orderId),
                            new BasicNameValuePair("bTGroupOrder",String.valueOf(true)));
                }

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
        private final TextView userNameView;
        private final TextView orderContentView;
        private final TextView grlNumberView;
        private final Button acceptBtn;
        private final Button acceptedBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            orderContentView = (TextView) itemView.findViewById(R.id.tv_order_content);
            grlNumberView = (TextView) itemView.findViewById(R.id.tv_grl_number);
            acceptBtn = (Button) itemView.findViewById(R.id.btn_order_accept);
            acceptedBtn = (Button) itemView.findViewById(R.id.btn_accept_already);
        }
    }
}
