package grl.com.configuratoin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class FileUtils {

    public static String getDataDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        String dir = null;
        if (fileDir != null) {
            dir = fileDir.getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        dir += "/mtc/data/";
        fileDir = new File(dir);
        fileDir.mkdirs();
        return dir;
    }

    public static String getSendDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        String dir = null;
        if (fileDir != null) {
            dir = fileDir.getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        dir += "/mtc/send/";
        fileDir = new File(dir);
        fileDir.mkdirs();
        return dir;
    }

    public static String getRecvDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        String dir = null;
        if (fileDir != null) {
            dir = fileDir.getAbsolutePath();
        } else {
            dir = context.getFilesDir().getAbsolutePath();
        }
        dir += "/mtc/recv/";
        fileDir = new File(dir);
        fileDir.mkdirs();
        return dir;
    }

    public static String getSnapshotPath(Context context) {
        return getRecvDir(context) + getCurrentTimeString() + ".jpeg";
    }

    public static String getAudioPath(Context context) {
        return getDataDir(context) + getCurrentTimeString() + ".wav";
    }

    public static String getVideoPath(Context context) {
        return getDataDir(context) + getCurrentTimeString() + ".avi";
    }

    private static String getCurrentTimeString() {
        Time time = new Time();
        time.set(System.currentTimeMillis());
        return String.format(Locale.getDefault(),
                "%04d-%02d-%02d-%02d-%02d-%02d", time.year, time.month,
                time.monthDay, time.hour, time.minute, time.second);
    }

    public static void deleteFile(String path) {
        try {
            File myFile = new File(path);
            if (myFile.exists())
                myFile.delete();
        } catch (Exception ex) {

        }
    }


    public static void fileCopy(String srcPath, String destPath) {
        try {
            if (srcPath == null || srcPath.isEmpty())
                return;
            File myFile = new File(destPath);
            if(myFile.exists())
                myFile.delete();

            File src = new File(srcPath);
            File dst = new File(destPath);
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception ex) {

        }

    }

    public static void saveBitmap(Bitmap bmp, String path) {
        try {
            File myCaptureFile = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Bitmap readBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static String getExtension(String path) {
        int dot = path.lastIndexOf("/");
        int endDot = path.lastIndexOf(".");
        if ((dot >-1) && (endDot < (path.length() - 1))) {
            String extension = path.substring(endDot + 1);
            return extension;
        }
        return "";
    }
    public static String getFileName(String path) {
        int dot = path.lastIndexOf("/");
        int endDot = path.lastIndexOf(".");
        if ((dot >-1) && (endDot < (path.length() - 1))) {
            String extension = path.substring(endDot + 1);
            String fileName = path.substring(dot + 1, endDot);
            return fileName;
        }
        return "";
    }

    // Get Thumb Bitmap
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap getImageThumb(String imagePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = computeSampleSize(options, -1, Constant.IMAGE_HEIGHT * Constant.IMAGE_HEIGHT);
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return bmp;
    }

    public static Bitmap getVideoThumb(String videoPath) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 392, 512,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
