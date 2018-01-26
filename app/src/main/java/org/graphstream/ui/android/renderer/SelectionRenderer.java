package org.graphstream.ui.android.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.graphstream.ui.android.Backend;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.Selection;
import org.graphstream.ui.android.util.Stroke;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.camera.DefaultCamera2D;

public class SelectionRenderer {
	
	private Selection selection;
	
	protected Form.Rectangle2D shape = new Form.Rectangle2D();

	protected int linesColorQ  = ColorManager.getColor(new org.graphstream.ui.graphicGraph.stylesheet.Color(  0,   0,   0, 64));
	protected int fillColor    = ColorManager.getColor(new org.graphstream.ui.graphicGraph.stylesheet.Color( 50,  50, 200, 32));
			
	public SelectionRenderer(Selection selection, GraphicGraph graph) {
		this.selection = selection ;
	}
	
	public void render(Backend bck, DefaultCamera2D camera, int panelWidth, int panelHeight) {
	    // XXX
	    // TODO make this an abstract class whose implementation are create by the back-end
	    // XXX
		if(selection.isActive() && selection.x1() != selection.x2() && selection.y1() != selection.y2()) {
			Canvas g = bck.graphics2D();

			float x1 = selection.x1();
			float y1 = selection.y1();
			float x2 = selection.x2();
			float y2 = selection.y2();
			float t = 0.0f;
			
			if(x1 > x2) { t = x1; x1 = x2; x2 = t; }
			if(y1 > y2) { t = y1; y1 = y2; y2 = t; }
			
			ColorManager.paint.setColor(linesColorQ);

			Stroke stroke = new Stroke(1);
			stroke.changeStrokeProperties(g);

			ColorManager.paint.setStyle(Paint.Style.STROKE);
			g.drawLine(0, y1, panelWidth, y1, ColorManager.paint);
			g.drawLine(0, y2, panelWidth, y2, ColorManager.paint);
			g.drawLine(x1, 0, x1, panelHeight, ColorManager.paint);
			g.drawLine(x2, 0, x2, panelHeight, ColorManager.paint);
			
			shape.setFrame(x1, y1, x2-x1, y2-y1);

            ColorManager.paint.setColor(fillColor);
            shape.drawByPoints(g, false);

            ColorManager.paint.setColor(linesColorQ);
            shape.drawByPoints(g, true);
		}
	}
}
