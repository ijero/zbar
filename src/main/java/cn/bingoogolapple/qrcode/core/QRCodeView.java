package cn.bingoogolapple.qrcode.core;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import camera.QRCodeReaderView;
import cn.bingoogolapple.qrcode.zxing.R;


public abstract class QRCodeView extends RelativeLayout implements QRCodeReaderView.OnQRCodeReadListener, QRCodeReaderView.ProcessDataDelegate {
    protected ScanBoxView mScanBoxView;
    protected Delegate mDelegate;
    protected Handler mHandler;
    protected ProcessDataTask mProcessDataTask;
    protected QRCodeReaderView qrCodeReaderView;

    public QRCodeView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        qrCodeReaderView = new QRCodeReaderView(getContext());
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setDelegate(this);
        qrCodeReaderView.setAutofocusInterval(500);

        mScanBoxView = new ScanBoxView(getContext());
        //设置为居中
        mScanBoxView.setCenterVertical(true);
        mScanBoxView.initCustomAttrs(context, attrs);
        qrCodeReaderView.setId(R.id.bgaqrcode_camera_preview);
        addView(qrCodeReaderView);
        LayoutParams layoutParams = new LayoutParams(context, attrs);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, qrCodeReaderView.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, qrCodeReaderView.getId());
        //暂时居中,如果有问题,解除上方注释
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(mScanBoxView, layoutParams);
    }

    /**
     * 设置扫描二维码的代理
     *
     * @param delegate 扫描二维码的代理
     */
    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ScanBoxView getScanBoxView() {
        return mScanBoxView;
    }

    /**
     * 显示扫描框
     */
    public void showScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏扫描框
     */
    public void hiddenScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.GONE);
        }
    }

    /**
     * 打开后置摄像头开始预览，但是并未开始识别
     */
    public void startCamera() {
        startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * 打开指定摄像头开始预览，但是并未开始识别
     *
     * @param cameraFacing
     */
    public void startCamera(int cameraFacing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                startCameraById(cameraId);
                break;
            }
        }
    }

    private void startCameraById(int cameraId) {
        try {
            qrCodeReaderView.setPreviewCameraId(cameraId);
            qrCodeReaderView.startCamera();
        } catch (Exception e) {
            if (mDelegate != null) {
                mDelegate.onScanQRCodeOpenCameraError();
            }
        }
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    public void stopCamera() {
        try {
            stopSpotAndHiddenRect();
            qrCodeReaderView.stopCamera();
        } catch (Exception e) {
        }
    }

    /**
     * 延迟1.5秒后开始识别
     */
    public void startSpot() {
        startSpotDelay(1000);
    }

    /**
     * 延迟delay毫秒后开始识别
     *
     * @param delay
     */
    public void startSpotDelay(int delay) {
        qrCodeReaderView.startSpot(delay);
        startCamera();
    }

    /**
     * 停止识别
     */
    public void stopSpot() {
        cancelProcessDataTask();

        qrCodeReaderView.stopSpot();
    }

    /**
     * 停止识别，并且隐藏扫描框
     */
    public void stopSpotAndHiddenRect() {
        stopSpot();
        hiddenScanRect();
    }

    /**
     * 显示扫描框，并且延迟1.5秒后开始识别
     */
    public void startSpotAndShowRect() {
        startSpot();
        showScanRect();
    }

    /**
     * 打开闪光灯
     */
    public void openFlashlight() {
        qrCodeReaderView.setTorchEnabled(true);
    }

    /**
     * 关闭散光灯
     */
    public void closeFlashlight() {
        qrCodeReaderView.setTorchEnabled(false);
    }

    /**
     * 销毁二维码扫描控件
     */
    public void onDestroy() {
        stopCamera();
        mHandler = null;
        mDelegate = null;
    }

    /**
     * 取消数据处理任务
     */
    protected void cancelProcessDataTask() {
        if (mProcessDataTask != null) {
            mProcessDataTask.cancelTask();
            mProcessDataTask = null;
        }
    }

    /**
     * 切换成扫描条码样式
     */
    public void changeToScanBarcodeStyle() {
        if (!mScanBoxView.getIsBarcode()) {
            mScanBoxView.setIsBarcode(true);
        }
    }

    /**
     * 切换成扫描二维码样式
     */
    public void changeToScanQRCodeStyle() {
        if (mScanBoxView.getIsBarcode()) {
            mScanBoxView.setIsBarcode(false);
        }
    }

    /**
     * 当前是否为条码扫描样式
     *
     * @return
     */
    public boolean getIsScanBarcodeStyle() {
        return mScanBoxView.getIsBarcode();
    }

    @Override
    public void onQRCodeRead(String result, PointF[] points) {
        if (mDelegate != null && !TextUtils.isEmpty(result)) {
            try {
                mDelegate.onScanQRCodeSuccess(result);
            } catch (Exception e) {
            }
        }
    }

    public interface Delegate {
        /**
         * 处理扫描结果
         *
         * @param result
         */
        void onScanQRCodeSuccess(String result);

        /**
         * 处理打开相机出错
         */
        void onScanQRCodeOpenCameraError();
    }
}