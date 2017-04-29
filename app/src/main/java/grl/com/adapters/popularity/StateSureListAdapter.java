package grl.com.adapters.popularity;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import grl.com.activities.discovery.popularity.PopularStateActivity;
import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.UserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class StateSureListAdapter extends RecyclerView.Adapter<StateSureListAdapter.MyViewHolder> {

    public List<JSONObject> myList;
    private PopularStateActivity ctx;
    private boolean bNumberShow = false;
    private OnItemOnClickListener mItemOnClickListener;

    public StateSureListAdapter(PopularStateActivity context) {
        this.ctx = context;
        this.myList = new ArrayList<JSONObject>();
    }

    public void showNumber() {
        bNumberShow = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        JSONObject itemModel = this.myList.get(position);

        holder.numberView.setText(String.format("%d", position + 1));
        holder.numberView.setVisibility(View.GONE);

        holder.userNameView.setText(GlobalVars.getStringFromJson(itemModel, "user_name"));
        GlobalVars.loadImage(holder.userPhotoView, GlobalVars.getStringFromJson(itemModel, "user_photo"));


        // set onClickListener
        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mItemOnClickListener != null)
                    mItemOnClickListener.onSureClick(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView numberView;
        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final TextView userStateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            numberView = (TextView) itemView.findViewById(R.id.tv_number);
            userStateView = (TextView) itemView.findViewById(R.id.tv_user_state);
        }
    }

    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnClickListener {
        public void onSureClick(int position);
    }
}
