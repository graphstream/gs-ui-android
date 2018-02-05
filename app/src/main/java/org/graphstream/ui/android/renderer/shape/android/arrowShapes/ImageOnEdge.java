package org.graphstream.ui.android.renderer.shape.android.arrowShapes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.AreaOnConnectorShape;
import org.graphstream.ui.android.util.CubicCurve;
import org.graphstream.ui.android.util.ImageCache;
import org.graphstream.ui.android.util.ShapeUtil;
import org.graphstream.ui.android.util.AttributeUtils.Tuple;

public class ImageOnEdge extends AreaOnConnectorShape {
	Bitmap image = null;
	Point3 p = null ;
	double angle = 0.0;
	
	@Override
	public void configureForGroup(Backend bck, Style style, DefaultCamera2D camera) {
		super.configureForGroup(bck, style, camera);
	}
	
	@Override
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, DefaultCamera2D camera) {
		super.configureForElement(bck, element, skel, camera);
		
		String url = element.getStyle().getArrowImage();
				
		if( url.equals( "dynamic" ) ) {
			if( element.hasLabel( "ui.arrow-image" ) )
				url = element.getLabel( "ui.arrow-image" ).toString();
			else
				url = null;
		}
				
		if( url != null ) {
			try {
                image = ImageCache.loadImage(url);
            }
            catch (Exception e) {
                if (image == null) {
                    image = ImageCache.dummyImage();
                }
            }
		}
	}
	
	@Override
	public void make(Backend backend, DefaultCamera2D camera) {
		make( false, camera );
	}
	@Override
	public void makeShadow(Backend backend, DefaultCamera2D camera) {
		make( true, camera );
	}
	
	private void make(boolean forShadow, DefaultCamera2D camera) {
		if( theConnector.skel.isCurve() )
			makeOnCurve( forShadow, camera );
		else
			makeOnLine( forShadow, camera );	
	}
	
	private void makeOnCurve(boolean forShadow, DefaultCamera2D camera) {
		Tuple<Point2, Double> tuple = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera );
		Point2 p1 = tuple.x ;
		double t = tuple.y ;
		
		Style style  = theEdge.getStyle();
		Point3 p2  = CubicCurve.eval( theConnector.fromPos(), theConnector.byPos1(), theConnector.byPos2(), theConnector.toPos(), t-0.1f );
		Vector2 dir = new Vector2( p1.x - p2.x, p1.y - p2.y );
		
		dir.normalize();
		
		double iw = camera.getMetrics().lengthToGu( image.getWidth(), Units.PX ) / 2;
		double x  = p1.x - ( dir.x() * iw );
		double y  = p1.y - ( dir.y() * iw );
		
		if( forShadow ) {
			x += shadowable.theShadowOff.x;
			y += shadowable.theShadowOff.y;
		}
		
		p = camera.transformGuToPx( x, y, 0 );
		angle = Math.acos( dir.dotProduct( 1, 0 ) );
		
		if( dir.y() > 0 )
			angle = ( Math.PI - angle );
	}
	
	private void makeOnLine(boolean forShadow, DefaultCamera2D camera) {
		double off = ShapeUtil.evalTargetRadius2D( theEdge, camera );
		
		Vector2 theDirection = new Vector2(
				theConnector.toPos().x - theConnector.fromPos().x,
				theConnector.toPos().y - theConnector.fromPos().y );
					
		theDirection.normalize();
				
		double iw = camera.getMetrics().lengthToGu( image.getWidth(), Units.PX ) / 2;
		double x  = theCenter.x - ( theDirection.x() * ( off + iw ) );
		double y  = theCenter.y - ( theDirection.y() * ( off + iw ) );
				
		if( forShadow ) {
			x += shadowable.theShadowOff.x;
			y += shadowable.theShadowOff.y;
		}	
				
		p = camera.transformGuToPx( x, y, 0 );	// Pass to pixels, the image will be drawn in pixels.
		angle = Math.acos( theDirection.dotProduct( 1, 0 ) );
				
		if( theDirection.y() > 0 )			// The angle is always computed for acute angles
			angle = ( Math.PI - angle );
	}

	@Override
	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skeleton) {
		Canvas g = bck.graphics2D();

 		make( false, camera );
 		// stroke( g, theShape )
 		// fill( g, theShape, camera )
 		
 		if( image != null ) {
 			Matrix Tx = g.getMatrix();
			g.setMatrix( new Matrix() );													// An identity matrix.

			g.translate( (float)p.x, (float)p.y );											// 3. Position the image at its position in the graph.
 			g.rotate( (float)angle );														// 2. Rotate the image from its center.
 			g.translate( (float)-image.getWidth()/2, (float)-image.getHeight()/2 );	// 1. Position in center of the image.

 			g.drawBitmap(image, 0, 0, new Paint());								// Paint the image.
 			g.setMatrix( Tx );															// Restore the original transform
 		}
	}

	@Override
	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skeleton) {
		//make( true, camera );
		//shadowable.cast( g, theShape );
	}	
}