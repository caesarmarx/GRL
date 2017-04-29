package grl.com.adapters.discovery;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grl.com.configuratoin.GlobalVars;
import grl.com.dataModels.AccountModel;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/7/2016.
 */
public class AccountSettingAdapter  extends RecyclerView.Adapter<AccountSettingAdapter.MyViewHolder>{
    Activity context;
    List<AccountModel> myList;
    int type;                   // 0: plate 1: gift 2: exchange

    public AccountSettingAdapter (Activity context) {
        this.context = context;
        type = 0;
        myList = new ArrayList<AccountModel>();
    }

    public void notifyData (List<AccountModel> data, int type) {
        this.myList = data;
        this.type = type;

        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_account_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        AccountModel dataModel = myList.get(position);
        String path = dataModel.itemImage;
        GlobalVars.loadImage(holder.imgItem, path);
        String strTitile = "";
        if(type == 2) {
            strTitile = dataModel.itemName + " " + String.valueOf(dataModel.itemPrice) + dataModel.itemPriceUnit;
        } else {
            strTitile = dataModel.itemName + " " + String.valueOf(dataModel.count) + "个";
        }
        holder.tvTitle.setText(strTitile);
    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgItem;
        private final TextView tvTitle;
        private final View view;
        public MyViewHolder(final View itemView) {
            super(itemView);

            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            view = itemView;

            itemView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = (int) displaymetrics.widthPixels / 3;
                    params.height = height;
                    itemView.setLayoutParams(params);
                }
            }, 1);

            imgItem.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgItem.getLayoutParams();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = (int) ((int) displayMetrics.widthPixels / 3 - GlobalVars.dp2px(context, 50));
                    params.height = height;
                    params.width = height;
                    imgItem.setLayoutParams(params);
                }
            }, 10);
        }
    }
}
