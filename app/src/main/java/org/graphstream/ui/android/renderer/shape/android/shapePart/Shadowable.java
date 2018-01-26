package org.graphstream.ui.android.renderer.shape.android.shapePart;

import android.graphics.Canvas;

import org.graphstream.ui.android.util.Background;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint.ShapeAreaPaint;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint.ShapeColorPaint;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;

public class Shadowable {
	/** The shadow paint. */
	public ShapePaint shadowPaint = null ;

	/** Additional width of a shadow (added to the shape size). */
	public Point2 theShadowWidth = new Point2();
 
	/** Offset of the shadow according to the shape center. */
	public Point2 theShadowOff = new Point2();

	/** Set the shadow width added to the shape width. */
	public void shadowWidth( double width, double height ) { theShadowWidth.set( width, height ); }
 
 	/** Set the shadow offset according to the shape. */ 
	public void shadowOffset( double xoff, double yoff ) { theShadowOff.set( xoff, yoff ); }
 
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
	public void cast(Canvas g, Form shape) {
		if ( shadowPaint instanceof ShapeAreaPaint ) {
			Background p = ((ShapeAreaPaint)shadowPaint).paint( shape, 1 ) ;
			p.applyPaint(g);
			shape.drawByPoints(g, false);
		}
		else if ( shadowPaint instanceof ShapeColorPaint ) {
			Background p = ((ShapeColorPaint)shadowPaint).paint( 0, -1 ) ;
			p.applyPaint(g);
			shape.drawByPoints(g, false);
		}
		else {
			System.out.println("no shadow !!!");
		}
   	}
 
    /** Configure all the static parts needed to cast the shadow of the shape. */
 	public void configureShadowableForGroup( Style style, DefaultCamera2D camera ) {
 		theShadowWidth.x = camera.getMetrics().lengthToGu( style.getShadowWidth() );
 		theShadowWidth.y = theShadowWidth.x;
 		theShadowOff.x   = camera.getMetrics().lengthToGu( style.getShadowOffset(), 0 );
 		theShadowOff.y   = theShadowOff.x ;
 		if( style.getShadowOffset().size() > 1 ) 
 			theShadowOff.y = camera.getMetrics().lengthToGu( style.getShadowOffset(), 1 ); 
 	  
  	  	/*if( shadowPaint == null )*/ shadowPaint = ShapePaint.apply( style, true );
 	}
}