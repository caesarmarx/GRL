package grl.com.adapters.search;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.http.BasicNameValuePair;

import org.json.JSONException;

import grl.com.activities.otherRel.OtherMainActivity;
import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.com.httpRequestTask.search.SearchPhoneTask;
import grl.com.network.HttpCallback;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/1/2016.
 */
public class PhoneSearchAdapter extends RecyclerView.Adapter<PhoneSearchAdapter.MyViewHolder>{
    Activity context;
    JsonArray myList;

    // task
    SearchPhoneTask searchPhoneTask;
    // value
    Boolean isSearching;
    Boolean isCompete;
    String searchText;
    static final int SEARCH_COUNT = 20;

    public PhoneSearchAdapter (Activity context) {
        this.context = context;
        this.myList = new JsonArray();
        isSearching = false;
        searchText = "";
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_search_phone_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(!isCompete && position == myList.size()) {
            holder.isSearching(true);
            gettingResult(searchText, myList.size(), SEARCH_COUNT);
            return;
        } else {
            holder.isSearching(false);
        }

        JsonObject info = myList.get(position).getAsJsonObject();
        final String photoPath = info.get("user_photo").getAsString();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!GlobalVars.loadImage(holder.imgUserPhoto, photoPath)) {
                    holder.imgUserPhoto.setImageDrawable(context.getResources().getDrawable(R.drawable.user_default));
                }
            }
        }, 1);
        holder.tvUserName.setText(info.get("user_name").getAsString());
        holder.tvUserPhone.setText(info.get("user_phone").getAsString());

        // 항목을 선택하였을 때 제3인자창으로 이행한다.
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myList.size() < position + 1){
                    return;
                }
                JsonObject info = myList.get(position).getAsJsonObject();
                JsonObject idObj = info.get("_id").getAsJsonObject();
                String otherID = idObj.get("$id").getAsString();
                String otherName = info.get("user_name").getAsString();
                String otherPhotoPath = info.get("user_photo").getAsString();

                Utils.start_Activity(context, OtherMainActivity.class,
                        new BasicNameValuePair(Constant.BACK_TITLE_ID, context.getString(R.string.user_nav_back)),
                        new BasicNameValuePair(Constant.TITLE_ID, otherName),
                        new BasicNameValuePair("user_id", otherID),
                        new BasicNameValuePair("user_photo", otherPhotoPath));
            }
        });
    }

    @Override
    public int getItemCount() {
        int cnt = myList.size();
        if(cnt > 0) {
            if(!isCompete)
                cnt += 1;
        } else {

        }
        return cnt;
    }

    public void searchPhone(String phoneNum) {
        this.searchText = phoneNum;
        this.myList = new JsonArray();

        this.isCompete = false;
        this.isSearching = false;
        gettingResult(searchText, 0, SEARCH_COUNT);
    }

    // 봉사기로 부터의 자료검색진행
    public void gettingResult(String keyword, int skip, int limit) {
        if(keyword.equals("") || isSearching == true)
            return;
        JsonObject params = new JsonObject();
        params.addProperty("user_id", SelfInfoModel.userID);
        params.addProperty("search_phone_num", keyword);
        params.addProperty("skip", skip);
        params.addProperty("limit", limit);
        isSearching = true;
        // send request
        searchPhoneTask = new SearchPhoneTask(context, params, new HttpCallback() {
            @Override
            public void onResponse(Boolean flag, Object Response) throws JSONException {
                isSearching = false;
                // check request
                if(!flag || Response == null) {
                    GlobalVars.showErrAlert(context);
                    return;
                }
                try {
                    JsonArray temp = (JsonArray) Response;
                    if(temp.size() == 0) {
                        isCompete = true;
                    }
                    myList.addAll(temp);
                    notifyDataSetChanged();
                } catch (Exception e) {}
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgUserPhoto;
        private final TextView tvUserName;
        private final TextView tvUserPhone;
        private final TextView tvSearching;
        private final View view;
        Boolean isSearchingValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imgUserPhoto = (ImageView) itemView.findViewById(R.id.img_user_photo);
            this.tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            this.tvUserPhone = (TextView) itemView.findViewById(R.id.tvUserPhone);
            this.tvSearching = (TextView) itemView.findViewById(R.id.tvSearching);
            this.view = itemView;
            isSearchingValue = false;
        }

        public void isSearching (Boolean flag) {
            isSearchingValue = flag ;
            if(isSearchingValue)
                tvSearching.setVisibility(View.VISIBLE);
            else
                tvSearching.setVisibility(View.GONE);
        }
    }
}
