package com.qtt.gcenter.cameraproject.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.qtt.gcenter.cameraproject.utils.Utils;

/**
 * @Author: kai.xu
 * @CreateDate: 2021/3/12 10:50 AM
 * @Description:
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private int cameraId;
    private Camera.Size mCameraSize;
    private Camera.FaceDetectionListener mFaceDetectionListener;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        //注册回调方法

        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    public void setFaceDetectionListener(Camera.FaceDetectionListener mFaceDetectionListener) {
        this.mFaceDetectionListener = mFaceDetectionListener;
    }

    public void bindCamera(Camera mCamera, Camera.Size mCameraSize, int mCameraId) {
        this.cameraId = mCameraId;
        this.mCamera = mCamera;
        this.mCameraSize = mCameraSize;
        startCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopFaceDetection();
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            startCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (null != mCamera) {
                mCamera.setPreviewCallback(null);
                mCamera.setFaceDetectionListener(null);
                mCamera.stopFaceDetection();
                mCamera.stopPreview();//停止预览
                mCamera.release(); // 释放相机资源
                mCamera = null;
            }
            if (null != mSurfaceHolder) {
                mSurfaceHolder.removeCallback(this);
                mSurfaceHolder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCamera() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            Utils.setCameraDisplayOrientation(mContext, cameraId, mCamera);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mCameraSize.width, mCameraSize.height);
            Camera.Size picSize = Utils.getCameraPictureSize(mCamera);
            parameters.setPictureSize(picSize.width, picSize.height);
            System.out.println("---pic---" + picSize.width + "------" + picSize.height);
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setExposureCompensation(0);
            parameters.setJpegQuality(100);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            if (mFaceDetectionListener != null) {
                mCamera.setFaceDetectionListener(mFaceDetectionListener);
                mCamera.startFaceDetection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != mCamera) {
            mCamera.autoFocus(null);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
