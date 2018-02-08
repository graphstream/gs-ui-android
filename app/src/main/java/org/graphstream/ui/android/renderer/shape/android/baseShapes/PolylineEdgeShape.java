package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.ShowCubics;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Path2D;

public class PolylineEdgeShape extends LineConnectorShape {
	public ShowCubics showCubics ;
	protected Path2D theShape = new Path2D(0, false);

	public PolylineEdgeShape() {
		this.showCubics = new ShowCubics();
	}

	@Override
	public void make(Backend backend, DefaultCamera2D camera) {
		int n = skel.size();
		
		theShape = new Path2D(n+2, false);
		theShape.moveTo(skel.apply(0).x, skel.apply(0).y);
		
		for(int i = 0 ; i < n ; i++) {
			theShape.lineTo(skel.apply(i).x, skel.apply(i).y);
		}		
	}

	@Override
	public void makeShadow(Backend backend, DefaultCamera2D camera) {}

	@Override
	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skeleton) {
		Canvas g = bck.graphics2D();
		Paint p = bck.getPaint();
		make(bck, camera);
		strokableLine.stroke(bck.drawingSurface(), g, p, theShape);
		fillableLine.fill(bck.drawingSurface(), g, p, theSize, theShape);
		decorable.decorConnector(bck, camera, skel.iconAndText, element, theShape);
	}

	@Override
	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
 		shadowableLine.cast(bck.drawingSurface(), bck.graphics2D(), bck.getPaint(), theShape);
	}

}