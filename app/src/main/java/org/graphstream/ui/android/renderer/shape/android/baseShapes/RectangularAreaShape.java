package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;

public abstract class RectangularAreaShape extends AreaShape {
	private Form.Rectangle2D theShape = new Form.Rectangle2D();
	
	@Override
	public void make(Backend backend, DefaultCamera2D camera) {
		float w = (float)area.theSize.x;
		float h = (float)area.theSize.y;
		
		((Form)theShape()).setFrame((float)area.theCenter.x-w/2, (float)area.theCenter.y-h/2, w, h);
	}

	@Override
	public void makeShadow(Backend backend, DefaultCamera2D camera) {
		float x = (float)(area.theCenter.x + shadowable.theShadowOff.x);
		float y = (float)(area.theCenter.y + shadowable.theShadowOff.y);
		float w = (float)(area.theSize.x + shadowable.theShadowWidth.x * 2);
		float h = (float)(area.theSize.y + shadowable.theShadowWidth.y * 2);
		
		((Form)theShape()).setFrame(x-w/2, y-h/2, w, h);
	}

	@Override
	public void render(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skel) {
		make(bck, camera);
 		fillable.fill(bck.drawingSurface(), bck.graphics2D(), bck.getPaint(), theShape(), camera);
 		strokable.stroke(bck.drawingSurface(), bck.graphics2D(), bck.getPaint(), theShape());
 		decorArea(bck, camera, skel.iconAndText, element, theShape());
	}

	@Override
	public void renderShadow(Backend bck, DefaultCamera2D camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
 		shadowable.cast(bck.drawingSurface(), bck.graphics2D(), bck.getPaint(), theShape());
	}
	
	public Form theShape() {
		return theShape;
	}
}