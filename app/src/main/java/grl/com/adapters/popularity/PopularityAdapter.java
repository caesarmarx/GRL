package grl.com.adapters.popularity;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL;

import grl.com.activities.imageManage.ImagePreviewActivity;
import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.PopularityModel;
import grl.com.subViews.dialogues.PopularityEstDialog;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/8/2016.
 */

public class PopularityAdapter extends RecyclerView.Adapter<PopularityAdapter.MyViewHolder> implements PopularityEstDialog.OnItemOnClickListener{

    public List<PopularityModel> myList;
    private Activity ctx;
    private OnItemOnClickListener mItemOnClickListener;

    PopularityEstDialog operateDlg;

    int operateIndex = -1;

    public PopularityAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<PopularityModel>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_popularity_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        PopularityModel model = this.myList.get(position);

        GlobalVars.loadImage(holder.userPhotoView, model.userPhoto);
        holder.userNameView.setText(model.userName);
        holder.descriptionView.setText(model.describeContent);
        if (model.photoPath.isEmpty()) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            GlobalVars.loadImage(holder.imageView, model.photoPath);
        }
        if (model.sureMans == null || model.sureMans.length() == 0)
            holder.sureNameView.setVisibility(View.GONE);
        else {
            holder.sureNameView.setText("@证人: ");
            for (int i = 0; i < model.sureMans.length(); i++) {
                try {
                    JSONObject object = model.sureMans.getJSONObject(i);
                    holder.sureNameView.append(GlobalVars.getStringFromJson(object, "user_name") + ";");
                } catch (Exception ex) {

                }
            }
        }
        holder.praiseTotalView.setText(String.format("%s : %d", ctx.getResources().getString(R.string.popularity_praise), model.praiseTotal));
        holder.estimateTotalView.setText(String.format("%s : %d", ctx.getResources().getString(R.string.popularity_estimate), model.estimateTotal));
        if (model.estName.compareTo("SY_FACTOR") == 0) {
            int good = 0, normal = 0, bad = 0;
            for (int i = 0; i < model.estimateList.length(); i++) {
                try {
                    JSONObject object = model.estimateList.getJSONObject(i);
                    if (GlobalVars.getIntFromJson(object, "praise_value") == 3) {
                        good ++;
                    }
                    if (GlobalVars.getIntFromJson(object, "praise_value") == 1) {
                        normal ++;
                    }
                    if (GlobalVars.getIntFromJson(object, "praise_value") == -3) {
                        bad ++;
                    }
                } catch (Exception ex) {

                }
            }
            holder.praiseTotalView.setText(String.format("好:%d 中:%d 差:%d", good, normal, bad));
        }
        // set onClickListener
        holder.userPhotoView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PopularityModel model = myList.get(position);
                Utils.start_Activity(ctx, OtherMainActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, ctx.getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.TITLE_ID, model.userName),
                        new BasicNameValuePair("user_id", model.userId),
                        new BasicNameValuePair("user_name", model.userName),
                        new BasicNameValuePair("user_photo", model.userPhoto));
            }
        });
        // set onClickListener
        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopularityModel model = myList.get(position);
                Utils.start_Activity(ctx, ImagePreviewActivity.class,
                        new BasicNameValuePair(Constant.PHOTO_PATH, model.photoPath));
            }
        });
        holder.operationView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                mItemOnClickListener.onItemClick(position);
                operateDlg = new PopularityEstDialog(ctx, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                operateDlg.show(holder.operationView);
                operateDlg.setItemOnClickListener(PopularityAdapter.this);
                operateIndex = position;
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

        private final ImageView userPhotoView;
        private final TextView userNameView;
        private final LinearLayout photoLayout;
        private final TextView descriptionView;
        private final TextView timeView;
        private final ImageView imageView;
        private final FrameLayout operationView;
        private final TextView sureNameView;
        private final TextView praiseTotalView;
        private final TextView estimateTotalView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhotoView = (ImageView) itemView.findViewById(R.id.img_user_photo);
            userNameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            descriptionView = (TextView) itemView.findViewById(R.id.tv_description);
            photoLayout = (LinearLayout) itemView.findViewById(R.id.layout_photo);
            imageView = (ImageView) itemView.findViewById(R.id.img_photo);
            timeView = (TextView) itemView.findViewById(R.id.tv_time);
            operationView = (FrameLayout) itemView.findViewById(R.id.img_operation);
            sureNameView = (TextView) itemView.findViewById(R.id.tv_sure_man);
            praiseTotalView = (TextView) itemView.findViewById(R.id.tv_praise_total);
            estimateTotalView = (TextView) itemView.findViewById(R.id.tv_estimate_total);

        }
    }

    public void onItemClick(int resId) {
        if (mItemOnClickListener == null)
            return;
        mItemOnClickListener.onItemClick(operateIndex, resId);
    }

    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnClickListener {
        public void onItemClick(int position, int resId);
    }
}
