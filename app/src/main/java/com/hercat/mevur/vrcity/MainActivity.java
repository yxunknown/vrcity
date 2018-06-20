package com.hercat.mevur.vrcity;

import android.graphics.Camera;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.preview)
    TextureView textureView;

    private CameraManager manager;

    private String cameraId;


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
                openCamera(width, height);
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
    }

    private void openCamera(int width, int height) {
        try {
            //chose camera
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics cc = manager.getCameraCharacteristics(id);
                Integer back = cc.get(CameraCharacteristics.LENS_FACING);
                if (back == 2) {
                    cameraId = id;
                }
            }

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
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
    private CaptureRequest.Builder builder;
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

}
