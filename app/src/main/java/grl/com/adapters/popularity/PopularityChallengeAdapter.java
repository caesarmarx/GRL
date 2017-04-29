package grl.com.adapters.popularity;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.order.SolutionSelectActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ChallengeModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class PopularityChallengeAdapter extends RecyclerView.Adapter<PopularityChallengeAdapter.MyViewHolder> {

    public List<ChallengeModel> myList;
    private Activity ctx;
    private OnItemOnClickListener mItemOnClickListener;

    private int contentStatus;

    public PopularityChallengeAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ChallengeModel>();
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

        ChallengeModel model = this.myList.get(position);
//        holder.textView.setText(model.orderResult);
        holder.textView.setText(model.fromUserName + "老门 : " + model.toUserName + "老门");
        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mItemOnClickListener != null)
                    mItemOnClickListener.onChallengeClick(position);
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

    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnClickListener {
        public void onChallengeClick(int position);
    }
}
