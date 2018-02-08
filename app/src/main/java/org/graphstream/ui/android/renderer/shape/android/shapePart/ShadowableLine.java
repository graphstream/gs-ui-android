package org.graphstream.ui.android.renderer.shape.android.shapePart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;

import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapeStroke;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.util.ColorManager;

public class ShadowableLine {
	/** The shadow paint. */
	public ShapeStroke shadowStroke = null;

	/** Additional width of a shadow (added to the shape size). */
	public float theShadowWidth = 0.0f;
 
	/** Offset of the shadow according to the shape center. */
	public Point2 theShadowOff = new Point2();

	public int theShadowColor = -1 ;
 
	/** Sety the shadow width added to the shape width. */
	public void shadowWidth( float width ) { theShadowWidth = width; }
 
 	/** Set the shadow offset according to the shape. */ 
	public void shadowOffset( double xoff, double yoff ) { theShadowOff.set( xoff, yoff ); }
	
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
   	public void cast(SurfaceView view, Canvas g, Paint p, Form shape ) {
   		p.setColor(theShadowColor);
   		shadowStroke.stroke( theShadowWidth , shape, -1 ).changeStrokeProperties(g, p);
   	  	shape.drawByPoints(view, g, p, true);
   	}
 
    /** Configure all the static parts needed to cast the shadow of the shape. */
 	public void configureShadowableLineForGroup( Style style, DefaultCamera2D camera) {
 		theShadowWidth = (float)(camera.getMetrics().lengthToGu( style.getSize(), 0 ) +
 			camera.getMetrics().lengthToGu( style.getShadowWidth() ) +
 			camera.getMetrics().lengthToGu( style.getStrokeWidth() ) );
 		theShadowOff.x = camera.getMetrics().lengthToGu( style.getShadowOffset(), 0 );
 		theShadowOff.y = theShadowOff.x ;
 		if( style.getShadowOffset().size() > 1 ) 
 			camera.getMetrics().lengthToGu( style.getShadowOffset(), 1 ) ;
  	  	theShadowColor = ColorManager.getShadowColor(style, 0);
 		shadowStroke   = ShapeStroke.strokeForConnectorFill( style );
 	}	
}