package org.graphstream.ui.android_viewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;

import org.graphstream.ui.android_viewer.util.DefaultMouseManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

import java.util.Collection;
import java.util.EnumSet;

public class DefaultView extends ViewPanel {

    /**
     * Parent viewer.
     */
    protected Viewer viewer;

    /**
     * The graph to render, shortcut to the viewers reference.
     */
    protected GraphicGraph graph;

    /**
     * The graph renderer.
     */
    protected GraphRenderer renderer;

    /**
     * Manager for events when the screen is touched.
     */
    protected DefaultMouseManager mouseClicks;

    // Construction
    public DefaultView(Context context, Viewer viewer, String identifier, GraphRenderer renderer) {
        super(context, identifier);

        this.viewer = viewer;
        this.graph = viewer.getGraphicGraph();
        this.renderer = renderer;

        setMouseManager(null);
        setShortcutManager(null);
        renderer.open(graph, this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        graph.removeAttribute("ui.viewClosed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
       // mThread.keepDrawing = false;
        mouseClicks.release();
        graph.setAttribute("ui.viewClosed", getIdView());
        switch (viewer.getCloseFramePolicy()) {
            case CLOSE_VIEWER:
                viewer.removeView(getIdView());
                break;
            case HIDE_ONLY:
                viewer.removeView(getIdView());
                break;
            case EXIT:
                System.exit(0);
            default:
                throw new RuntimeException(String.format("The %s view is not up to date, do not know %s CloseFramePolicy.",
                        getClass().getName(), viewer.getCloseFramePolicy()));
        }
    }

    @Override
    public Camera getCamera() {
        return renderer.getCamera();
    }

    public void render(Canvas c) {
        // get the surfaceView's location on screen
        int[] location = new int[2];
        location[0] = 0; location[1] = 0;

        if (!isHardwareAccelerated()) {
            getLocationOnScreen(location);
        }

        renderer.render(c, location[0], location[1], getWidth(), getHeight());
        //renderer.render(c, 0, statusBarHeight, getWidth(), getHeight());

        // No screenshot in android, renderer.screenshot is empty
        String screenshot = (String) graph.getLabel("ui.screenshot");

        if (screenshot != null) {
            graph.removeAttribute("ui.screenshot");
            renderer.screenshot(screenshot, getWidth(), getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        render(canvas);
    }

    public void display(GraphicGraph graphicGraph, boolean b) {
        throw new RuntimeException("Please use the onDraw method with Canvas");
    }

    @Override
    public void openInAFrame(boolean b) {
    }

    public void close(GraphicGraph graph) {
        renderer.close();
        graph.setAttribute("ui.viewClosed", getIdView());

        //shortcuts.release();
        mouseClicks.release();

        openInAFrame(false);
    }

    public void beginSelectionAt(double x1, double y1) {
        renderer.beginSelectionAt(x1, y1);
        invalidate();
    }

    public void selectionGrowsAt(double x, double y) {
        renderer.selectionGrowsAt(x, y);
        invalidate();
    }

    public void endSelectionAt(double x2, double y2) {
        renderer.endSelectionAt(x2, y2);
        invalidate();
    }

    @Override
    public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
        return renderer.allGraphicElementsIn(types, x1, y1, x2, y2);
    }

    @Override
    public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
        return renderer.findGraphicElementAt(types,x, y);
    }


    public void moveElementAtPx(GraphicElement element, double x, double y) {
        // The feedback on the node positions is often off since not needed
        // and generating lots of events. We activate it here since the
        // movement of the node is decided by the viewer. This is one of the
        // only moment when the viewer really moves a node.
        boolean on = graph.feedbackXYZ();
        graph.feedbackXYZ(true);
        renderer.moveElementAtPx(element, x, y);
        graph.feedbackXYZ(on);
    }

    public void freezeElement(GraphicElement element, boolean frozen) {
        if (frozen) {
            element.setAttribute("layout.frozen");
        } else {
            element.removeAttribute("layout.frozen");
        }
    }

    @Override
    public void setMouseManager(MouseManager manager) {
        // Look onTouchEvent method
        if (mouseClicks != null)
            mouseClicks.release();

        if (manager == null)
            manager = new DefaultMouseManager();

        manager.init(graph, this);

        mouseClicks = (DefaultMouseManager)manager;
    }

    /**
     * This is a shortcut to a call setMouseManager instance and with
     * (InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE).
     */
    public void enableMouseOptions() {
        setMouseManager(new DefaultMouseManager(EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE)));
    }

    @Override
    public void setShortcutManager(ShortcutManager shortcutManager) {}

    @Override
    public Object requireFocus() {
        return requestFocus();
    }

    public void setBackLayerRenderer(LayerRenderer<Canvas> renderer) {
        this.renderer.setBackLayerRenderer(renderer);
        invalidate();
    }

    public void setForeLayoutRenderer(LayerRenderer<Canvas> renderer) {
        this.renderer.setForeLayoutRenderer(renderer);
        invalidate();
    }

    @Override
    public <T, U> void addListener(T descriptor, U listener) {
        View.OnTouchListener onTouch = (View.OnTouchListener)listener;
        setOnTouchListener(onTouch);
    }

    @Override
    public <T, U> void removeListener(T descriptor, U listener) {
    }
}
