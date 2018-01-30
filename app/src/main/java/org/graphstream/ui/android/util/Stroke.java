package org.graphstream.ui.android.util;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

public class Stroke {
	protected float width ;
	private Float dashes;
	private Paint.Cap cap ;
	
	public Stroke() {
		this(1, null, Paint.Cap.SQUARE);
	}
	
	public Stroke(float width) {
		this(width, null, Paint.Cap.SQUARE);
	}
	
	public Stroke(float width, Float dashes, Paint.Cap cap) {
		this.width = width ;
		this.dashes = dashes ;
		this.cap = cap ;
	}
	
	public void changeStrokeProperties(Canvas c, Paint p) {
		p.setStrokeWidth(width);

		if (dashes == null) {
			p.setPathEffect(null);
		}
		else {
			ColorManager.dashes = dashes ;
			p.setPathEffect(new DashPathEffect(new float[]{dashes, dashes}, 0));
		}
		p.setStrokeCap(cap);
	}
}
