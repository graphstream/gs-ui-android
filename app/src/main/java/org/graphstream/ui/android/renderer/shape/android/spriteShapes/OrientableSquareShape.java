package org.graphstream.ui.android.renderer.shape.android.spriteShapes;

import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Rectangle2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.OrientableRectangularAreaShape;

public class OrientableSquareShape extends OrientableRectangularAreaShape {
	private Rectangle2D theShape = new Rectangle2D();
	
	public Form theShape() {
		return theShape;
	}
}