package grl.com.adapters.order;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.order.SolutionSelectActivity;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.SolveModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

// 我发的令 - 他的锦囊

public class OtherSolveAdapter extends RecyclerView.Adapter<OtherSolveAdapter.MyViewHolder> {

    public List<SolveModel> myList;
    private Activity ctx;
    public OtherSolveAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<SolveModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_my_order_solve_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        SolveModel model = this.myList.get(position);

        holder.orderResultView.setText(model.orderResult);
//        holder.orderStateView.setText(model.timeEnd);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                Utils.start_Activity(ctx, SolutionSelectActivity.class);
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

        private final TextView orderResultView;
        private final TextView orderStateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            orderResultView = (TextView) itemView.findViewById(R.id.tv_order_result);
            orderStateView = (TextView) itemView.findViewById(R.id.tv_order_state);
        }
    }
}
