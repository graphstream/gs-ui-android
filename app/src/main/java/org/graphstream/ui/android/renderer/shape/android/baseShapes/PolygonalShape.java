package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import android.graphics.Canvas;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Path2D;

public abstract class PolygonalShape extends AreaShape {
	protected Path2D theShape = null;
 
 	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
 		makeShadow(bck, camera);
 		shadowable.cast(bck.graphics2D(), theShape());
 	}
  
 	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
 		Canvas g = bck.graphics2D();
 		make(bck, camera);
 		fillable.fill(g, theShape(), camera);
 		strokable.stroke(g, theShape());
 		decorArea(bck, camera, skel.iconAndText, element, theShape());
 	}
 	
 	public Path2D theShape() {
 		return theShape ;
 	}
}