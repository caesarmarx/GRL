package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.ChallengeModel;
import grl.com.dataModels.UserModel;
import grl.com.subViews.fragment.prepare.Fragment_enter;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ChallengeNotifyAdapter extends RecyclerView.Adapter<ChallengeNotifyAdapter.MyViewHolder> {

    public List<ChallengeModel> myList;
    private Activity ctx;
    private boolean bNumberShow = false;
    public ChallengeNotifyAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ChallengeModel>();
    }

    public void showNumber() {
        bNumberShow = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_notify_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ChallengeModel model = this.myList.get(position);

        String startDate = GlobalVars.getDateStringFromLong(model.startDate, "yyyy-MM-dd");
        String endDate = GlobalVars.getDateStringFromLong(model.endDate, "yyyy-MM-dd");

        holder.dateView.setText(String.format("%s - %s", startDate, endDate));
        holder.contentView.setText(String.format("%s:%s", model.fromUserName, model.toUserName));

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
            //

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;
        private final TextView contentView;

        public MyViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.tv_date);
            contentView = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
