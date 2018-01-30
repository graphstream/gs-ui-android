package org.graphstream.ui.android_viewer.basicRenderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceView;

import org.graphstream.graph.Element;
import org.graphstream.ui.android_viewer.AndroidGraphRendererBase;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android_viewer.util.DefaultCamera;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.util.GraphMetrics;
import org.graphstream.ui.view.util.InteractiveElement;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidBasicGraphRenderer extends AndroidGraphRendererBase {
    private static final Logger logger = Logger.getLogger(AndroidBasicGraphRenderer.class.getName());

    /**
     * Set the view on the view port defined by the metrics.
     */
    protected DefaultCamera camera = null;

    protected NodeRenderer nodeRenderer = new NodeRenderer();

    protected EdgeRenderer edgeRenderer = new EdgeRenderer();

    protected SpriteRenderer spriteRenderer = new SpriteRenderer();

    protected LayerRenderer<Canvas> backRenderer = null;

    protected LayerRenderer<Canvas> foreRenderer = null;

    protected PrintStream fpsLog = null;

    protected long T1 = 0;

    protected long steps = 0;

    protected double sumFps = 0;

    // Construction

    public AndroidBasicGraphRenderer() {
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.ui.view.GraphRendererBase#open(org.graphstream.ui.
	 * graphicGraph.GraphicGraph, java.lang.Object)
	 */
    @Override
    public void open(GraphicGraph graph, SurfaceView renderingSurface) {
        super.open(graph, renderingSurface);
        camera = new DefaultCamera(graph);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.graphstream.ui.view.GraphRendererBase#close()
     */
    @Override
    public void close() {
        if (fpsLog != null) {
            fpsLog.flush();
            fpsLog.close();
            fpsLog = null;
        }

        camera = null;
        super.close();
    }

    // Access

    public Camera getCamera() {
        return camera;
    }

    @Override
    public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
        return camera.allGraphicElementsIn(graph, types, x1, y1, x2, y2);
    }

    @Override
    public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
        return camera.findGraphicElementAt(graph, types, x, y);
    }

    // Command

    public void render(Canvas c, int x, int y, int width, int height) {
        // If not closed, one or two renders can occur after closed.
        // Camera == null means closed. In case render occurs after closing
        // (called from the gfx thread).
        Paint p = new Paint();

        if (graph != null && c != null && camera != null) {
            beginFrame();

            if (camera.getGraphViewport() == null && camera.getMetrics().diagonal == 0
                    && (graph.getNodeCount() == 0 && graph.getSpriteCount() == 0)) {
                displayNothingToDo(c, p, width, height);
            } else {
                camera.setPadding(graph);
                camera.setViewport(x, y, width, height);
                renderGraph(c, p);
                renderSelection(c, p);
            }

            endFrame();
        }
    }

    protected void beginFrame() {
        if (graph.hasLabel("ui.log") && fpsLog == null) {
            try {
                fpsLog = new PrintStream(graph.getLabel("ui.log").toString());
            } catch (IOException e) {
                fpsLog = null;
                e.printStackTrace();
            }
        }

        if (fpsLog != null) {
            T1 = System.currentTimeMillis();
        }
    }

    protected void endFrame() {
        if (fpsLog != null) {
            steps += 1;
            long T2 = System.currentTimeMillis();
            long time = T2 - T1;
            double fps = 1000.0 / time;
            sumFps += fps;
            fpsLog.printf("%.3f   %d   %.3f%n", fps, time, (sumFps / steps));
        }
    }

    public void moveElementAtPx(GraphicElement element, double x, double y) {
        Point3 p = camera.transformPxToGu(camera.getMetrics().viewport[0] + x, camera.getMetrics().viewport[1] + y);
        element.move(p.x, p.y, element.getZ());
    }

    // Rendering

    protected void renderGraph(Canvas g, Paint p) {
        StyleGroup style = graph.getStyle();

        setupGraphics(g, p);
        renderGraphBackground(g, p);
        renderBackLayer(g);
        camera.pushView(graph, g);
        renderGraphElements(g, p);

        if (style.getStrokeMode() != StyleConstants.StrokeMode.NONE && style.getStrokeWidth().value != 0) {
            GraphMetrics metrics = camera.getMetrics();
            RectF rect = new RectF();
            double px1 = metrics.px1;
            Value stroke = style.getShadowWidth();

            rect.set((float) metrics.lo.x, (float) (metrics.lo.y + px1), (float) (metrics.size.data[0] - px1), (float) (metrics.size.data[1] - px1));

            p.setStrokeWidth((float) metrics.lengthToGu(stroke));
            p.setColor(ColorManager.getStrokeColor(graph.getStyle(), 0));
            p.setStyle(Paint.Style.FILL);
            g.drawRect(rect, p);
        }

        camera.popView(g);
        renderForeLayer(g);
    }

    /**
     * Render the background of the graph.
     *
     * @param g The Swing graphics.
     */
    protected void renderGraphBackground(Canvas g, Paint paint) {
        StyleGroup group = graph.getStyle();

        if (group.getFillMode() != StyleConstants.FillMode.NONE) {
            int c = ColorManager.getFillColor(group, 0);
            paint.setColor(c);
            paint.setStyle(Paint.Style.FILL);
            g.drawRect(0, 0, (int) camera.getMetrics().viewport[2], (int) camera.getMetrics().viewport[3], paint);
        }
    }

    /**
     * Render the element of the graph.
     *
     * @param g The Swing graphics.
     */
    protected void renderGraphElements(Canvas g, Paint p) {
        try {
            StyleGroupSet sgs = graph.getStyleGroups();
            //System.out.println(sgs);
            if (sgs != null) {
                for (HashSet<StyleGroup> groups : sgs.zIndex()) {
                    for (StyleGroup group : groups) {
                        renderGroup(g, p, group);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected error during graph render.", e);
        }
    }

    /**
     * Render a style group.
     *
     * @param g     The Swing graphics.
     * @param group The group to render.
     */
    protected void renderGroup(Canvas g, Paint p, StyleGroup group) {
        switch (group.getType()) {
            case NODE:
                nodeRenderer.render(group, g, p, camera);
                break;
            case EDGE:
                edgeRenderer.render(group, g, p, camera);
                break;
            case SPRITE:
                spriteRenderer.render(group, g, p, camera);
                break;
            default:
                // Do nothing
                break;
        }
    }

    protected void setupSpriteStyle(Canvas g, Paint paint, StyleGroup group) {
        int c = ColorManager.getFillColor(group, 0);
        paint.setColor(c);
    }

    protected void renderSelection(Canvas g, Paint paint) {
        if (selection != null && selection.x1 != selection.x2 && selection.y1 != selection.y2) {
            double x1 = selection.x1;
            double y1 = selection.y1;
            double x2 = selection.x2;
            double y2 = selection.y2;
            double t;

            double w = camera.getMetrics().getSize().data[0];
            double h = camera.getMetrics().getSize().data[1];

            if (x1 > x2) {
                t = x1;
                x1 = x2;
                x2 = t;
            }
            if (y1 > y2) {
                t = y1;
                y1 = y2;
                y2 = t;
            }

            Paint oldPaint = paint;
            paint.setStrokeWidth(1);

            paint.setColor(Color.argb(128, 50, 50, 200));
            paint.setStyle(Paint.Style.FILL);
            g.drawRect((float) x1, (float) y1, (float) (x1+(x2 - x1)), (float) (y1+(y2 - y1)), paint);

            //paint.setColor(Color.argb(128, 0, 0, 0));
           /* g.drawLine(0.0f, (float) y1, (float) w, (float) y1, paint);
            g.drawLine(0.0f, (float) y2, (float) w, (float) y2, paint);
            g.drawLine((float) x1, 0.0f, (float) x1, (float) h, paint);
            g.drawLine((float) x2, 0.0f, (float) x2, (float) h, paint);*/

            paint.setColor(Color.argb(64, 50, 50, 200));
            paint.setStyle(Paint.Style.STROKE);
            g.drawRect((float) x1, (float) y1, (float) (x1+(x2 - x1)), (float) (y1+(y2 - y1)), paint);

            paint = oldPaint;
        }
    }

    protected void renderBackLayer(Canvas g) {
        if (backRenderer != null)
            renderLayer(g, backRenderer);
    }

    protected void renderForeLayer(Canvas g) {
        if (foreRenderer != null)
            renderLayer(g, foreRenderer);
    }

    protected void renderLayer(Canvas g, LayerRenderer<Canvas> renderer) {
        GraphMetrics metrics = camera.getMetrics();

        renderer.render(g, graph, metrics.ratioPx2Gu, (int) metrics.viewport[2], (int) metrics.viewport[3],
                metrics.loVisible.x, metrics.loVisible.y, metrics.hiVisible.x, metrics.hiVisible.y);
    }

    /**
     * Show the centre, the low and high points of the graph, and the visible
     * area (that should always map to the window borders).
     */
    protected void debugVisibleArea(Canvas g, Paint paint) {
        RectF rect = new RectF();
        GraphMetrics metrics = camera.getMetrics();

        float x = (float) metrics.loVisible.x;
        float y = (float) metrics.loVisible.y;
        float w = (float) Math.abs(metrics.hiVisible.x - x);
        float h = (float) Math.abs(metrics.hiVisible.y - y);

        rect.set(x, y, w, h);
        paint.setStrokeWidth((float) (metrics.px1 * 4));
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        g.drawRect(rect, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        double px1 = metrics.px1;
        g.drawOval((float) (camera.getViewCenter().x - 3 * px1), (float) (camera.getViewCenter().y - 3 * px1), (float) (px1 * 6), (float) (px1 * 6), paint);
        g.drawOval((float) (metrics.lo.x - 3 * px1), (float) (metrics.lo.y - 3 * px1), (float) (px1 * 6), (float) (px1 * 6), paint);
        g.drawOval((float) (metrics.hi.x - 3 * px1), (float) (metrics.hi.y - 3 * px1), (float) (px1 * 6), (float) (px1 * 6), paint);
    }

    public void screenshot(String filename, int width, int height) {

    }

    public void setBackLayerRenderer(LayerRenderer<Canvas> renderer) {
        backRenderer = renderer;
    }

    public void setForeLayoutRenderer(LayerRenderer<Canvas> renderer) {
        foreRenderer = renderer;
    }

    // Style Group Listener

    public void elementStyleChanged(Element element, StyleGroup oldStyle, StyleGroup style) {
    }

    protected void setupGraphics(Canvas g, Paint p) {
        p.setAntiAlias(graph.hasAttribute("ui.antialias"));
    }
}
