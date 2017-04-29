package grl.com.adapters.order;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import grl.com.activities.otherRel.EnergyRequestActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.Utils;
import grl.com.dataModels.ContactsModel;
import grl.com.httpRequestTask.newEnergy.EnergyAcceptTask;
import grl.com.network.HttpCallback;
import grl.com.subViews.dialogues.ActionItem;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/18/2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    public List<ContactsModel.Contacts> myList;
    private Activity ctx;
    private OnItemOnClickListener mItemOnClickListener;

    public ContactsAdapter(Activity context) {
        this.ctx = context;
        this.myList = new ArrayList<ContactsModel.Contacts>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_user_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // 사용자의 사진을 누렀을 때의 처리 진행
        ContactsModel.Contacts contact = myList.get(position);
        holder.nameView.setText(contact.contactName);
        if (contact.contactPhoto == null || contact.contactPhoto.isEmpty())
            holder.userPhoto.setImageDrawable(ctx.getResources().getDrawable(R.drawable.user_default));
        else
            holder.userPhoto.setImageURI(Uri.parse(contact.contactPhoto));

        holder.phoneView.setText(contact.contactPhone.get(0));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userPhoto;
        private final TextView nameView;
        private final TextView phoneView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhoto = (ImageView) itemView.findViewById(R.id.img_user_photo);
            nameView = (TextView) itemView.findViewById(R.id.tv_user_name);
            phoneView = (TextView) itemView.findViewById(R.id.tv_user_state);
        }
    }

    public void setItemOnClickListener(
            OnItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    public static interface OnItemOnClickListener {
        public void onItemClick(int position);
    }
}