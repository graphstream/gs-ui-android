package org.graphstream.ui.android.renderer.shape.android.basicShapes;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Line2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Rectangle2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.RectangularAreaShape;

public class FreePlaneNodeShape extends RectangularAreaShape {
	Rectangle2D theShape = new Rectangle2D();
	Line2D theLineShape = new Line2D();
	
	@Override
	public void make(Backend backend, DefaultCamera2D camera) {
		double w = area.theSize.x ;
		double h = area.theSize.y ;
		double x = area.theCenter.x ;
		double y = area.theCenter.y ;
		
		((Rectangle2D) theShape()).setFrame( x-w/2, y-h/2, w, h );
		
		w -= strokable.theStrokeWidth;
		
		theLineShape.setFrame( x-w/2, y-h/2, x+w/2, y-h/2 );
	}
	
	@Override
	public void makeShadow(Backend backend, DefaultCamera2D camera) {
		double x = area.theCenter.x + shadowable.theShadowOff.x;
		double y = area.theCenter.y + shadowable.theShadowOff.y;
		double w = area.theSize.x + shadowable.theShadowWidth.x * 2;
		double h = area.theSize.y + shadowable.theShadowWidth.y * 2;
		
		((Rectangle2D) theShape()).setFrame( x-w/2, y-h/2, w, h );
		theLineShape.setFrame( x-w/2, y-h/2, x+w/2, y-h/2 );	
	}
	
	@Override
	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
		Canvas g = bck.graphics2D();
		Paint p = bck.getPaint();
		make(bck, camera);
		fillable.fill(g, p, theShape(), camera);
		strokable.stroke(g, p, theLineShape);
		decorArea(bck, camera, skel.iconAndText, element, theShape());
	}
	
	@Override
	public Form theShape() {
		return theShape;
	} 
	
}
