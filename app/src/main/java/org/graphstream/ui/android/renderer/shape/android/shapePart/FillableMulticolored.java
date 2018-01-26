package org.graphstream.ui.android.renderer.shape.android.shapePart;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.util.ColorManager;

public class FillableMulticolored {
	public int[] fillColors = null ;
	
	public void configureFillableMultiColoredForGroup(Style style, DefaultCamera2D camera) {
		int count = style.getFillColorCount();
		
		if(fillColors == null || fillColors.length != count) {
			fillColors = new int[count];
		}
		
		for (int i = 0 ; i < count ; i++) {
			fillColors[i] = ColorManager.getFillColor(style, i);
		}
	}
}