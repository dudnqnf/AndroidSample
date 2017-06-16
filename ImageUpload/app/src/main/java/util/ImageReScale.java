package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;

public class ImageReScale {

	public ImageReScale(){
		
	}
	/**
	  *
	  * @param context
	  *            application context
	  * @param imgFilePath
	  *            bitmap file path
	  * @return Bitmap
	  * @throws IOException
	  */
	public Bitmap loadBackgroundBitmap(Context context, String imgFilePath) throws Exception, OutOfMemoryError {
//	    if (!FileUtil.exists(imgFilePath)) {
//	        throw new FileNotFoundException("background-image file not found : " + imgFilePath);
//	    }

	    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int displayWidth = display.getWidth();
	    int displayHeight = display.getHeight();
	 
	    // ??��?��?��?? ??��?��????? ??��?��??�? 구�?????.
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inPreferredConfig = Config.RGB_565;
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imgFilePath, options);
	 
	    // ???�? ??��?��????? �???? 근�???????? ??��?��????? 리�?��????? ??��?��??�? 구�?????.
	    // 리�?��????��?? ??��?��????? �????�? �??????????. (??��?��?? ?????��?? �??????????�? ??????.) 
	    float widthScale = options.outWidth / displayWidth;
	    float heightScale = options.outHeight / displayHeight;
	    float scale = widthScale > heightScale ? widthScale : heightScale;
	            
	    if(scale >= 8) {
	        options.inSampleSize = 8;
	    } else if(scale >= 6) {
	        options.inSampleSize = 6;
	    } else if(scale >= 4) {
	        options.inSampleSize = 4;
	    } else if(scale >= 2) {
	        options.inSampleSize = 2;
	    } else {
	        options.inSampleSize = 1;
	    }
	    options.inJustDecodeBounds = false;
	 
	    return BitmapFactory.decodeFile(imgFilePath, options);
	}

	
}
