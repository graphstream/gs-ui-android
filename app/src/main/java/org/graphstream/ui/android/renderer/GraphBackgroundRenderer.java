package org.graphstream.ui.android.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Debug;
import android.util.Log;

import org.graphstream.ui.android.Backend;
import org.graphstream.ui.android.util.Background;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.ImageCache;
import org.graphstream.ui.android_viewer.util.GradientFactory;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.view.util.GraphMetrics;

/**
 * Renderer for the graph background.
 * 
 * This class is not a StyleRenderer because the graph is not a GraphicElement.
 * 
 * TODO XXX make this class an abstract one, and create several distinct back-ends.
 */
public class GraphBackgroundRenderer implements GraphicElement.SwingElementRenderer
{
	private GraphicGraph graph ;
	private StyleGroup style ;
	
	public GraphBackgroundRenderer(GraphicGraph graph, StyleGroup style) {
		this.graph = graph ;
		this.style = style ;
	}
	
	/**
     * Render a background indicating there is nothing to draw. 
	 */
	public void displayNothingToDo(Backend bck, int w, int h) {
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute." ;
		Canvas g = bck.graphics2D() ;
        Paint p = bck.getPaint();

        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);

		g.drawRect( 0, 0, w, h, p);

        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);

        g.drawLine(0, 0, w, h, p);
        g.drawLine(0, h, w, 0, p);

        double msg1length = p.measureText(msg1);
        double msg2length = p.measureText(msg2);

        int x = w / 2;
		int y = h / 2;

        p.setColor(Color.BLACK);
		g.drawText(msg1, (float) (x - msg1length / 2), (float) (y - 20), p);
		g.drawText(msg2, (float) (x - msg2length / 2), (float) (y + 20), p);
	}
	
	
	public void render(Backend bck, DefaultCamera2D camera, int w, int h) {
		if ( (camera.graphViewport() == null) && camera.getMetrics().diagonal == 0
				&& (graph.getNodeCount() == 0 && graph.getSpriteCount() == 0)) {
			displayNothingToDo(bck, w, h);
		}
		else {
			renderGraphBackground(bck, camera);
			strokeGraph(bck, camera);
		}
	}

	private void renderGraphBackground(Backend bck, DefaultCamera2D camera) {
		Canvas g = bck.graphics2D() ;
		Paint p = bck.getPaint();
		switch (graph.getStyle().getFillMode()) {
		case NONE:
			break;
		case IMAGE_TILED: fillImageTiled(g, p, camera);
			break;
		case IMAGE_SCALED: fillImageScaled(g, p, camera, 0);
			break;
		case IMAGE_SCALED_RATIO_MAX: fillImageScaled(g, p, camera, 1);
			break;
		case IMAGE_SCALED_RATIO_MIN: fillImageScaled(g, p, camera, 2);
			break;
		case GRADIENT_DIAGONAL1: fillGradient(g, p, camera);
			break;
		case GRADIENT_DIAGONAL2: fillGradient(g, p, camera);
			break;
		case GRADIENT_HORIZONTAL: fillGradient(g, p, camera);
			break;
		case GRADIENT_VERTICAL: fillGradient(g, p, camera);
			break;
		case GRADIENT_RADIAL: fillGradient(g, p, camera);
			break;
		case DYN_PLAIN: fillBackground(g, p, camera);
			break;
		default: fillBackground(g, p, camera);
			break;
		}
	}

	private void fillBackground(Canvas g, Paint p, DefaultCamera2D camera) {
		GraphMetrics metrics = camera.getMetrics();

        p.setColor(ColorManager.getFillColor(style, 0));
        p.setStyle(Paint.Style.FILL);

		g.drawRect(0, 0, (int)metrics.viewport[2], (int)metrics.viewport[3], p);
	}
	
	private void fillCanvasBackground(Canvas g, Paint p, DefaultCamera2D camera) {
		GraphMetrics metrics = camera.getMetrics();

        p.setColor(ColorManager.getCanvasColor(style, 0));
        p.setStyle(Paint.Style.FILL);
		g.drawRect( 0, 0, (int) metrics.viewport[2], (int) metrics.viewport[3], p);
	}
	

	private void fillImageTiled(Canvas g, Paint p, DefaultCamera2D camera) {
		GraphMetrics metrics = camera.getMetrics();
		double px2gu = metrics.ratioPx2Gu;
		Bitmap img = null ;
		
		img = ImageCache.loadImage(style.getFillImage());
		if ( img == null ) {
			img = ImageCache.dummyImage();
		}
		
		float gw    = (float)( metrics.graphWidthGU()  * px2gu ) ;// + ( padx * 2 )	// consider the padding ???
        float gh    = (float)( metrics.graphHeightGU() * px2gu ) ;// + ( pady * 2 )	// probably not.
        float x     = (float)( metrics.viewport[2] / 2 ) - ( gw / 2 ) ;
        float y     = (float)(metrics.viewport[3] - ( metrics.viewport[3] / 2 ) - ( gh / 2 )) ;

        p.setStyle(Paint.Style.FILL);
		g.drawBitmap(img, x, y, p);
		g.drawRect(0, 0, (float)metrics.viewport[2], (float)metrics.viewport[3], p);
	}
	

	private void fillImageScaled(Canvas g, Paint p, DefaultCamera2D camera, int mode) {
		GraphMetrics metrics = camera.getMetrics();
		double px2gu = metrics.ratioPx2Gu;
		Bitmap img = null ;
				
		img = ImageCache.loadImage(style.getFillImage());
		if ( img == null ) {
			img = ImageCache.dummyImage();
		}
				
		fillCanvasBackground( g, p, camera );
		float gw    = (float)( metrics.graphWidthGU()  * px2gu ) ;
        float gh    = (float)( metrics.graphHeightGU() * px2gu ) ;
        float x     = (float)( metrics.viewport[2] / 2 ) - ( gw / 2 ) ;
        float y     = (float)( metrics.viewport[3] - ( metrics.viewport[3] / 2 ) - ( gh / 2 ) ) ;
		
		if (mode == 0) { // Ratio
            g.drawBitmap(img, new Rect((int)x, (int)y, (int)(x+gw), (int)(y+gh)), new RectF(0.0f, 0.0f, img.getWidth(), img.getHeight()), p);
		}
		else if (mode == 1) { // Ratio-max
			double ratioi = (double)img.getWidth() / (double)img.getHeight();
			double ratiog = gw / gh;
			
			if(ratioi > ratiog) {
				double newgw = gh * ratioi;
				double newx  = x - ((newgw-gw)/2);
                g.drawBitmap(img, new Rect((int)newx, (int)y, (int)(newx+newgw), (int)(y+gh)), new RectF(0, 0, img.getWidth(), img.getHeight()), p);
            }
			else {
				double newgh = gw / ratioi;
				double newy  = y - ((newgh-gh)/2);
                g.drawBitmap(img, new Rect((int)x, (int)newy, (int)(x+gw), (int)(newy+newgh)), new RectF(0, 0, img.getWidth(), img.getHeight()), p);
            }
		}
		else if (mode == 2) { // Ratio-min
			double ratioi = (double) img.getWidth() / (double) img.getHeight();
			double ratiog = gw / gh;
					
			if( ratiog > ratioi ) {
				double newgw = gh * ratioi;
				double newx  = x + ((gw-newgw)/2);
                g.drawBitmap(img, new Rect((int)newx, (int)y, (int)(newx+newgw), (int)(y+gh)), new RectF(0, 0, img.getWidth(), img.getHeight()), p);
            }
			else {
				double newgh = gw / ratioi;
				double newy  = y + ((gh-newgh)/2);
                g.drawBitmap(img, new Rect((int)x, (int)newy, (int)(x+gw), (int)(newy+newgh)), new RectF(0, 0, img.getWidth(), img.getHeight()), p);
            }
		}
		else {
			throw new RuntimeException("Error graphBackground");
		}
	}

	private void strokeGraph(Backend bck, DefaultCamera2D camera) {
		GraphMetrics metrics = camera.getMetrics();
		Canvas g = bck.graphics2D() ;
		Paint p = bck.getPaint();
		if( style.getStrokeMode() != StyleConstants.StrokeMode.NONE && style.getStrokeWidth().value > 0 ) {
			p.setColor( ColorManager.getStrokeColor(style, 0) );

			p.setStrokeWidth((float)metrics.lengthToGu( style.getStrokeWidth()));
			int padx = (int)metrics.lengthToPx( style.getPadding(), 0 ) ;
			int pady = padx ;
			if( style.getPadding().size() > 1 ) 
				pady = (int)metrics.lengthToPx( style.getPadding(), 1 );

            p.setStyle(Paint.Style.FILL);
            g.drawRect(padx, pady, (int)metrics.viewport[2] - padx*2, (int)metrics.viewport[3] - pady*2 , p);
		}
	}
	
	protected void fillGradient(Canvas g, Paint p, DefaultCamera2D camera) {
		GraphMetrics metrics = camera.getMetrics();

		if( style.getFillColors().size() < 2 ) {
			fillBackground( g, p, camera );
		}
		else {
			int w = (int)metrics.viewport[2] ; 
			int h = (int)metrics.viewport[3] ;

            Background b = GradientFactory.gradientInArea( 0, 0, w, h, style );
			if (b != null)
				b.applyPaint(g, p);

            p.setStyle(Paint.Style.FILL);
			g.drawRect( 0, 0, w, h, p);
            //b.removePaint(p);
		}
	}
	
}

