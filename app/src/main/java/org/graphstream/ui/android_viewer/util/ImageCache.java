package org.graphstream.ui.android_viewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import org.graphstream.ui.android.util.ColorManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ImageCache {

	protected static HashMap<String, Bitmap> imageCache = new HashMap<>();
	
	protected static Bitmap dummy = null ;
		
	public void init() {
		Bitmap img = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);

		Canvas c = new Canvas(img);
		ColorManager.paint.setColor(Color.RED);
		c.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1, ColorManager.paint);
		c.drawLine(0, 0, img.getWidth()-1, img.getHeight()-1, ColorManager.paint);
		c.drawLine(0, img.getHeight()-1, img.getWidth()-1, 0, ColorManager.paint);
		
		dummy = img ;
	}
	
	
	public static Bitmap loadImage(String fileNameOrUrl) {
		return loadImage(fileNameOrUrl, false);
	}


	public static Bitmap loadImage(String fileNameOrUrl, boolean forceTryReload) {
		if (imageCache.get(fileNameOrUrl) == null) {
		    Bitmap image = null;
		    try {
                URL url = new URL(fileNameOrUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                image = BitmapFactory.decodeStream(input);

            }
            catch (Exception e) {e.printStackTrace();}

            return image;
		}
		else {
			if(imageCache.get(fileNameOrUrl) == dummy && forceTryReload) {
				imageCache.remove(fileNameOrUrl) ;
				return loadImage(fileNameOrUrl);
			}
			else
				return imageCache.get(fileNameOrUrl);
		}
	}
	
	public static Bitmap dummyImage() {
		return dummy ;
	}
}
