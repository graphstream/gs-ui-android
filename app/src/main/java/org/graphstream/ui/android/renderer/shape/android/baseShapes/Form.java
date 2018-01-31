package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.Stroke;

import java.util.ArrayList;

/**
 * The interface and the classes that implement it used by all shapes in renderer.shape.javafx 
 * for create and display a javafx.scene.shape.Shape in a Canvas.
 */
public interface Form  {	
	public void drawByPoints(Canvas c, Paint p, boolean stroke) ;
	public void setFrame(double x, double y, double w, double h);
	public RectF getBounds() ; // Left, Top, Right, Bottom (x1, y1, x2, y2)
	// used by the Double Stroke (see ShapeStroke.class)
	public String getIdForm();
	public Object getPath();
	
	public class Rectangle2D implements Form {
        private float x ;
        private float y ;
        private float w ;
        private float h ;
        private float arcHeight = 0.0f ;
        private float arcWidth = 0.0f ;

		private Stroke strokeBig ;
		private Stroke strokeSmall ;
		private boolean doubleStroke = false ;
		private int fillColor = -1 ;

        float[][] path = new float[2][2];
        RectF bounds ;

		public void setFrame(double x, double y, double w, double h) {
			/*path[0][0] = x ; path[0][1] = y ;
			path[1][0] = x+w ; path[1][1] = y ;
			path[2][0] = x+w ; path[2][1] = y-h ;
			path[3][0] = x ; path[3][1] = y-h ;*/
			
			path[0][0] = (float)x ; path[0][1] = (float)y ;
			path[1][0] = (float)w; path[1][1] = (float)y ;
			
			this.x = (float)x ;
			this.y = (float)y ;
			this.w = (float)w ;
			this.h = (float)h ;
			this.bounds = new RectF((float)x, (float)y, (float)(w+x), (float)(h+y));
		}
		
		public void drawByPoints(Canvas c, Paint p, boolean stroke) {
			if (doubleStroke) {
                p.setStyle(Paint.Style.STROKE);

				strokeBig.changeStrokeProperties(c, p);
				c.drawRoundRect(bounds, arcWidth, arcHeight, p);
				strokeSmall.changeStrokeProperties(c, p);

                p.setColor(fillColor);
			}

            if (stroke)
                p.setStyle(Paint.Style.STROKE);
            else
                p.setStyle(Paint.Style.FILL);

            c.drawRoundRect(bounds, arcWidth, arcHeight, p);
		}

		@Override
		public String getIdForm() {
			return "Rect";
		}

		@Override
		public float[][] getPath() {
			return path;
		}

		public void setRoundRect(double x, double y, double w, double h, double r, double r2) {
			setFrame(x, y, w, h);
			arcWidth = (float)r;
			arcHeight = (float)r2;
		}

		@Override
		public RectF getBounds() {
			return bounds;
		}

		public void setDoubleStroke(Stroke strokeBig, Stroke strokeSmall, int fillColor) {
			if ( fillColor != -1 ) {
				this.strokeSmall = strokeSmall;
				this.strokeBig = strokeBig;
				this.doubleStroke = true ;
				this.fillColor = fillColor ;
			}
		}
	}
	
	public class Path2D extends Path implements Form {
		private boolean fillable;
        private RectF bounds = new RectF();

		public Path2D(int nbElement, boolean fillable) {
			super();
			this.fillable = fillable ;
		}
		
		public void moveTo(double x, double y) {
			super.moveTo((float)x, (float)y);
        }
		
		public void lineTo(double x, double y) {
            super.lineTo((float)x, (float)y);

            computeBounds(this.bounds, true);
		}
		
		public void curveTo(double xc1, double yc1, double xc2, double yc2, double x1, double y1) {
            super.cubicTo((float)xc1, (float)yc1, (float)xc2, (float)yc2, (float)x1, (float)y1);

            computeBounds(this.bounds, true);
		}
		
		public void quadTo(double cx, double cy, double x, double y) {
            super.quadTo((float)cx, (float)cy, (float)x, (float)y);

            computeBounds(this.bounds, true);
		}
		
		public void closePath() {
            super.close();

            computeBounds(this.bounds, true);
		}
		
		public void drawByPoints(Canvas c, Paint p, boolean stroke) {
            if (!stroke && fillable)
                p.setStyle(Paint.Style.FILL);
            else
                p.setStyle(Paint.Style.STROKE);

            c.drawPath(this, p);
        }
		
		@Override
		public String getIdForm() {
			return "Path";
		}

		@Override
		public Object getPath() {
			return null;
		}

		@Override
		public void setFrame(double x, double y, double w, double h) {
			throw new RuntimeException("SetFrame with Path2D ?");
		}
		
		@Override
		public RectF getBounds() {
			return bounds;
		}
	}

	public class CubicCurve2D extends Path implements Form {
        private RectF bounds  = new RectF();

		public CubicCurve2D() {
			super();
		}
		
		public CubicCurve2D(double xFrom, double yFrom, double xCtrl1, double yCtrl1, double xCtrl2, double yCtrl2, double xTo, double yTo) {
			super();
            super.moveTo((float)xFrom, (float)yFrom);
            super.cubicTo((float)xCtrl1, (float)yCtrl1, (float)xCtrl2, (float)yCtrl2, (float)xTo, (float)yTo);
            computeBounds(this.bounds, true);
		}

		@Override
		public void drawByPoints(Canvas c, Paint p, boolean stroke) {
            p.setStyle(Paint.Style.STROKE);

            c.drawPath(this, p);
		}

		@Override
		public String getIdForm() {
			return "CubicCurve";
		}

		@Override
		public double[][] getPath() {
			return null;
		}
		
