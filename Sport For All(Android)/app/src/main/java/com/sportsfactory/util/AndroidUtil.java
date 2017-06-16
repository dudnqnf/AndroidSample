package com.sportsfactory.util;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

public class AndroidUtil {
	
	 public static void setImagePit(ImageView iv) {
		 
		final int WIDTH = 0;
		final int HEIGHT = 1;
			
         float[] value = new float[9];
         
         Matrix matrix = iv.getImageMatrix();
         
         matrix.getValues(value);

         int width = iv.getWidth();
         int height = iv.getHeight();

         
         Drawable d = iv.getDrawable();
         if (d == null)
                 return;
         int imageWidth = d.getIntrinsicWidth();
         int imageHeight = d.getIntrinsicHeight();
         int scaleWidth = (int) (imageWidth * value[0]);
         int scaleHeight = (int) (imageHeight * value[4]);

        
         value[2] = 0;
         value[5] = 0;

         int target = WIDTH;
         if (imageWidth < imageHeight)
                 target = HEIGHT;
         
        
         if (target == WIDTH)
                 value[0] = value[4] = (float) width / imageWidth;
         if (target == HEIGHT)
                 value[0] = value[4] = (float) height / imageHeight;

         scaleWidth = (int) (imageWidth * value[0]);
         scaleHeight = (int) (imageHeight * value[4]);
         if (scaleWidth > width)
                 value[0] = value[4] = (float) width / imageWidth;
         if (scaleHeight > height)
                 value[0] = value[4] = (float) height / imageHeight;
         
      
         scaleWidth = (int) (imageWidth * value[0]);
         scaleHeight = (int) (imageHeight * value[4]);
         if (scaleWidth < width) {
                 value[2] = (float) width / 2 - (float) scaleWidth / 2;
         }
         if (scaleHeight < height) {
                 value[5] = (float) height / 2 - (float) scaleHeight / 2;
         }

         matrix.setValues(value);
         
         
        
         iv.setImageMatrix(matrix);
	 }
	 
	 
	 
	 
	/**
	 * 요일 가져오기 (한글)
	 * @param yyyy
	 * @param mm
	 * @param dd
	 * @return
	 */
	public final static String getYoil(Context context, String yyyy, String mm, String dd)
	{
		Calendar cal= Calendar.getInstance ();
		cal.set(Integer.parseInt(yyyy), Integer.parseInt(mm)-1, Integer.parseInt(dd));
	    
	    String output = "";
	    
	    switch (cal.get(Calendar.DAY_OF_WEEK)){
	    case 1:
	    	output= "월";
	        break;
	    case 2:
	    	output= "화";
	        break;
	    case 3:
	    	output= "수";
	        break;
	    case 4:
	    	output= "목";
	        break;
	    case 5:
	    	output= "금";
	        break;
	    case 6:
	    	output= "토";
	        break;
	    case 7:
	    	output= "일";
	        break;
	    }
	    
	    return output;
	}
	
