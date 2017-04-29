package grl.com.adapters.energy;

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
import grl.com.dataModels.ChallengeContributeModel;
import grl.com.dataModels.OrderModel;
import grl.com.dataModels.SolveModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ChallengeMemberAdapter extends RecyclerView.Adapter<ChallengeMemberAdapter.MyViewHolder> {

    public List<ChallengeContributeModel> myList;
    private Activity ctx;

    private int contentStatus;

    public ChallengeMemberAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ChallengeContributeModel>();
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

        ChallengeContributeModel model = this.myList.get(position);
        holder.textView.setText(String.format("%s : %d", model.userName, model.contribute));

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
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
