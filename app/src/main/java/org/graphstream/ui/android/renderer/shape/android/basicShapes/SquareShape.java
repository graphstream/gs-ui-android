package org.graphstream.ui.android.renderer.shape.android.basicShapes;

import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Rectangle2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.RectangularAreaShape;

public class SquareShape extends RectangularAreaShape {
	private Rectangle2D theShape = new Rectangle2D();
	
	public Form theShape() {
		return theShape;
	}
}