package org.graphstream.ui.android.renderer.shape.android.shapePart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import org.graphstream.ui.android.util.Background;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint.ShapeAreaPaint;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint.ShapeColorPaint;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint.ShapePlainColorPaint;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;

public class Fillable {
	
	/** The fill paint. */
	private ShapePaint fillPaint = null;
 
	/** Value in [0..1] for dyn-colors. */
	private double theFillPercent = 0.0;
	
	private int theFillColor = -1;
	
	private boolean plainFast = false;
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
	 * @param shape The shape to fill. */
	public void fill(Canvas g, Paint p, double dynColor, int optColor, Form shape, DefaultCamera2D camera) {
		if(plainFast) {
			p.setColor(theFillColor);
			shape.drawByPoints(g, p, false);
	    } 
		else {
			if ( fillPaint instanceof ShapeAreaPaint ) {	
				Background background = ((ShapeAreaPaint)fillPaint).paint(shape, camera.getMetrics().ratioPx2Gu) ;
				background.applyPaint(g, p);
                if (!background.isImage())
					shape.drawByPoints(g, p, false);
				background.removePaint(p);
			}
			else if (fillPaint instanceof ShapeColorPaint ) {
				Background background = ((ShapeColorPaint)fillPaint).paint(dynColor, optColor);
				background.applyPaint(g, p);
				if (!background.isImage())
					shape.drawByPoints(g, p, false);
                background.removePaint(p);
			}
	    }
	}
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param shape The shape to fill. */
 	public void fill(Canvas g, Paint p, Form shape, DefaultCamera2D camera) {
 		fill( g, p, theFillPercent, theFillColor, shape, camera );
 	}

    /** Configure all static parts needed to fill the shape. */
 	public void configureFillableForGroup(Backend bck, Style style, DefaultCamera2D camera ) {
 		fillPaint = ShapePaint.apply(style);
 
 		if(fillPaint instanceof ShapePlainColorPaint) {
 			ShapePlainColorPaint paint = (ShapePlainColorPaint)fillPaint;
 			
 		    plainFast = true;
 		    theFillColor = paint.color;
			bck.getPaint().setColor(theFillColor);
 		    // We prepare to accelerate the filling process if we know the color is not dynamic
 		    // and is plain: no need to change the paint at each new position for the shape.
 		} 
 		else {
 		    plainFast = false;
 		}
 	}
 	
    /** Configure the dynamic parts needed to fill the shape. */
  	public void configureFillableForElement( Style style, DefaultCamera2D camera, GraphicElement element ) {
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
            if ( element.getAttribute( "ui.color" ) instanceof Integer) {
                theFillColor = ((int)element.getAttribute( "ui.color" ));
                theFillPercent = 0;
            }
  	  	    else if ( element.getAttribute( "ui.color" ) instanceof Number ) {
                theFillPercent = (float) ((Number) element.getAttribute("ui.color"));
                theFillColor = -1;
            }
  	  		else {
  	  			theFillPercent = 0; 
  	  			theFillColor = -1;
  	  		}
  	  	}
  	  	else {
  	  		theFillPercent = 0;
  	  	}
  	}
}