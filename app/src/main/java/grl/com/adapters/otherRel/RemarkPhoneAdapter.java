package grl.com.adapters.otherRel;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 6/10/2016.
 */
public class RemarkPhoneAdapter extends RecyclerView.Adapter<RemarkPhoneAdapter.MyViewHolder>{

    public List<String> myList;
    private Activity context;

    public RemarkPhoneAdapter(Activity context) {
        this.context = context;
        this.myList = new ArrayList<String>();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_remark_phone_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public void notifyData(List<String> data) {
        this.myList = data;
        notifyDataSetChanged();
    }

    public List<String> getPhoneList () {
        return this.myList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(position < myList.size())
            holder.etPhoneNum.setText(this.myList.get(position));
        else
            holder.etPhoneNum.setText("");
        if((position == myList.size() && position < 4) || (myList.size() - 1 < position && position == 4)){
            holder.imgControl.setImageDrawable(context.getResources().getDrawable(R.drawable.plus_icon));
        } else {
            holder.imgControl.setImageDrawable(context.getResources().getDrawable(R.drawable.minus_icon));

        }
        holder.imgControl.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 추가 혹은 삭제 설정 밑 그에 대한 처리진행
                context.getCurrentFocus().clearFocus();
                if((position == myList.size() && position < 4) || (myList.size() - 1 < position && position == 4)){ // 추가 처리 진행
                    myList.add("");
                } else {                            // 삭제처리 진행
                    myList.remove(position);
                }
                notifyDataSetChanged();
            }
        });


        holder.etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(position >= myList.size())
                    return;
                String number = holder.etPhoneNum.getText().toString();
                myList.remove(position);
                myList.add(position, number);
            }
        });
    }

    @Override
    public int getItemCount() {
        int cnt = this.myList.size();
        if(cnt < 5) {
            cnt ++;
        }
        return cnt ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgControl;
        private final EditText etPhoneNum;
        public MyViewHolder(View itemView) {
            super(itemView);

            imgControl = (ImageView) itemView.findViewById(R.id.imgPhoneAdd);
            etPhoneNum = (EditText) itemView.findViewById(R.id.etPhoneNum);
        }
    }
}
