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

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.preview)
    TextureView textureView;

    private CameraManager manager;
    private CaptureRequest.Builder builder;

    private String cameraId;

    @BindView(R.id.device_list)
    TextView deviceList;

    private Sensor orientationSensor;

    private Sensor accelerometer;
    private Sensor magnetic;

    private SensorManager sensorManager;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private static final String TAG = "camera activity";


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
    }

    //<editor-fold desc="方向传感器">
    private void updateOrientation() {
        float[] values = new float[3];
        float[] r = new float[9];
        SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(r, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);
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

}
