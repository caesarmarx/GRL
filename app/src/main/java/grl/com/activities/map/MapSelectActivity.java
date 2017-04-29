package grl.com.activities.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Double2;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import grl.com.configuratoin.Constant;
import grl.com.configuratoin.FileUtils;
import grl.com.configuratoin.SelfInfoModel;
import grl.com.configuratoin.Utils;
import grl.wangu.com.grl.R;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapSelectActivity extends Activity implements View.OnClickListener, AMap.OnMapClickListener, AMap.OnMapScreenShotListener{

    String title;
    String rightBtnTiltle;

    MapView mapView;
    private AMap aMap;
    Marker marker = null;

    private  LatLng selectedPoint;
    double latitude;
    double longitude;
    int zoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);

        Intent intent = getIntent();
        title = intent.getStringExtra(Constant.ACTIVITY_TITLE);
        if (title == null || title.isEmpty())
            title = "";
        rightBtnTiltle = intent.getStringExtra(Constant.RIGHT_BTN_TITLE);
        if (rightBtnTiltle == null || rightBtnTiltle.isEmpty())
            rightBtnTiltle = getResources().getString(R.string.button_send);

        latitude = intent.getDoubleExtra(Constant.SAVED_LATITUDE, 0);
        longitude = intent.getDoubleExtra(Constant.SAVED_LONGITUDE, 0);
        zoom = intent.getIntExtra(Constant.SAVED_ZOOM, 0);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写


        initNavBar();
        setOnListener();
        init();
    }

    public void setOnListener() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.txt_left).setOnClickListener(this);
        findViewById(R.id.txt_right).setOnClickListener(this);
        }

    public void initNavBar() {
        ((ImageView)findViewById(R.id.img_back)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_left)).setText(this.getString(R.string.user_nav_back));
        ((TextView)findViewById(R.id.txt_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_title)).setText(title);
        ((TextView)findViewById(R.id.txt_right)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.txt_right)).setText(rightBtnTiltle);
    }

    public void init () {
        // set up Adapter
        aMap = mapView.getMap();
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setOnMapClickListener(this);

        if (zoom == 0)
            zoom = 10;

        if (latitude < Double.MIN_NORMAL || longitude < Double.MIN_NORMAL) {
            latitude = SelfInfoModel.latitude;
            longitude = SelfInfoModel.longitude;
        }
        marker = aMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));

//        aMap.setLocationSource(this)
    }

    void sendLocation() {
        aMap.getMapScreenShot(this);
        aMap.invalidate();// 刷新地图
        String path = FileUtils.getSendDir(getApplicationContext()) +  "MAP_CAPTURE" + ".jpg";

        Intent data = new Intent();
        data.putExtra(Constant.SAVED_LATITUDE, latitude);
        data.putExtra(Constant.SAVED_LONGITUDE, longitude);
        data.putExtra(Constant.SAVED_ZOOM, zoom);
        data.putExtra(Constant.MAP_THUMB, path);
        setResult(RESULT_OK, data);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.img_back:                     // 되올이 단추를 누른 경우
            case R.id.txt_left:
                Utils.finish(this);
                break;
            case R.id.txt_right:
                sendLocation();
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
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        if (marker == null)
            marker = aMap.addMarker(new MarkerOptions().position(latLng));
        else
            marker.setPosition(latLng);

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(null == bitmap){
            return;
        }
        try {
//            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 392, 512,
//                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            String path = FileUtils.getSendDir(getApplicationContext()) +  "MAP_CAPTURE" + ".jpg";
            FileOutputStream fos = new FileOutputStream(path);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, Constant.IMAGE_HEIGHT / 2, Constant.IMAGE_WIDTH / 2, true);
            boolean b = resized.compress(Bitmap.CompressFormat.PNG, 100, fos);

            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
