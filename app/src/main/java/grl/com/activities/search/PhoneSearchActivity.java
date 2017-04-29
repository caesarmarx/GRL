package grl.com.activities.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.lang.reflect.Field;

import grl.com.adapters.search.PhoneSearchAdapter;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/1/2016.
 */
public class PhoneSearchActivity extends Activity implements View.OnClickListener {

    // view
    SearchView searchView;
    RecyclerView recyclerView;

    // value
    PhoneSearchAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_num_search);

        getParamsFromIntent();
        initNavBar();
        getViewByID();
        initializeData();
        setOnListener();
    }

    public void getParamsFromIntent () {

    }

    public void initNavBar () {

    }

    public void getViewByID () {
        searchView = (SearchView) findViewById(R.id.searchView);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initializeData () {
        // setup recycler adapter
        recyclerAdapter = new PhoneSearchAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // init view
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }
        searchView.setIconified(false);
    }

    public void setOnListener () {
        findViewById(R.id.img_back).setOnClickListener(this);

        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });

        //*** setOnQueryTextListener ***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                recyclerAdapter.searchPhone(query);
                searchView.setFocusable(false);
                getCurrentFocus().clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:                 // 되돌이 단추를 누를 때의 처리 진행
                Utils.finish(this);
                break;
        }
    }
}
