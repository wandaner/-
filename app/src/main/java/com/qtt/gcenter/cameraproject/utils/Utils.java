package com.qtt.gcenter.cameraproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Surface;

import com.qtt.gcenter.cameraproject.camera.CameraView;

import java.util.List;

import androidx.core.content.ContextCompat;

public class Utils {

    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait    是否竖屏
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int requestSizeW;
        int requestSizeH;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            requestSizeW = surfaceHeight;
            requestSizeH = surfaceWidth;
        } else {
            requestSizeW = surfaceWidth;
            requestSizeH = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == requestSizeW) && (size.height == requestSizeH)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) requestSizeW) / requestSizeH;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }


    /**
     * 检查是否拥有指定的所有权限
     */
    public static boolean checkPermissionAllGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
        Activity activity = findActivity(context);
        if (activity != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int displayDegree;
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                displayDegree = (info.orientation + degrees) % 360;
                displayDegree = (360 - displayDegree) % 360;
            } else {
                displayDegree = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(displayDegree);
        }
    }

    public static Camera.Size getCameraPreviewSize(Context context, Camera mCamera, int w, int h) {
        if (null != mCamera) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (w == 0 || h == 0) {
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                w = metrics.widthPixels;
                h = metrics.heightPixels;
            }
            return Utils.getCloselyPreSize(true, w, h,
                    parameters.getSupportedPreviewSizes());
        }
        return null;
    }

    public static Camera.Size getCameraPictureSize(Camera mCamera) {
        Camera.Size maxSize = null;
        if (null != mCamera) {
            Camera.Parameters parameters = mCamera.getParameters();
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
//                System.out.println("-----suu-----" + size.width + "  -  " + size.height);
                if (maxSize == null) {
                    maxSize = size;
                } else {
                    if (maxSize.width < size.width) {
                        maxSize = size;
                    }
                }
            }
        }
        return maxSize;
    }

    /**
     * 获取最大预览照片
     *
     * @param camera 相机
     * @return 返回最大预览尺寸
     */
    public static Camera.Size getMaxSupportPreviewSize(Camera camera) {
        List<Camera.Size> supportSizeList = camera.getParameters().getSupportedPreviewSizes();
        if (supportSizeList == null || supportSizeList.size() == 0) {
            return camera.new Size(100, 100);
        }
        Camera.Size maxSize = supportSizeList.get(0);
        if (supportSizeList.size() == 1) {
            return maxSize;
        }
        for (int i = 1; i < supportSizeList.size(); i++) {
            Camera.Size temp = supportSizeList.get(i);
            if (temp.width * temp.height > maxSize.width * maxSize.height) {
                maxSize = temp;
            }
        }
        return maxSize;
    }

    /**
     *
     */
    public static int getViewHeightByCameraSize(Camera.Size size, int viewWidth) {
        return (int) (1.0 * viewWidth * size.width / size.height);
    }
}
