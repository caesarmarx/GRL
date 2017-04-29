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
import grl.com.dataModels.EmoticonModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class EmoticonAdapter extends RecyclerView.Adapter<EmoticonAdapter.MyViewHolder> {

    public List<EmoticonModel> myList;
    private Activity ctx;
    private OnItemClickListener mItemClickListener;

    Boolean bEstimate = false;

    public EmoticonAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<EmoticonModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_expression, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        EmoticonModel model = this.myList.get(position);
        holder.imageView.setImageResource(model.resId);
        if (GlobalVars.isGiftEmoticon(model.emoticonName) >= 0 && bEstimate == false ) {
            holder.imageView.setAlpha(0.5f);
        } else {
            holder.imageView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    // 해당 대화창으로 이행
                    if (mItemClickListener != null)
                        mItemClickListener.onEmoticonClick(position);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_expression);
        }
    }

    public void onItemClick(int position) {
        if (mItemClickListener == null)
            return;
        mItemClickListener.onEmoticonClick(position);
    }

    public void setItemOnClickListener(
            OnItemClickListener onItemOnClickListener) {
        this.mItemClickListener = onItemOnClickListener;
    }

    public static interface OnItemClickListener {
        public void onEmoticonClick(int position);
    }

    // Estimate Enable / Disable
    public void enableEstimate() {
        bEstimate = true;
    }
    public void disableEstimate() {
        bEstimate = false;
    }
}
