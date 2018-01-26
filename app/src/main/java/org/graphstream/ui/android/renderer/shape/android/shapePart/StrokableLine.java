package org.graphstream.ui.android.renderer.shape.android.shapePart;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapeStroke;

public class StrokableLine extends Strokable {
 	public void configureStrokableForGroup( Style style, DefaultCamera2D camera ) {
		theStrokeWidth = (float)(camera.getMetrics().lengthToGu( style.getStrokeWidth() ) + camera.getMetrics().lengthToGu( style.getSize(), 0 ));
		strokeColor = ShapeStroke.strokeColor( style );
		theStroke = ShapeStroke.strokeForArea( style );
 	}
 	
 	public void configureStrokableLineForGroup( Style style, DefaultCamera2D camera ) { 
 		configureStrokableForGroup( style, camera ) ;
 	}
}