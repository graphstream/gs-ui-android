package org.graphstream.ui.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;

public class ImageCache {

	protected static HashMap<String, Bitmap> imageCache = new HashMap<>();
	
	protected static Bitmap dummy = null ;

	public static Context context = null ;

	public static void init(Context c) {
	    context = c ;
    }
	
	public static Bitmap loadImage(String drawable) {
		return loadImage(drawable, false);
	}

	public static Bitmap loadImage(String drawable, boolean forceTryReload) {
		if (imageCache.get(drawable) == null) {
            Bitmap image = null ;
		    try {
		        int img = Integer.parseInt(drawable);
                image = BitmapFactory.decodeResource(context.getResources(), img);
            }
            catch (Exception e) { e.printStackTrace(); }

			
			return image ;
		}
		else {
			if(imageCache.get(drawable) == dummy && forceTryReload) {
				imageCache.remove(drawable) ;
				return loadImage(drawable);
			}
			else
				return imageCache.get(drawable);
		}
	}

	public static Bitmap dummyImage() {
		return dummy ;
	}
}
