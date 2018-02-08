package org.graphstream.ui.android;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceView;

import java.util.Stack;
import java.util.logging.Logger;

import org.graphstream.ui.android.renderer.GraphBackgroundRenderer;
import org.graphstream.ui.android.renderer.shape.Shape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.AngleShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.BlobShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.CubicCurveShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.FreePlaneEdgeShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.HorizontalSquareEdgeShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.LSquareEdgeShape;
import org.graphstream.ui.android.renderer.shape.android.advancedShapes.PieChartShape;
import org.graphstream.ui.android.renderer.shape.android.arrowShapes.ArrowOnEdge;
import org.graphstream.ui.android.renderer.shape.android.arrowShapes.CircleOnEdge;
import org.graphstream.ui.android.renderer.shape.android.arrowShapes.DiamondOnEdge;
import org.graphstream.ui.android.renderer.shape.android.arrowShapes.ImageOnEdge;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.LineShape;
import org.graphstream.ui.android.renderer.shape.android.baseShapes.PolylineEdgeShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.CircleShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.CrossShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.DiamondShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.FreePlaneNodeShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.PolygonShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.RoundedSquareShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.SquareShape;
import org.graphstream.ui.android.renderer.shape.android.basicShapes.TriangleShape;
import org.graphstream.ui.android.renderer.shape.android.spriteShapes.OrientableSquareShape;
import org.graphstream.ui.android.renderer.shape.android.spriteShapes.SpriteArrowShape;
import org.graphstream.ui.android.renderer.shape.android.spriteShapes.SpriteFlowShape;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.StyleGroup;

public class BackendJ2D implements Backend {
	
	private SurfaceView surface ;
	private Canvas g2 ;
	private Paint p ;
	private Stack<Matrix> matrixStack ;
	private Matrix Tx ;
	private Matrix xT ;
	private Matrix currentTransform ; // save the current transform because we can't use g.getMatrix() if we want to support hardware acceleration
	private Point3 dummyPoint = new Point3(-1, -1);
	
	public BackendJ2D() {
		surface = null ;
		g2 = null ;
        currentTransform = null ;
		p = null ;
		matrixStack = new Stack<>() ;
		Tx = null ;
		xT = null ;
	}
	
	public void setCanvas(Canvas g) {
		this.g2 = g ;
	}
		
	@Override
	public void open(SurfaceView drawingSurface) {
		surface = drawingSurface ;
	}

	@Override
	public void close() {
		surface = null ;
	}

	@Override
	public void prepareNewFrame(Canvas g) {
		this.g2 = g ;
		this.p = new Paint();

 		currentTransform = getMatrixSurface();

        Tx = getMatrixSurface();
        matrixStack.clear();
	}

	public Matrix getMatrix(){
        return currentTransform;
    }

