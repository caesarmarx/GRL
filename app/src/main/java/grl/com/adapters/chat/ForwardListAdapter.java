package grl.com.adapters.chat;

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

import grl.com.activities.consult.ChatActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ConsultUserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ForwardListAdapter extends RecyclerView.Adapter<ForwardListAdapter.MyViewHolder> {

    public List<ConsultUserModel> myList;
    private Activity ctx;
    private OnItemClickListener mItemClickListener;

    public ForwardListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ConsultUserModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        int index = position;
        ConsultUserModel itemModel = this.myList.get(index);
        holder.userNameView.setText(itemModel.userName);
        GlobalVars.loadImage(holder.userPhotoView, itemModel.userPhoto);

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                mItemClickListener.onForwardClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onForwardClick(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView userNameView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
        }
    }


    public void onItemClick(int position) {
        if (mItemClickListener == null)
            return;
        mItemClickListener.onForwardClick(position);
    }

    public void setItemOnClickListener(
            OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        public void onForwardClick(int position);
    }
}
