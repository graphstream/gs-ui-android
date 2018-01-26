package org.graphstream.ui.android.renderer.shape.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.android.renderer.ConnectorSkeleton;
import org.graphstream.ui.view.camera.Camera;

/** Utility trait to display cubics Bézier curves control polygons. */
public class ShowCubics {
	public boolean showControlPolygon = false ;
	
	/** Show the control polygons. */
    public void showCtrlPoints(Canvas g, Camera camera, ConnectorSkeleton skel) {
        if (showControlPolygon && skel.isCurve()) {
            Point3 from = skel.from();
            Point3 ctrl1 = skel.apply(1);
            Point3 ctrl2 = skel.apply(2);
            Point3 to = skel.to();

            int color = ColorManager.paint.getColor();
            float stroke = ColorManager.paint.getStrokeWidth();

            double px6 = camera.getMetrics().px1 * 6;
            double px3 = camera.getMetrics().px1 * 3;

            ColorManager.paint.setColor(Color.RED);
            ColorManager.paint.setStyle(Paint.Style.FILL);
            g.drawOval((float)(from.x - px3), (float)(from.y - px3), (float)px6, (float)px6, ColorManager.paint);

            if (ctrl1 != null) {
            	g.drawOval((float)(ctrl1.x - px3), (float)(ctrl1.y - px3), (float)px6, (float)px6, ColorManager.paint);

            	g.drawOval((float)(ctrl2.x - px3), (float)(ctrl2.y - px3), (float)px6, (float)px6, ColorManager.paint);

                ColorManager.paint.setStyle(Paint.Style.STROKE);
                ColorManager.paint.setStrokeWidth((float)camera.getMetrics().px1);
                g.drawLine((float)ctrl1.x, (float)ctrl1.y, (float)ctrl2.x, (float)ctrl2.y, ColorManager.paint);
               
                g.drawLine((float)from.x, (float)from.y, (float)ctrl1.x, (float)ctrl1.y, ColorManager.paint);
                
                g.drawLine((float)ctrl2.x, (float)ctrl2.y, (float)to.x, (float)to.y, ColorManager.paint);
            }

            ColorManager.paint.setStyle(Paint.Style.FILL);
            g.drawOval((float)(to.x - px3), (float)(to.y - px3), (float)px6, (float)px6, ColorManager.paint);

            ColorManager.paint.setColor(color);
            ColorManager.paint.setStrokeWidth(stroke);
        }
    }
}