    /**
     * Setup the transformation with translation for the status bar of android
     */
    public Matrix getMatrixSurface(){
        Matrix origin = surface.getMatrix();

        if (!surface.isHardwareAccelerated()) {
            int statusBarHeight = 0;
            int resourceId = surface.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = surface.getResources().getDimensionPixelSize(resourceId);
            }
            origin.postTranslate(0, statusBarHeight);
        }
        return origin ;
    }

    public void setMatrix(Matrix m) {
        currentTransform = m ;
	    g2.setMatrix(m);
    }

	@Override
	public Point3 transform(double x, double y, double z) {
		float[] p = {(float)x, (float)y};
		Tx.mapPoints(p, p);

		dummyPoint = new Point3(p[0], p[1], 0);

		return dummyPoint;
	}

	@Override
	public Point3 inverseTransform(double x, double y, double z) {
        float[] p = {(float)x, (float)y};
        xT.mapPoints(p, p);

        dummyPoint = new Point3(p[0], p[1], 0);
        return dummyPoint;
	}

	@Override
	public Point3 transform(Point3 p) {
        float[] point = {(float)p.x, (float)p.y};
        Tx.mapPoints(point, point);

        dummyPoint = new Point3(point[0], point[1], 0);
        p.set(dummyPoint.x, dummyPoint.y, 0);
        return dummyPoint;
	}

	@Override
	public Point3 inverseTransform(Point3 p) {
        float[] point = {(float)p.x, (float)p.y};
        xT.mapPoints(point, point);

        dummyPoint = new Point3(point[0], point[1], 0);
        p.set(dummyPoint.x, dummyPoint.y, 0);
        return dummyPoint;
	}

	@Override
	public void pushTransform() {
        matrixStack.push(new Matrix(getMatrix()));
	}

	@Override
	public void beginTransform() {}

	@Override
	public void setIdentity() { Tx = new Matrix(); }

	@Override
	public void translate(double tx, double ty, double tz) {
	    Matrix m = getMatrix() ;
	    m.preTranslate((float)tx, (float)ty);
	    setMatrix(m);
	}

	@Override
	public void rotate(double angle, double ax, double ay, double az) {
	    Matrix m = getMatrix() ;
	    m.preRotate((float)angle);
        setMatrix(m);
	}

	@Override
	public void scale(double sx, double sy, double sz) {
        Matrix m = getMatrix() ;
        m.preScale((float)sx, (float)sy);
        setMatrix(m);
	}

	@Override
	public void endTransform() {
		Tx = getMatrix();
		computeInverse();
	}

	private void computeInverse() {
	    xT = new Matrix(Tx);
		if( !xT.invert(xT)) {
            Log.e("Error", "Cannot inverse matrix. " + this.getClass().getSimpleName());
        }
	}

	@Override
	public void popTransform() {
		assert(!matrixStack.isEmpty());
		setMatrix(matrixStack.pop());
	}

	@Override
	public void setAntialias(Boolean on) {
        p.setAntiAlias(on);
	}

	@Override
	public void setQuality(Boolean on) {
	}

	@Override
	public Canvas graphics2D() {
		return g2 ;
	}

    @Override
    public Paint getPaint() { return p ;}

    @Override
	public Shape chooseNodeShape(Shape oldShape, StyleGroup group) {
		switch (group.getShape()) {
			case CIRCLE:
				if(oldShape instanceof CircleShape)
					return oldShape ;
				else 								
					return new CircleShape();
			case BOX:
				if(oldShape instanceof SquareShape)
					return oldShape ;
				else
					return new SquareShape();
			case ROUNDED_BOX:
				if(oldShape instanceof RoundedSquareShape)
					return oldShape ;
				else
					return new RoundedSquareShape();
			case DIAMOND:
				if(oldShape instanceof DiamondShape)
					return oldShape ;
				else
					return new DiamondShape();
			case TRIANGLE:
				if(oldShape instanceof TriangleShape)
					return oldShape ;
				else
					return new TriangleShape();
			case CROSS:
				if(oldShape instanceof CrossShape)
					return oldShape ;
				else
					return new CrossShape();
			case FREEPLANE:
				if(oldShape instanceof FreePlaneNodeShape)	
					return oldShape ;
				else
					return new FreePlaneNodeShape();
			case PIE_CHART:
				if(oldShape instanceof PieChartShape)
					return oldShape ;
				else
					return new PieChartShape();
			case POLYGON:
				if(oldShape instanceof PolygonShape)	
					return oldShape ;
				else
					return new PolygonShape();
			//------
			case TEXT_BOX:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-box shape not yet implemented **");  
				return new SquareShape();
			case TEXT_PARAGRAPH:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-para shape not yet implemented **");  
				return new SquareShape();
			case TEXT_CIRCLE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-circle shape not yet implemented **");  
				return new CircleShape();
			case TEXT_DIAMOND:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-diamond shape not yet implemented **");  
				return new CircleShape();
			case ARROW:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY arrow shape not yet implemented **");  
				return new CircleShape();
			case IMAGES:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY images shape not yet implemented **");  
				return new SquareShape();
			//----
			case JCOMPONENT:
				throw new RuntimeException("Jcomponent should have its own renderer");
			default:
				throw new RuntimeException(group.getShape().toString()+" shape cannot be set for nodes");
		}
	}

	@Override
	public Shape chooseEdgeShape(Shape oldShape, StyleGroup group) {
		switch(group.getShape()) {
			case LINE:
				if(oldShape instanceof LineShape)
					return oldShape ;
				else 								
					return new LineShape();
			case ANGLE:
				if(oldShape instanceof AngleShape)
					return oldShape ;
				else 								
					return new AngleShape();
			case BLOB:
				if(oldShape instanceof BlobShape)
					return oldShape ;
				else 								
					return new BlobShape();
			case CUBIC_CURVE:
				if(oldShape instanceof CubicCurveShape)
					return oldShape ;
				else 								
					return new CubicCurveShape();
			case FREEPLANE:
				if(oldShape instanceof FreePlaneEdgeShape)
					return oldShape ;
				else 								
					return new FreePlaneEdgeShape();
			case POLYLINE:
				if(oldShape instanceof PolylineEdgeShape)
					return oldShape ;
				else 								
					return new PolylineEdgeShape();
			case SQUARELINE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY square-line shape not yet implemented **");
				return new HorizontalSquareEdgeShape() ;
			case LSQUARELINE:
				if(oldShape instanceof LSquareEdgeShape)
					return oldShape ;
				else 								
					return new LSquareEdgeShape();
			case HSQUARELINE:
				if(oldShape instanceof HorizontalSquareEdgeShape)	
					return oldShape ;
				else 								
					return new HorizontalSquareEdgeShape();
			case VSQUARELINE:
				Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY square-line shape not yet implemented **");
				return new HorizontalSquareEdgeShape() ;
			default:
				throw new RuntimeException(group.getShape()+" shape cannot be set for edges");
		}
	}

	@Override
	public Shape chooseEdgeArrowShape(Shape oldShape, StyleGroup group) {
		switch (group.getArrowShape()) {
			case NONE:
				return null ;
			case ARROW:
				if(oldShape instanceof ArrowOnEdge)
					return oldShape ;
				else 								
					return new ArrowOnEdge();
			case CIRCLE:
				if(oldShape instanceof CircleOnEdge)
					return oldShape ;
				else 								
					return new CircleOnEdge();
			case DIAMOND:
				if(oldShape instanceof DiamondOnEdge)
					return oldShape ;
				else 								
					return new DiamondOnEdge();
			case IMAGE:
				if(oldShape instanceof ImageOnEdge)
					return oldShape ;
				else 								
					return new ImageOnEdge();
			default:
				throw new RuntimeException(group.getArrowShape().toString()+" shape cannot be set for edge arrows");
		}
	}

	@Override
	public Shape chooseSpriteShape(Shape oldShape, StyleGroup group) {
		switch (group.getShape()) {
		case CIRCLE:
			if(oldShape instanceof CircleShape)	
				return oldShape ;
			else 								
				return new CircleShape();
		case BOX:
			if(oldShape instanceof OrientableSquareShape)
				return oldShape ;
			else
				return new OrientableSquareShape();
		case ROUNDED_BOX:
			if(oldShape instanceof RoundedSquareShape)	
				return oldShape ;
			else
				return new RoundedSquareShape();
		case DIAMOND:
			if(oldShape instanceof DiamondShape)	
				return oldShape ;
			else
				return new DiamondShape();
		case TRIANGLE:
			if(oldShape instanceof TriangleShape)
				return oldShape ;
			else
				return new TriangleShape();
		case CROSS:
			if(oldShape instanceof CrossShape)
				return oldShape ;
			else
				return new CrossShape();
		case ARROW:
			if(oldShape instanceof SpriteArrowShape)
				return oldShape ;
			else
				return new SpriteArrowShape();
		case FLOW:
			if(oldShape instanceof SpriteFlowShape)
				return oldShape ;
			else
				return new SpriteFlowShape();
		case PIE_CHART:
			if(oldShape instanceof PieChartShape)
				return oldShape ;
			else
				return new PieChartShape();
		case POLYGON:
			if(oldShape instanceof PolygonShape)	
				return oldShape ;
			else
				return new PolygonShape();
		//------
		case TEXT_BOX:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-box shape not yet implemented **");  
			return new SquareShape();
		case TEXT_PARAGRAPH:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-para shape not yet implemented **");  
			return new SquareShape();
		case TEXT_CIRCLE:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-circle shape not yet implemented **");  
			return new CircleShape();
		case TEXT_DIAMOND:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY text-diamond shape not yet implemented **");  
			return new CircleShape();
		case IMAGES:
			Logger.getLogger(this.getClass().getSimpleName()).warning("** SORRY images shape not yet implemented **");  
			return new SquareShape();
		//----
		case JCOMPONENT:
			throw new RuntimeException("Jcomponent should have its own renderer");
		default:
			throw new RuntimeException(group.getShape().toString()+" shape cannot be set for nodes");
		}
	}

	@Override
	public GraphBackgroundRenderer chooseGraphBackgroundRenderer() {
		return null ;
	}

	@Override
	public SurfaceView drawingSurface() {
		return surface ;
	}

}
