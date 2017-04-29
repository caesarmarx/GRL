package grl.com.adapters.energy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import grl.wangu.com.grl.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by Administrator on 6/7/2016.
 */
public class TGroupSectionTitleAdapter extends StatelessSection {

    List<String> myList = Arrays.asList(new String[1] );
    Activity context;
    public TGroupSectionTitleAdapter(Activity context) {
        super(R.layout.layout_none, R.layout.layout_none,  R.layout.layout_tgroup_title);

        this.context = context;

        myList.set(0, context.getResources().getString(R.string.tgroup_title));
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
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder)holder;
        //bind your view here
        itemHolder.tvItem.setText(myList.get(position));
    }

    class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvItem;

        public MyItemViewHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}