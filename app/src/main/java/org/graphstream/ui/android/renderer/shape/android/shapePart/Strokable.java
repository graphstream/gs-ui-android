package org.graphstream.ui.android.renderer.shape.android.shapePart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapeStroke;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.util.ColorManager;

public class Strokable {
    /** The stroke color. */
	public int strokeColor = -1 ;
	
	public int fillColor = -1 ; // Used by doubleStroke

	/** The stroke. */
	public ShapeStroke theStroke = null ;
 	
	/** The stroke width. */
	public float theStrokeWidth = 0.0f ;

 	/** Paint the stroke of the shape. */
	public void stroke(SurfaceView view, Canvas g, Paint p, Form shape ) {
		if(theStroke != null) {
			p.setColor(strokeColor);
			theStroke.stroke(theStrokeWidth, shape, fillColor).changeStrokeProperties(g, p);
			shape.drawByPoints(view, g, p, true);
		}
	}
	
 	/** Configure all the static parts needed to stroke the shape. */
 	public void configureStrokableForGroup( Style style, DefaultCamera2D camera ) {
		theStrokeWidth = (float)camera.getMetrics().lengthToGu( style.getStrokeWidth() );
		
		/*if( strokeColor == null )*/ strokeColor = ShapeStroke.strokeColor( style );
		/*if( theStroke   == null )*/ theStroke   = ShapeStroke.strokeForArea( style );
		
		fillColor = ColorManager.getFillColor(style, 0);
 	}
}