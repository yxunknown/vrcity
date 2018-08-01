package com.hercat.mevur.vrcity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.hercat.mevur.vrcity.entity.PointInfo;
import com.hercat.mevur.vrcity.service.RequestListener;
import com.hercat.mevur.vrcity.tools.DirectionAngelUtil;
import com.hercat.mevur.vrcity.tools.PointPool;
import com.hercat.mevur.vrcity.tools.Tipper;
import com.hercat.mevur.vrcity.view.EndlessHorizontalScrollView;
import com.hercat.mevur.vrcity.view.EndlessHorizontalScrollViewAdapter;
import com.hercat.mevur.vrcity.view.OrientationListener;
import com.hercat.mevur.vrcity.view.RadarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bertsir.cameralibary.CameraView;


//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑
//   ┃　　　┃   代码无BUG！
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛


public class MainActivity extends FragmentActivity implements RequestListener {

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.device_list)
    TextView deviceList;

    private LocationClient locationClient = null;
    private BDLocation currentLocation;


    private static final String TAG = "camera activity";


    private final static String AK = "yiPbVyi1AkCBckV0n0scLThN4nV21ygC";
    private final static String BASE_URL = "http://api.map.baidu.com";

    private List<PointInfo> pointInfos;

    private PointPool pointPool;

    private float currentDirection;




    @BindView(R.id.info_container)
    EndlessHorizontalScrollView scrollView;


    @BindView(R.id.cv)
    CameraView cameraView;

    @BindView(R.id.compass)
    RadarView radarView;

    private Adapter mAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        pointInfos = new ArrayList<>();

        pointPool = PointPool.instance();
        pointInfos = pointPool.getData();
        System.out.println(pointInfos);

        //start get current location
        getLocation();

//        cameraView.open(this);
//
        //<editor-fold desc="ar demo code">
        //ar
//        if (null != findViewById(R.id.ar_container)) {
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            Bundle data = new Bundle();
//            JSONObject obj = new JSONObject();
//            try {
////                obj.put(ARConfigKey.AR_KEY, "10199081");
//                obj.put(ARConfigKey.AR_KEY, "10199081");
//                obj.put(ARConfigKey.AR_TYPE, 5);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            data.putString(ARConfigKey.AR_VALUE, obj.toString());
//            ARFragment arFragment = new ARFragment();
//            arFragment.setArguments(data);
//            System.out.println("sss");
//            try {
//                fragmentTransaction.replace(R.id.ar_container, arFragment);
//                fragmentTransaction.commitAllowingStateLoss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //</editor-fold>

        Tipper.initialize(this);

        mAdapter = new Adapter(this, pointInfos);
        scrollView.setAdapter(mAdapter);
        scrollView.setOrientationListener(new OrientationListener() {
            @Override
            public void onOrientationChange(double orientation) {
                currentDirection = (float) orientation;
                updateOrientationData();
            }
        });
    }

    //<editor-fold desc="定位">
    private void getLocation() {
        locationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(false);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true);
        //是否需要显示地址信息, 默认 false

        option.setIsNeedAltitude(true);
        //是否需要海拔信息

        option.setIsNeedLocationDescribe(true);
        //是否需要位置描述

        option.setIsNeedLocationPoiList(true);
        //是否需要 poi 数据

        //是否需要方位数据
        option.setNeedDeviceDirect(true);

        locationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double latitude = bdLocation.getLatitude();    //获取纬度信息
                double longitude = bdLocation.getLongitude();    //获取经度信息
                float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
                float direction = bdLocation.getDirection();
                String addr = bdLocation.getAddrStr();    //获取详细地址信息
                String desc = bdLocation.getLocationDescribe();
                double altitude = bdLocation.getAltitude();
                String city = bdLocation.getCity();
                String distict = bdLocation.getDistrict();
                String street = bdLocation.getStreet();
                String locationValue = "纬度:" + latitude + "\n" +
                        "经度:" + longitude + "\n" +
                        "精度:" + radius + "米" + "\n" +
                        "海拔:" + altitude + "米" + "\n" +
                        "方位:" + direction + "\n" +
                        "当前地址:" + addr + "\n" +
                        "信息:" + desc;
                location.setText(locationValue);
                currentLocation = bdLocation;
                // refresh data
                // TODO: 18-7-31 here to refresh data based on current location

                new UpdateDataTask().execute();


            }
        });
        locationClient.start();
    }

    //</editor-fold>


    //<editor-fold desc="http response handler">
    @Override
    public void success(String response, int responseCode, String identity) {
    }

    @Override
    public void error(String error, int responseCode, String identity) {

    }

    //</editor-fold>


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.open(this);
    }

    private void updateOrientationData() {
        this.radarView.setOrientation(currentDirection);

    }


    private class UpdateDataTask extends AsyncTask<Void, View, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(View... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            LatLng p1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            for (PointInfo p : pointInfos) {
                LatLng p2 = new LatLng(p.getLat(), p.getLng());
                double angel = DirectionAngelUtil.relativeDirection(p1.latitudeE6, p1.longitudeE6,
                        p2.latitudeE6, p2.longitudeE6);
                double distance = DistanceUtil.getDistance(p1, p2);
                p.setDirectionAngel(angel);
                p.setDistance(distance);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println(pointInfos);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class Adapter extends EndlessHorizontalScrollViewAdapter {
        private Context context;
        private List<PointInfo> pointInfos;
        private LayoutInflater layoutInflater;

        public Adapter(Context context, List<PointInfo> pointInfos) {
            if (null == context || null == pointInfos) {
                throw new IllegalStateException("context or pointInfos can not be null.");
            }
            this.context = context;
            this.pointInfos = pointInfos;
            this.layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public double getDirection(int position) {
            return pointInfos.get(position).getDirectionAngel();
        }

        @Override
        public double getDistance(int position) {
            return pointInfos.get(position).getDistance();
        }

        @Override
        public int getCount() {
            return pointInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return pointInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PointInfo p = pointInfos.get(position);
            if (null == convertView) {
                convertView = layoutInflater.inflate(R.layout.info_item, null);
            }
            TextView name = convertView.findViewById(R.id.tv_name);
            TextView distance = convertView.findViewById(R.id.tv_distance);
            name.setText(p.getName());
            distance.setText(String.valueOf(p.getDistance()));
            if (p.getType() == PointInfo.TYPE_TXT_IMAGE) {
                ImageView img = convertView.findViewById(R.id.img);
                img.setImageResource(p.getLogoUrl());
            } else {
                convertView.findViewById(R.id.img).setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog pd = new MaterialDialog.Builder(MainActivity.this)
                            .title("详情")
                            .content(p.getName() + "\n" +
                            "距离:" + p.getDistance() + "米")
                            .positiveText("确定")
                            .build();
                    if (p.getType() == PointInfo.TYPE_TXT_IMAGE) {
                        pd.setIcon(p.getLogoUrl());
                    }
                    pd.show();
                }
            });
            return convertView;
        }
    }
}
