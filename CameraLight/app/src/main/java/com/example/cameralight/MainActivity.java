package com.example.cameralight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;

import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button open, close;
    private CameraManager manager;
    private Camera m_Camera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        open = (Button) findViewById(R.id.open);
        close = (Button) findViewById(R.id.close);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                lightopen();
                break;
            case R.id.close:
                lightclose();
                break;
        }
    }

    private void lightopen() {
        lightSwitch(false);
    }

    private void lightclose() {
        lightSwitch(true);
    }

    private void lightSwitch(final boolean lightStatus) {
        if (lightStatus) { // 關手電筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //當版本大於等於23時會執行
                try {
                    manager.setTorchMode("0", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //如果版本沒有大於23就會釋放相機
                if (m_Camera != null) {
                    m_Camera.stopPreview();
                    m_Camera.release();
                    m_Camera = null;
                }
            }
        } else { // 開手電筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //當版本大於等於23時會執行
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PackageManager pm = getPackageManager();
                final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                for (final FeatureInfo f : features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        //看是否支援閃光燈
                        if (null == m_Camera) {
                            m_Camera = Camera.open();
                        }
                        final Camera.Parameters parameters = m_Camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        m_Camera.setParameters(parameters);
                        m_Camera.startPreview();
                    }
                }
            }
        }
    }
}