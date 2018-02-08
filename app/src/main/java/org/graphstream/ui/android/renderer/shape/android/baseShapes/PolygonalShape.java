package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Path2D;

public abstract class PolygonalShape extends AreaShape {
	protected Path2D theShape = null;
 
 	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
 		makeShadow(bck, camera);
 		shadowable.cast(bck.drawingSurface(), bck.graphics2D(), bck.getPaint(), theShape());
 	}
  
 	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
 		Canvas g = bck.graphics2D();
		Paint p = bck.getPaint();
 		make(bck, camera);
 		fillable.fill(bck.drawingSurface(), g, p, theShape(), camera);
 		strokable.stroke(bck.drawingSurface(), g, p, theShape());
 		decorArea(bck, camera, skel.iconAndText, element, theShape());
 	}
 	
 	public Path2D theShape() {
 		return theShape ;
 	}
}