	/**
	 * 생성일자에서 플러스데이의 남은 시간을 알려준다.
	 * @param cre_dt
	 * @param plusDay
	 * @param formats
	 * @return
	 */
	public static String getDiffBetweenTimer(String cre_dt, int plusDay, SimpleDateFormat formats)
	{
	       SimpleDateFormat curr_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	      
	       Date d1 = new Date();
		   Date d2 = null;
		   Date d3 = null;
		   
		   String dateStop = null;
		   
    	    try {
				 Calendar c = Calendar.getInstance();
				 c.setTime(curr_format.parse(cre_dt));
			     c.add(Calendar.DATE, plusDay);  // number of days to add
		    
		    	 dateStop = curr_format.format(c.getTime()) ;
				d2 = curr_format.parse(dateStop);
				
			    return formats.format(d1.getTime() - d2.getTime());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		 
		   return "";
	}
	/**
	 * 일 / 시/ 분 으로 표시 
	 * @param cre_dt
	 * @param plusDay
	 * @param formats
	 * @return
	 */
	public static String getDiffBetweenTimerDMH(Context context, String cre_dt, int plusDay)
	{
	       SimpleDateFormat curr_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	      
	       Date d1 = new Date();
		   Date d2 = null;
		   Date d3 = null;
		   
		   String dateStop = null;
		   
    	    try {
				 Calendar c = Calendar.getInstance();
				 c.setTime(curr_format.parse(cre_dt));
			     c.add(Calendar.DATE, plusDay);  // number of days to add
		    
		    	 dateStop = curr_format.format(c.getTime()) ;
				d2 = curr_format.parse(dateStop);
		
				
				long diff = d2.getTime() - d1.getTime();
			    long diffSeconds = diff / 1000 % 60;
			    long diffMinutes = diff / (60 * 1000) % 60;
			    long diffHours = diff / (60 * 60 * 1000) % 24 ;
			    long diffDays = diff / (1000 * 60 * 60 * 24);
			    
			    String hh = diffHours < 10 ? "0" + diffHours : diffHours + "";
			    String mm = diffMinutes < 10 ? "0" + diffMinutes : diffMinutes + "";
			    
			    //String ss = diffSeconds < 10 ? "0" + diffSeconds : diffSeconds + "";
			    String dd = diffDays < 10 ? "0" + diffDays : diffDays + "";
			    
			    return dd +" 일" + hh +" 시" + mm +" 분";

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		 
		   return "";
	}
	 
    
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
    
    public String getRealPathFromURI(Activity activity, Uri contentUri) {
//		String []proj = {MediaStore.Images.Media.DATA};
//		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
//		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//		cursor.moveToFirst();
//		return cursor.getString(column_index);

		String result;
		Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) { // Source is Dropbox or other similar local file path
			result = contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			result = cursor.getString(idx);
			cursor.close();
		}
		return result;
	}
	
	public int exifOrientationToDegrees(int exifOrientation)
	{
	  if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
	  {
	    return 90;
	  }
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
	  {
	    return 180;
	  }
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
	  {
	    return 270;
	  }
	  return 0;
	}
	
	/**
	 * bitmap ??��?��?? ??��????? ????????? ??��?��?? ?????? ?????�기 
	 * @param selPhotoPath
	 * @param selPhoto
	 * @return
	 */
	public Bitmap bitmapRotate(String selPhotoPath, Bitmap selPhoto)
	{
		
		try {
			 ExifInterface exif = new ExifInterface(selPhotoPath);
			 int exifOrientation = exif.getAttributeInt(
					 ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					 int exifDegree = exifOrientationToDegrees(exifOrientation);
					 selPhoto = rotate(selPhoto, exifDegree);	
					 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		 
		
		return selPhoto;
	}

	public Bitmap rotate(Bitmap bitmap, int degrees)
	{
	  if(degrees != 0 && bitmap != null)
	  {
	    Matrix m = new Matrix();
	    m.setRotate(degrees, (float) bitmap.getWidth() / 2, 
	    (float) bitmap.getHeight() / 2);
	    
	    try
	    {
	      Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
	      bitmap.getWidth(), bitmap.getHeight(), m, true);
	      if(bitmap != converted)
	      {
	        bitmap.recycle();
	        bitmap = converted;
	      }
	    }
	    catch(OutOfMemoryError ex)
	    {
	      
	    }
	  }
	  return bitmap;
	}
	

	/**
	 * 이미지 blur 처리 
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

		//if(sentBitmap==null) return null;
		
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
   
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

       
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public synchronized static int GetExifOrientation(String filepath){
        int degree = 0;
        ExifInterface exif = null;
        try{
            exif = new ExifInterface(filepath);
        }catch (IOException e){
            e.printStackTrace();
        }

        if (exif != null){
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1){
                switch(orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees){
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return bitmap;
    }
    
}

