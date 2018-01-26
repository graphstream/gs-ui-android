package org.graphstream.ui.android.renderer.shape.android.spriteShapes;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Ellipse2D;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form.Rectangle2D;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.Stroke;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class ShapeStroke {
	public abstract Stroke stroke(float width, Form shape, int fillColor) ;

	public static ShapeStroke strokeForArea(Style style) {
		switch (style.getStrokeMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DOTS: return new DotsShapeStroke();
			case DASHES: return new DashesShapeStroke();
			case DOUBLE: return new DoubleShapeStroke();
			default: return null ;
		}
	}
	
	public static ShapeStroke strokeForConnectorFill(Style style) {
		switch (style.getFillMode()) {
			case PLAIN: return new PlainShapeStroke();
			case DYN_PLAIN: return new PlainShapeStroke();
			case NONE: return null	; // Gracefully handled by the drawing part.
			default: return new PlainShapeStroke() ;
		}
	}
	
	public ShapeStroke strokeForConnectorStroke(Style style) {
		return strokeForArea(style);
	}
	
	public static int strokeColor(Style style) {
		if( style.getStrokeMode() != org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode.NONE ) {
			return ColorManager.getStrokeColor(style, 0);
		} 
		else {
			return -1;
		}
	}
}

class PlainShapeStroke extends ShapeStroke {
	private float oldWidth = 0 ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(float width, Form shape, int fillColor) {
		return stroke(width);
	}
	
	public Stroke stroke(float width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) 
				oldStroke = new Stroke(width);
			return oldStroke;
		} 
		else {
			oldWidth  = width;
			oldStroke = new Stroke(width);
			return oldStroke;
		}
	}
}

class DotsShapeStroke extends ShapeStroke {
	private float oldWidth = 0.0f ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(float width, Form shape, int fillColor) {
		return stroke(width);
	}
	
	public Stroke stroke(float width) {
		if( width == oldWidth ) {
			if( oldStroke == null ) {
				oldStroke = new Stroke( width, width, Paint.Cap.BUTT);
			}
			return oldStroke;
		} else {
			oldWidth = width;
			oldStroke = new Stroke( width, width, Paint.Cap.BUTT);
			return oldStroke;
		}
	}
}

class DashesShapeStroke extends ShapeStroke {
	private float oldWidth = 0.0f ;
	private Stroke oldStroke = null ;
	
	@Override
	public Stroke stroke(float width, Form shape, int fillColor) {
		return stroke(width);
	}
	
	public Stroke stroke(float width) {
		if( width == oldWidth ) {
			if( oldStroke == null ){
				oldStroke = new Stroke( width, (3*width), Paint.Cap.BUTT);
			}
			return oldStroke ;
		} else {
			oldWidth = width ;
			oldStroke = new Stroke( width, (3*width), Paint.Cap.BUTT);
			return oldStroke ;
		}
	}	
}

class DoubleShapeStroke extends ShapeStroke {
	
	@Override
	public Stroke stroke(float width, Form shape, int fillColor) {
		return new CompositeStroke( new Stroke(width*3),  new Stroke(width), width, shape, fillColor);
	}
	
	class CompositeStroke extends Stroke {
		private Stroke stroke1 ;
		private Stroke stroke2 ;
		private Form form ;
		private int fillColor ;
		
		public CompositeStroke(Stroke stroke1, Stroke stroke2, float w, Form form, int fillColor) {
			super(w);
			this.stroke1 = stroke1 ;
			this.stroke2 = stroke2 ;
			this.form = form ;
			this.fillColor = fillColor ;
		}
		
		@Override
		public void changeStrokeProperties(Canvas g) {
			// if form is null ==> debug in FillableLine 
			
			if (form.getIdForm().equals("Rect")) {
				((Rectangle2D)form).setDoubleStroke(stroke1, stroke2, fillColor);
			}
			else if (form.getIdForm().equals("Path")) {
				System.err.println("DoubleStroke for Path not implemented yet");
				stroke2.changeStrokeProperties(g);
			}
			else if (form.getIdForm().equals("CubicCurve")) {
				System.err.println("DoubleStroke for CubicCurve not implemented yet");
				stroke2.changeStrokeProperties(g);
			}
			else if (form.getIdForm().equals("Line")) {
				float[][] path = (float[][]) form.getPath();
				float x1 = path[0][0];
				float y1 = path[0][1];
				float x2 = path[1][0];
				float y2 = path[1][1];

				stroke2.changeStrokeProperties(g);
				double angle = Math.toDegrees(Math.atan2(y2-y1, x2-x1) - Math.atan2(1, 1));
				if ( angle >= 90 || angle <= -180 || (angle >= -90 && angle < 0)) {
					y1 += width*3 ;
					y2 += width*3 ;
				}
				else {
					x1 += width*3 ;
					x2 += width*3 ;
				}
				ColorManager.paint.setStyle(Paint.Style.STROKE);
				g.drawLine(x1, y1, x2, y2, ColorManager.paint);
			}
			else if (form.getIdForm().equals("Ellipse")) {
				((Ellipse2D)form).setDoubleStroke(stroke1, stroke2, fillColor);
			}
		}
	}
}

