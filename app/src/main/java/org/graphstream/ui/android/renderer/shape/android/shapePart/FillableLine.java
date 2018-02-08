package org.graphstream.ui.android.renderer.shape.android.shapePart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceView;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.shape.android.ShapePaint;
import org.graphstream.ui.android.renderer.shape.android.ShapeStroke;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.util.ColorManager;

public class FillableLine {
	ShapeStroke fillStroke = null ;
	double theFillPercent = 0.0 ;
	int theFillColor = -1 ;
	boolean plainFast = false ;
  
	public void fill(SurfaceView view, Canvas g, Paint p, double width, double dynColor, Form shape) {
		if(fillStroke != null) {
		    if(plainFast) {
				p.setColor(theFillColor);
				shape.drawByPoints(view, g, p, false);
		    }
		    else {
				p.setColor(theFillColor);
				fillStroke.stroke((float)width, shape, -1).changeStrokeProperties(g, p);

				shape.drawByPoints(view, g, p, false);
			}
		}
	}
 
	public void fill(SurfaceView view, Canvas g, Paint p, double width, Form shape) { fill(view, g, p, width, theFillPercent, shape); }
 
	public void configureFillableLineForGroup(Backend bck, Style style, DefaultCamera2D camera, double theSize) {
		fillStroke = ShapeStroke.strokeForConnectorFill( style );
  	  	plainFast = (style.getSizeMode() == StyleConstants.SizeMode.NORMAL); 
		theFillColor = ColorManager.getFillColor(style, 0);
		bck.getPaint().setColor(theFillColor);
		if(fillStroke != null) {
			fillStroke.stroke((float)theSize, null, theFillColor).changeStrokeProperties(bck.graphics2D(), bck.getPaint());
		}
	}

	public void configureFillableLineForElement( Style style, DefaultCamera2D camera, GraphicElement element ) {
		theFillPercent = 0 ;
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
            if ( element.getAttribute( "ui.color" ) instanceof Integer ) {
                theFillColor = ((int)element.getAttribute( "ui.color" ));
                theFillPercent = 0;
            }
	  	  	else if ( element.getAttribute( "ui.color" ) instanceof Number ) {
  	  			theFillPercent = (float)((Number)element.getAttribute( "ui.color" ));
  	  			theFillColor = ShapePaint.interpolateColor( style.getFillColors(), theFillPercent ) ;
  	  		}
  	  		else {
  	  			theFillPercent = 0f;
  	  			theFillColor = ColorManager.getFillColor(style, 0);
  	  		}
	  	  	
  	  		plainFast = false;
  	  	}
	}
}