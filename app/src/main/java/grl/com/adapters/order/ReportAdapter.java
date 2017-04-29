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

import org.w3c.dom.Text;

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

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    public List<String> myList;
    private Activity ctx;
    public ReportAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<String>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_report_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String reason = this.myList.get(position);
        holder.ressovView.setText(reason);
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

        private final TextView ressovView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ressovView = (TextView) itemView.findViewById(R.id.tv_report_view);
        }
    }
}
