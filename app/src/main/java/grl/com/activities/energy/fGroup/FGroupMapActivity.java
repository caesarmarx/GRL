package grl.com.activities.energy.fGroup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.GlobalVars;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;

/**
 * Created by Administrator on 7/11/2016.
 */
public class FGroupMapActivity extends Activity implements View.OnClickListener, AMap.OnMapClickListener, AMap.OnMapScreenShotListener{
    MapView mapView;
    private AMap aMap;

    private LatLng selectedPoint;
    double latitude;
    double longitude;
    int zoom;

    // require
    JSONArray members;
    String strBackTitle;
    String strTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fgroup_map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写


        getParamsFromIntent();
        getViewByIDs();
        initNavBar();
        initializeData();
        setOnListener();
        init();
    }

    public void getParamsFromIntent () {
        Intent intent = getIntent();
        strBackTitle = intent.getStringExtra(Constant.BACK_TITLE_ID);
        strTitle = intent.getStringExtra(Constant.TITLE_ID);
        String strMembers = intent.getStringExtra("members");
        try {
            this.members = new JSONArray(strMembers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getViewByIDs () {

    }

    public void initializeData () {
        latitude = 0;
        longitude = 0;
        zoom = 0;
    }
    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
    }

    public void initNavBar() {
        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_left).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_title).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_right).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_left)).setText(strBackTitle);
        ((TextView)findViewById(R.id.txt_title)).setText(strTitle);

    }

    public void init () {
        // set up Adapter
        aMap = mapView.getMap();
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setOnMapClickListener(this);

        if (zoom == 0)
            zoom = 10;

        // 본인위치 현시 진행
        if (latitude < Double.MIN_NORMAL || longitude < Double.MIN_NORMAL) {
            latitude = SelfInfoModel.latitude;
            longitude = SelfInfoModel.longitude;
        }
        aMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));

        // 친구계성원들을 현시한다.

            for (int i = 0; i < members.length(); i++) {
                final int finalI = i;
                        JSONObject obj = null;
                        try {
                            obj = members.getJSONObject(finalI);

                            final double lat = obj.getDouble("latitude");
                            final double lon = obj.getDouble("longitude");
                            String userPhotoPath = obj.getString("user_photo");
                            String userName = obj.getString("user_name");

                            final MarkerOptions options = new MarkerOptions();
                            options.position(new LatLng(lat, lon));
                            if (userPhotoPath.equals("")) {
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_default);
                                options.icon(BitmapDescriptorFactory.fromBitmap(scaleCircleBitmap(bmp)));
                                aMap.addMarker(options);
                            } else {
                                String imgUrl = GlobalVars.SEVER_ADDR + "/" + userPhotoPath;
                                Ion.with(FGroupMapActivity.this).load(imgUrl).withBitmap().asBitmap()
                                        .setCallback(new FutureCallback<Bitmap>() {
                                            @Override
                                            public void onCompleted(Exception e, Bitmap result) {
                                                // do something with your bitmap
                                                options.icon(BitmapDescriptorFactory.fromBitmap(scaleCircleBitmap(result)));
                                                aMap.addMarker(options);
                                            }
                                        });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

    }

    public Bitmap scaleCircleBitmap(Bitmap bmp) {
        int w = (int) GlobalVars.dp2px(this, 40);
        Bitmap result = Bitmap.createScaledBitmap(bmp, w, w, false);
        final Bitmap output = Bitmap.createBitmap(result.getWidth(),
                result.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(result, rect, rect, paint);

        result.recycle();
        return output;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:
                break;

        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }
}
