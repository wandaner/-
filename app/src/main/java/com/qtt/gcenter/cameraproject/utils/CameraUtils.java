package com.qtt.gcenter.cameraproject.utils;


import android.graphics.Matrix;

/**
 * @Author: kai.xu
 * @CreateDate: 2021/3/15 4:15 PM
 * @Description:
 */
public class CameraUtils {

    /**
     * 准备用于转换的矩阵工具
     *
     * @param isBackCamera       是否后置相机
     * @param displayOrientation 摄像头设置的角度
     * @param viewWidth          预览界面宽
     * @param viewHeight         预览界面高
     */
    public static Matrix prepareMatrix(Boolean isBackCamera, int displayOrientation, int viewWidth, int viewHeight) {
        Matrix matrix = new Matrix();
        //前置摄像头处理镜像关系
        matrix.setScale(1f, isBackCamera ? 1f : -1f);
        matrix.postRotate(displayOrientation * 1f);
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
        return matrix;
    }
}
