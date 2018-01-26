package org.graphstream.ui.android.renderer.shape.android.basicShapes;

import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Ellipse2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.RectangularAreaShape;

public class CircleShape extends RectangularAreaShape {
	private Form theShape = new Ellipse2D();
	
	public Form theShape() {
		return theShape;
	}
}