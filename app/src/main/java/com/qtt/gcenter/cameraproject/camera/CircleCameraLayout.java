package com.qtt.gcenter.cameraproject.camera;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qtt.gcenter.cameraproject.R;
import com.qtt.gcenter.cameraproject.utils.CameraUtils;
import com.qtt.gcenter.cameraproject.utils.Utils;

import java.util.Locale;


/**
 * @Author: kai.xu
 * @CreateDate: 2021/3/12 10:50 AM
 * @Description: 圆形的人脸识别
 */
public class CircleCameraLayout extends FrameLayout implements Handler.Callback {

    private static final int MSG_FACE_DETECT = 0x0001;
    private String msg = "请将面部完全呈现在圆形识别区域内";

    private Handler mHandler = new Handler(this);
    private Context mContext;
    private CameraView mCameraView;//摄像预览
    private CircleLayerView mLayerView;
    private double whRatio = 0.0;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;//前置摄像头
    private Camera mCamera;
    private Camera.Size mCameraSize;
    private TextView tvMsg;

    private int mWidth;
    private int mHeight;
    private Matrix matrix;

    private Camera.FaceDetectionListener mFaceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            System.out.println("------detect----" + faces.length);
            Message msg = Message.obtain();
            msg.what = MSG_FACE_DETECT;
            msg.obj = faces;
            mHandler.sendMessage(msg);
        }
    };

    public CircleCameraLayout(Context context) {
        this(context, null);
    }

    public CircleCameraLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleCameraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {

        whRatio = 1.0 * mContext.getResources().getDisplayMetrics().widthPixels / mContext.getResources().getDisplayMetrics().heightPixels;

        mCameraView = new CameraView(mContext);
        LayoutParams cameraLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cameraLayoutParam.gravity = Gravity.CENTER;
        addView(mCameraView, cameraLayoutParam);

        mLayerView = new CircleLayerView(mContext);
        LayoutParams layerParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layerParam.gravity = Gravity.CENTER;
        addView(mLayerView, layerParam);

        tvMsg = new TextView(mContext);
        tvMsg.setTextColor(Color.RED);
        tvMsg.setTextSize(1.0f * mContext.getResources().getDisplayMetrics().density * 6);
        LayoutParams textParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParam.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(tvMsg, textParam);

        mCamera = Camera.open(mCameraId);
        mCameraSize = Utils.getMaxSupportPreviewSize(mCamera);
//        mCameraView.setFaceDetectionListener(mFaceDetectionListener);
//        mCameraView.bindCamera(mCamera, mCameraSize, mCameraId);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = Utils.getViewHeightByCameraSize(mCameraSize, mWidth);
        int w = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_FACE_DETECT) {
            Camera.Face[] faces = (Camera.Face[]) msg.obj;
            if (faces == null || faces.length == 0) {
                tvMsg.setText("请将面部完全呈现在圆形识别区域内");
            } else if (faces.length > 1) {
                tvMsg.setText("仅能放置一个面部");
            } else {
                if (matrix == null) {
                    matrix = CameraUtils.prepareMatrix(false, 270, mWidth, mHeight);
                }
                Camera.Face face = faces[0];
                RectF rectF = new RectF(face.rect);
                matrix.mapRect(rectF);
                mLayerView.setFaceRectF(rectF);
                tvMsg.setText(String.format(Locale.getDefault(), "监测到人脸[ %f , %f , %f , %f ],左眼[ %d ],右眼 [ %d ],嘴巴 [ %d ],得分 [ %d ]",
                        rectF.left, rectF.top, rectF.right, rectF.bottom,
                        face.leftEye == null ? 0 : 1,
                        face.rightEye == null ? 0 : 1,
                        face.mouth == null ? 0 : 1,
                        face.score));

            }
        }
        return false;
    }
}
