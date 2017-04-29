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
import grl.com.activities.consult.NewsActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ConsultUserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class ConsultUserListAdapter extends RecyclerView.Adapter<ConsultUserListAdapter.MyViewHolder> {

    public List<ConsultUserModel> myList;
    private Activity ctx;
    private OnItemOnLongClickListener mItemOnLongClickListener;

    public ConsultUserListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ConsultUserModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_consult_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position == 0) {
            holder.userPhotoView.setImageResource(R.drawable.news_icon);
            holder.userNameView.setText("消息通知");
        } else {
            int index = position - 1;
            ConsultUserModel itemModel = this.myList.get(index);
            holder.userNameView.setText(itemModel.userName);
            holder.lastMsgView.setText(itemModel.lastMsg);
            holder.lastDateView.setText(itemModel.lastDate);
            if (itemModel.msgCount == 0) {
                holder.msgCountView.setVisibility(View.INVISIBLE);
                holder.lastDateView.setVisibility(View.VISIBLE);
            } else {
                holder.msgCountView.setText(String.valueOf(itemModel.msgCount));
                holder.msgCountView.setVisibility(View.VISIBLE);
                holder.lastDateView.setVisibility(View.GONE);
            }
        }

        // set onClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 해당 대화창으로 이행
                if (position == 0) {
                    Utils.start_Activity(ctx, NewsActivity.class);
                    return;
                }
                int index = position - 1;
                ConsultUserModel itemModel = myList.get(index);
                Utils.start_Activity(ctx, ChatActivity.class,
                        new BasicNameValuePair(Constant.USER_ID, itemModel.userID),
                        new BasicNameValuePair(Constant.USER_NAME, itemModel.userName),
                        new BasicNameValuePair(Constant.USER_PHOTO, itemModel.userPhoto));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mItemOnLongClickListener.onConsultLongClick(position - 1);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.size() + 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final TextView lastMsgView;
        private final TextView lastDateView;
        private final TextView msgCountView;


        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            lastMsgView = (TextView) itemView.findViewById(R.id.tv_last_msg);
            lastDateView = (TextView) itemView.findViewById(R.id.tv_last_date);
            msgCountView = (TextView) itemView.findViewById(R.id.tv_msg_count);
        }
    }


    public void onItemClick(int position) {
        if (mItemOnLongClickListener == null)
            return;
        mItemOnLongClickListener.onConsultLongClick(position);
    }

    public void setItemOnClickListener(
            OnItemOnLongClickListener onItemOnClickListener) {
        this.mItemOnLongClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnLongClickListener {
        public void onConsultLongClick(int position);
    }
}
