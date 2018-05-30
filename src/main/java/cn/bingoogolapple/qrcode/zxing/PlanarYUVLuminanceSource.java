//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.bingoogolapple.qrcode.zxing;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.google.zxing.LuminanceSource;

public final class PlanarYUVLuminanceSource extends LuminanceSource {
    private final int dataHeight;
    private final int dataWidth;
    private final int left;
    private final int top;
    private final byte[] yuvData;

    public PlanarYUVLuminanceSource(byte[] var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
        super(var6, var7);
        if(var4 + var6 <= var2 && var5 + var7 <= var3) {
            this.yuvData = var1;
            this.dataWidth = var2;
            this.dataHeight = var3;
            this.left = var4;
            this.top = var5;
            if(var8) {
                this.reverseHorizontal(var6, var7);
            }

        } else {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
    }

    private void reverseHorizontal(int var1, int var2) {
        byte[] var3 = this.yuvData;
        int var4 = 0;

        for(int var5 = this.top * this.dataWidth + this.left; var4 < var2; var5 += this.dataWidth) {
            int var6 = var5 + var1 / 2;
            int var7 = var5;

            for(int var8 = -1 + var5 + var1; var7 < var6; --var8) {
                byte var9 = var3[var7];
                var3[var7] = var3[var8];
                var3[var8] = var9;
                ++var7;
            }

            ++var4;
        }

    }

    public byte[] getMatrix() {
        int var1 = this.getWidth();
        int var2 = this.getHeight();
        byte[] var4;
        if(var1 == this.dataWidth && var2 == this.dataHeight) {
            var4 = this.yuvData;
        } else {
            int var3 = var1 * var2;
            var4 = new byte[var3];
            int var5 = this.top * this.dataWidth + this.left;
            if(var1 == this.dataWidth) {
                System.arraycopy(this.yuvData, var5, var4, 0, var3);
                return var4;
            }

            byte[] var6 = this.yuvData;

            for(int var7 = 0; var7 < var2; ++var7) {
                System.arraycopy(var6, var5, var4, var7 * var1, var1);
                var5 += this.dataWidth;
            }
        }

        return var4;
    }

    public byte[] getRow(int var1, byte[] var2) {
        if(var1 >= 0 && var1 < this.getHeight()) {
            int var3 = this.getWidth();
            if(var2 == null || var2.length < var3) {
                var2 = new byte[var3];
            }

            int var4 = (var1 + this.top) * this.dataWidth + this.left;
            System.arraycopy(this.yuvData, var4, var2, 0, var3);
            return var2;
        } else {
            throw new IllegalArgumentException("Requested row is outside the image: " + var1);
        }
    }

    public boolean isCropSupported() {
        return true;
    }

    public Bitmap renderCroppedGreyscaleBitmap() {
        int var1 = this.getWidth();
        int var2 = this.getHeight();
        int[] var3 = new int[var1 * var2];
        byte[] var4 = this.yuvData;
        int var5 = this.top * this.dataWidth + this.left;

        for(int var6 = 0; var6 < var2; ++var6) {
            int var8 = var6 * var1;

            for(int var9 = 0; var9 < var1; ++var9) {
                int var10 = 255 & var4[var5 + var9];
                var3[var8 + var9] = -16777216 | 65793 * var10;
            }

            var5 += this.dataWidth;
        }

        Bitmap var7 = Bitmap.createBitmap(var1, var2, Config.ARGB_8888);
        var7.setPixels(var3, 0, var1, 0, 0, var1, var2);
        return var7;
    }
}
