package com.hercat.mevur.vrcity;

import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ar.bean.DuMixARConfig;
import com.baidu.ar.util.Res;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.hercat.mevur.vrcity.service.CodeService;

import java.io.EOFException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements Callback<ResponseBody> {

    @BindView(R.id.preview)
    TextureView textureView;

    @BindView(R.id.location)
    TextView location;

    private CameraManager manager;
    private CaptureRequest.Builder builder;

    private String cameraId;

    @BindView(R.id.device_list)
    TextView deviceList;

    @BindView(R.id.radar)
    TextView radar;

    private Sensor orientationSensor;

    private Sensor accelerometer;
    private Sensor magnetic;

    private SensorManager sensorManager;

    private LocationClient locationClient = null;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private static final String TAG = "camera activity";

    private CodeService service;

    private final static String AK = "yiPbVyi1AkCBckV0n0scLThN4nV21ygC";
    private final static String BASE_URL = "http://api.map.baidu.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        manager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        updateOrientation();
        getLocation();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        service = retrofit.create(CodeService.class);
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

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
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
                List<Poi> pois = bdLocation.getPoiList();
                radar.setText("");
                String poiValue = "";
                for (Poi poi : pois) {
                    String address = city + distict + street + poi.getName();
                    Call<ResponseBody> call = service.getPoint(address, AK, "json");
                    call.enqueue(MainActivity.this);
                    poiValue += poi.getName() + "\n";
                }
                radar.setText(poiValue);
                Log.i(TAG, "onReceiveLocation: " + bdLocation);
            }
        });
        locationClient.start();
    }

    //<editor-fold desc="方向传感器">
    private void updateOrientation() {
        float[] values = new float[3];
        float[] r = new float[9];
        SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(r, values);
        values[0] = (int) Math.toDegrees(values[0]);
        values[1] = (int) Math.toDegrees(values[1]);
        values[2] = (int) Math.toDegrees(values[2]);
        String orientationValues = "北偏离:" + values[0] + "\n" +
                "仰俯:" + values[1] + "\n" +
                "倾斜:" + values[2];
        deviceList.setText(orientationValues);
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(new MySensorEventListener(), accelerometer, Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new MySensorEventListener(), magnetic, Sensor.TYPE_MAGNETIC_FIELD);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(new MySensorEventListener());
        super.onPause();
    }

    private class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }
            updateOrientation();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    //</editor-fold>

    //<editor-fold desc="相机预览">
    private void openCamera() {
        try {
            //chose camera
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics cc = manager.getCameraCharacteristics(id);
                Integer back = cc.get(CameraCharacteristics.LENS_FACING);
                if (back == 2) {
                    cameraId = id;
                }
            }

            manager.openCamera("0", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    startPreview(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, null);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPreview(final CameraDevice cameraDevice) {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(new Surface(texture));
            cameraDevice.createCaptureSession(Arrays.asList(new Surface(texture)),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (null != cameraDevice) {
                                try {
                                    builder.set(CaptureRequest.CONTROL_AF_MODE,
                                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                    builder.set(CaptureRequest.CONTROL_AE_MODE,
                                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                    CaptureRequest request = builder.build();
                                    session.setRepeatingRequest(request, null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>


    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
