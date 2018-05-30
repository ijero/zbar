package cn.bingoogolapple.qrcode.zxing;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;

import cn.bingoogolapple.qrcode.core.QRCodeView;


//import com.google.zxing.PlanarYUVLuminanceSource;

public class ZXingView extends QRCodeView {
    private static final String TAG = ZXingView.class.getSimpleName();
    //    private final ImageView child;
    private QRCodeReader mMultiFormatReader;

    public ZXingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ZXingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMultiFormatReader();
//        child = new ImageView(context);
//        child.setBackgroundColor(Color.RED);
//        child.setLayoutParams(new RelativeLayout.LayoutParams(300, 300));
//        addView(child);
    }

    private void initMultiFormatReader() {
        mMultiFormatReader = new QRCodeReader();
//        mMultiFormatReader.setHints(QRCodeDecoder.HINTS);
    }


    @Override
    public Result processData(byte[] data, int width, final int height) {
        final PlanarYUVLuminanceSource source;
        final Rect rect = mScanBoxView.getScanBoxAreaRect(width);
//        System.out.println("开始识别,width:"+
//                rect.width()+";height:" +rect.height() +
//                ";left:"+rect.left+
//                ";right:"+rect.right+
//                ";top:"+rect.top+
//                ";bottom:"
//                +rect.bottom+";图片大小width:"+width+";height:"+height
//        );
//        if (rect != null) {
//            source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
//        } else {
//            source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
//        }
        source = new PlanarYUVLuminanceSource(data, width, height, rect.top,
                height - rect.right, rect.height(), rect.width(), false);

//        post(new Runnable() {
//            @Override
//            public void run() {
////                int t = height - rect.right;
////                child.layout(rect.top, t, rect.top + rect.height(), t+rect.width());
//                child.setImageBitmap(source.renderCroppedGreyscaleBitmap());
//            }
//        });

        final HybridBinarizer hybBin = new HybridBinarizer(source);
        final BinaryBitmap bitmap = new BinaryBitmap(hybBin);

        try {
            return mMultiFormatReader.decode(bitmap, new HashMap<DecodeHintType, Object>());
        } catch (ChecksumException e) {
            Log.d(TAG, "ChecksumException", e);
        } catch (NotFoundException e) {
            Log.d(TAG, "No QR Code found");
        } catch (FormatException e) {
            Log.d(TAG, "FormatException", e);
        } finally {
            mMultiFormatReader.reset();
        }

        return null;
    }
}