package grl.com.adapters.order;

import android.app.Activity;
import android.graphics.Color;
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
import grl.com.activities.discovery.order.SolutionSelectActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class MyOrderSolveAdapter extends RecyclerView.Adapter<MyOrderSolveAdapter.MyViewHolder> {

    public List<SolveModel> myList;
    private Activity ctx;

    private int contentStatus;

    public MyOrderSolveAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<SolveModel>();
    }

    public void setContentStatus(int status) {
        contentStatus = status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_text_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SolveModel model = this.myList.get(position);
        holder.textView.setText(model.orderResult);
        if (model.orderStatus == OrderModel.ORDER_SELECT_STATE) {
            holder.textView.setTextColor(Color.RED);
        }

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                SolveModel model = myList.get(position);
                Utils.start_Activity(ctx, SolutionSelectActivity.class,
                        new BasicNameValuePair("user_id", model.userId),
                        new BasicNameValuePair("user_name", model.userName),
                        new BasicNameValuePair("user_photo", model.userPhoto),
                        new BasicNameValuePair("content_id",model.contentId),
                        new BasicNameValuePair("content_status",String.valueOf(contentStatus)),
                        new BasicNameValuePair("order_id",model.orderId),
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
        private final TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }
}
