package grl.com.adapters.chat;

import android.app.Activity;
import android.graphics.Bitmap;
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

import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.FileUtils;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.MessageModel;
import grl.com.dataModels.UserModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder> {

    public List<MessageModel> myList;
    private Activity ctx;
    private boolean bNumberShow = false;
    private OnItemClickListener mItemClickListener;

    public FavouriteListAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<MessageModel>();
    }

    public void showNumber() {
        bNumberShow = true;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_favourite_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        MessageModel itemModel = this.myList.get(position);
        GlobalVars.loadImage(holder.userPhotoView, itemModel.getPhoto());
        holder.userNameView.setText(itemModel.getUserName());
        holder.textView.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.GONE);
        if (itemModel.getMsgType().compareTo(Constant.MSG_TEXT_TYPE) == 0) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(itemModel.getMsgText());
        }
        if (itemModel.getMsgType().compareTo(Constant.MSG_IMAGE_TYPE) == 0) {
            holder.imageView.setVisibility(View.VISIBLE);
            Bitmap bmp = FileUtils.readBitmap(itemModel.getThumbnailUrl());
            holder.imageView.setImageBitmap(bmp);
        }
        if (itemModel.getMsgType().compareTo(Constant.MSG_VIDEO_TYPE) == 0) {
            holder.imageView.setVisibility(View.VISIBLE);
            Bitmap bmp = FileUtils.readBitmap(itemModel.getThumbnailUrl());
            holder.imageView.setImageBitmap(bmp);
        }
        if (itemModel.getMsgType().compareTo(Constant.MSG_VOICE_TYPE) == 0) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(ctx.getString(R.string.msg_voice_text));
        }
        if (itemModel.getMsgType().compareTo(Constant.MSG_LOCATION_TYPE) == 0) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(ctx.getString(R.string.msg_location_text));
        }
        if (itemModel.getMsgType().compareTo(Constant.MSG_EMOTICON_TYPE) == 0) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(itemModel.getMsgText());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onFavouriteClick(position);
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
        private final TextView textView;
        private final ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            textView = (TextView) itemView.findViewById(R.id.tv_view);
            imageView = (ImageView) itemView.findViewById(R.id.img_view);
        }
    }

    public void onItemClick(int position) {
        if (mItemClickListener == null)
            return;
        mItemClickListener.onFavouriteClick(position);
    }

    public void setItemOnClickListener(
            OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        public void onFavouriteClick(int position);
    }
}
