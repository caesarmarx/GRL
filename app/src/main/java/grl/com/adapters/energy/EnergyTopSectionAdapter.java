package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import grl.com.activities.energy.fGroup.FriendGroupActivity;
import grl.com.activities.energy.NewEnergyActivity;
import grl.com.activities.energy.OrderGroupPlateActivity;
import grl.com.configuratoin.NotificationManager;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Administrator on 6/7/2016.
 */
public class EnergyTopSectionAdapter extends StatelessSection {

    List<String> myList = Arrays.asList(new String[3] );
    public Activity context;
    public EnergyTopSectionAdapter(Activity context) {
        super(R.layout.normal_section_row, R.layout.normal_section_row,  R.layout.main_func_row);

        this.context = context;

        myList.set(0, context.getResources().getString(R.string.new_energy_title));
        myList.set(1, context.getResources().getString(R.string.order_plate_title));
        myList.set(2, context.getResources().getString(R.string.friend_group_title));
    }

    @Override
    public int getContentItemsTotal() {
        return myList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder)holder;
        //bind your view here
        itemHolder.tvItem.setText(myList.get(position));
        if(position == 0) {                                 // 신능량인 경우 badge 현시
            if(NotificationManager.energyNotificationData != null){
                int cnt = NotificationManager.energyNotificationData.size();
                if(cnt == 0)
                    itemHolder.tvUnread.setVisibility(View.GONE);
                else {
                    itemHolder.tvUnread.setText(String.valueOf(cnt));
                    itemHolder.tvUnread.setVisibility(View.VISIBLE);
                }
            }
        } else {
            itemHolder.tvUnread.setVisibility(View.GONE);
        }
        // add action on view
        ((MyItemViewHolder) holder).view.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                switch(position) {
                    case 0:                                 // 신능량단추를 누른 경우
                        Utils.start_Activity(context, NewEnergyActivity.class);
                        break;
                    case 1:                                 // 령패계단추를 누른 경우
                        Utils.start_Activity(context, OrderGroupPlateActivity.class);
                        break;
                    case 2:                                 // 친구계단추를 누른 경우
                        Utils.start_Activity(context, FriendGroupActivity.class);
                        break;
                }
            }
        });

        if(position == 2) {
            itemHolder.borderView.setVisibility(View.GONE);
        } else {
            itemHolder.borderView.setVisibility(View.VISIBLE);
        }
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvItem;
        private final View view;
        private final View borderView;
        private final TextView tvUnread;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tvTitle);
            borderView = (View) itemView.findViewById(R.id.view_border);
            tvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
            view = itemView;
        }
    }
}
