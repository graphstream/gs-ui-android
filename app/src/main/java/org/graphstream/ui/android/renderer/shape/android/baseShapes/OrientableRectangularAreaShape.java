package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.Orientable;

public class OrientableRectangularAreaShape extends RectangularAreaShape {
	Orientable orientable ;
	
	Point3 p = null;
	double angle = 0.0;
	double w = 0.0;
	double h = 0.0;
	boolean oriented = false;
	
	public OrientableRectangularAreaShape() {
		orientable = new Orientable();
	}
	
	public void configureForGroup(Backend bck, Style style, DefaultCamera2D camera) {
		super.configureForGroup(bck, style, camera);
		orientable.configureOrientableForGroup(style, camera);
		oriented = (style.getSpriteOrientation() != StyleConstants.SpriteOrientation.NONE);
	}
	
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, DefaultCamera2D camera) {
		super.configureForElement(bck, element, skel, camera);
		orientable.configureOrientableForElement(camera, (GraphicSprite) element /* Check This XXX TODO !*/);
	}
	
	public void make(Backend backend, DefaultCamera2D camera) {make(backend, false, camera);}
	
 	public void makeShadow(Backend backend, DefaultCamera2D camera) {make(backend, true, camera);}

	private void make(Backend bck, boolean forShadow, DefaultCamera2D camera) {
		if (oriented) {
			Vector2 theDirection = new Vector2(
					orientable.target.x - area.theCenter.x,
					orientable.target.y - area.theCenter.y );
			
			theDirection.normalize();
		
			double x = area.theCenter.x;
			double y = area.theCenter.y;
		
			if( forShadow ) {
				x += shadowable.theShadowOff.x;
				y += shadowable.theShadowOff.y;
			}
		
			p = camera.transformGuToPx(x, y, 0); // Pass to pixels, the image will be drawn in pixels.
			angle = Math.acos(theDirection.dotProduct( 1, 0 ));
		
			if( theDirection.y() > 0 )			// The angle is always computed for acute angles
				angle = ( Math.PI - angle );
	
			w = camera.getMetrics().lengthToPx(area.theSize.x, Units.GU);
			h = camera.getMetrics().lengthToPx(area.theSize.y, Units.GU);
			((Form) theShape()).setFrame(0, 0, w, h);
		} else {
			if (forShadow)
				super.makeShadow(bck, camera);
			else
				super.make(bck, camera);
		}
	}
 	
	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
		make(bck, false, camera);
		
		Canvas g = bck.graphics2D();
		Paint paint = bck.getPaint();
 		
 		if (oriented) {
	 		Matrix Tx = bck.getMatrix();
			bck.setMatrix(new Matrix());						// An identity matrix.
	 		g.translate((float) p.x, (float)p.y );			// 3. Position the image at its position in the graph.
	 		g.rotate( (float)angle );						// 2. Rotate the image from its center.
			g.translate( (float)-w/2, (float)-h/2 );	// 1. Position in center of the image.

			strokable.stroke(bck.drawingSurface(), g, paint, theShape());
	 		fillable.fill(bck.drawingSurface(), g, paint, theShape(), camera);
			bck.setMatrix(Tx);							// Restore the original transform
	 		((Form) theShape()).setFrame(area.theCenter.x-w/2, area.theCenter.y-h/2, w, h);
	 		decorArea(bck, camera, skel.iconAndText, element, theShape());
 		}
 		else {
 			super.render(bck, camera, element, skel);
 		}
	}
	
	
 	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
 		make(bck, true, camera);
 		
 		Canvas g = bck.graphics2D();
 		
 		if (oriented) {
			Matrix Tx = bck.getMatrix();
			bck.setMatrix(new Matrix());						// An identity matrix.
	 		g.translate( (float)p.x, (float)p.y );				// 3. Position the image at its position in the graph.
	 		g.rotate( (float)angle );							// 2. Rotate the image from its center.
	 		g.translate( (float)-w/2, (float)-h/2 );		// 1. Position in center of the image.

 			shadowable.cast(bck.drawingSurface(), g, bck.getPaint(), theShape());
			bck.setMatrix( Tx );								// Restore the original transform
 		} else {
 			super.renderShadow(bck, camera, element, skel);
 		}
 	}
}