		@Override
		public void setFrame(double x, double y, double w, double h) {
			throw new RuntimeException("SetFrame with CubicCurve ?");
		}
		
		@Override
		public RectF getBounds() {
			return bounds;
		}
	}
	
	public class Line2D implements Form {
		private float[][] path = new float[2][2];
        private RectF bounds ;

        private float x1 ;
        private float y1 ;
        private float x2 ;
        private float y2 ;

		public Line2D() {
			super();
		}

		public Line2D(double x, double y, double x2, double y2) {
			path[0][0] = (float)x ; path[0][1] = (float)y ;
			path[1][0] = (float)x2; path[1][1] = (float)y2 ;

			this.x1 = (float)x ;
            this.y1 = (float)y ;
            this.x2 = (float)x2 ;
            this.y2 = (float)y2 ;

            bounds = new RectF(this.x1, this.y1, this.x2, this.y2);
		}

		@Override
		public void drawByPoints(Canvas g, Paint p, boolean stroke) {
            g.drawLine(x1, y1, x2, y2, p);
		}

		@Override
		public String getIdForm() {
			return "Line";
		}

		@Override
		public float[][] getPath() {
			return path;
		}

		@Override
		public void setFrame(double x, double y, double x2, double y2) {
			path[0][0] = (float)x ; path[0][1] = (float)y ;
			path[1][0] = (float)x2; path[1][1] = (float)y2 ;

            this.x1 = (float)x ;
            this.y1 = (float)y ;
            this.x2 = (float)x2 ;
            this.y2 = (float)y2 ;

            bounds = new RectF(this.x1, this.y1, this.x2, this.y2);
		}
		
		@Override
		public RectF getBounds() {
			return bounds;
		}
	}

	public class Arc2D extends Path implements Form {
        private RectF bounds = new RectF();

		public void setArcByCenter(double x, double y, double rad, double angleSt, double angleLen) {
            super.arcTo((float)(x-rad), (float)(y-rad), (float)rad*2, (float)rad*2, (float)angleSt, (float)angleLen, true);

            computeBounds(this.bounds, true);
		}
		
		@Override
		public void drawByPoints(Canvas c, Paint p, boolean stroke) {
			if (stroke)
                p.setStyle(Paint.Style.STROKE);
            else
                p.setStyle(Paint.Style.FILL);

            c.drawPath(this, p);
        }
		
		@Override
		public String getIdForm() {
			return "Arc";
		}

		@Override
		public Object getPath() {
			return null;
		}
		
		@Override
		public void setFrame(double x, double y, double w, double h) {
			throw new RuntimeException("SetFrame with Arc2D ?");
		}
		
		@Override
		public RectF getBounds() { return bounds; }
	}

	public class Ellipse2D implements Form {
		private Stroke strokeBig ;
		private Stroke strokeSmall ;
		private boolean doubleStroke = false ;
		private int fillColor = -1 ;

        private RectF bounds ;
		private float[][] path = new float[2][2];
		
		public void setFrameFromCenter(double centerX, double centerY, double cornerX, double cornerY) {
            /*bounds = new RectF((float)(centerX-(cornerX-centerX)), (float)(centerY-(cornerY-centerY)), (float)(cornerX-centerX)*2, (float)(cornerY-centerY)*2);
			
			path[0][0] = (float)(centerX-(cornerX-centerX)) ; path[0][1] = (float)(centerY-(cornerY-centerY)) ;
			path[1][0] = (float)(cornerX-centerX)*2 ; path[1][1] = (float)(cornerY-centerY)*2;
			*/
            bounds = new RectF((float)centerX, (float)centerY, (float)(centerX+cornerX), (float)(centerY+cornerY));

            path[0][0] = (float)centerX ; path[0][1] = (float)centerY;
            path[1][0] = (float)cornerX; path[1][1] = (float)cornerY;

            doubleStroke = false ;
		}
		
		public void setFrame(double x, double y, double cornerX, double cornerY) {
            /*bounds = new RectF((float)x, (float)y, (float)cornerX, (float)cornerY);

            path[0][0] = (float)x ; path[0][1] = (float)y ;
			path[1][0] = (float)cornerX ; path[1][1] = (float)cornerY ;*/
            bounds = new RectF((float)x, (float)y, (float)(x+cornerX), (float)(y+cornerY));

            path[0][0] = (float)x ; path[0][1] = (float)y;
            path[1][0] = (float)(x+cornerX); path[1][1] = (float)(y+cornerY);

            doubleStroke = false ;
		}
		
		@Override
		public void drawByPoints(Canvas c, Paint p, boolean stroke) {
			if (doubleStroke) {
				strokeBig.changeStrokeProperties(c, p);
                p.setStyle(Paint.Style.STROKE);

                c.drawOval(path[0][0], path[0][1], path[1][0], path[1][1], p);
				strokeSmall.changeStrokeProperties(c, p);
				
                p.setColor(fillColor);
			}
			
			if(stroke) {
                p.setStyle(Paint.Style.STROKE);
			}
			else {
                p.setStyle(Paint.Style.FILL);
            }

            c.drawOval(path[0][0], path[0][1], path[1][0], path[1][1], p);

        }

		@Override
		public String getIdForm() {
			return "Ellipse";
		}

		@Override
		public Object getPath() {
			return path;
		}
		
		@Override
		public RectF getBounds() { return bounds; }

		public void setDoubleStroke(Stroke strokeBig, Stroke strokeSmall, int fillColor) {
			if ( fillColor != -1 ) {
				this.strokeSmall = strokeSmall;
				this.strokeBig = strokeBig;
				this.doubleStroke = true ;
				this.fillColor = fillColor ;
			}
		}
	}
}
