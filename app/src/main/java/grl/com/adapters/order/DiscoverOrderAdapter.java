package grl.com.adapters.order;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.order.DiscoverOrderAcceptActivity;
import grl.com.activities.discovery.order.DiscoverOrderSolveActivity;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderEntireModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class DiscoverOrderAdapter extends RecyclerView.Adapter<DiscoverOrderAdapter.MyViewHolder> {

    public List<OrderEntireModel> myList;
    private Activity ctx;
    public DiscoverOrderAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<OrderEntireModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_discover_order_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        OrderEntireModel model = this.myList.get(position);

        holder.orderContentView.setText(model.contentModel.ordContent);
        holder.grlNumberView.setText(model.orderModel.grlNumber);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                if (position == 0)
                    Utils.start_Activity(ctx, DiscoverOrderAcceptActivity.class);
                else
                    Utils.start_Activity(ctx, DiscoverOrderSolveActivity.class);
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
        private final TextView grlNumberView;
        private final TextView stateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            orderContentView = (TextView) itemView.findViewById(R.id.tv_order_content);
            grlNumberView = (TextView) itemView.findViewById(R.id.tv_grl_number);
            stateView = (TextView) itemView.findViewById(R.id.tv_order_state);
        }
    }
}
