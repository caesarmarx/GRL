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

import grl.com.activities.discovery.order.MySolveDetailActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class MySolveAdapter extends RecyclerView.Adapter<MySolveAdapter.MyViewHolder> {

    public List<SolveModel> myList;
    private Activity ctx;
    public MySolveAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<SolveModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_solution_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SolveModel model = this.myList.get(position);

        GlobalVars.loadImage(holder.userPhotoView, model.userPhoto);
        holder.orderContentView.setText(String.format("%s %s", GlobalVars.getDateStringFromLong(model.timeEnd, "yyyy-MM-dd"),model.orderContent));
        holder.orderResultrView.setText(model.orderResult);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                SolveModel model = myList.get(position);
                Utils.start_Activity(ctx, MySolveDetailActivity.class,
                        new BasicNameValuePair("user_id", model.userId),
                        new BasicNameValuePair("content_id",model.contentId),
                        new BasicNameValuePair("ord_type",model.orderType),
                        new BasicNameValuePair("ord_content",model.orderContent),
                        new BasicNameValuePair("order_result",model.orderResult),
                        new BasicNameValuePair("time_start",GlobalVars.getDateStringFromLong(model.timeStart, "yyyy-MM-dd")),
                        new BasicNameValuePair("ord_budget",String.valueOf(model.orderBudget)),
                        new BasicNameValuePair("order_status",String.valueOf(model.orderStatus)));
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
        private final TextView orderResultrView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            orderContentView = (TextView) itemView.findViewById(R.id.tv_order_content);
            orderResultrView = (TextView) itemView.findViewById(R.id.tv_order_result);
        }
    }
}
