package org.graphstream.ui.android_viewer.util;

import android.view.MotionEvent;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;

import java.util.EnumSet;

public class DefaultMouseManager implements MouseManager, android.view.View.OnTouchListener {

    /**
     * The view this manager operates upon.
     */
    protected View view;

    /**
     * The graph to modify according to the view actions.
     */
    protected GraphicGraph graph;

    final private EnumSet<InteractiveElement> types;

    protected GraphicElement curElement;

    protected float x1, y1;

    public DefaultMouseManager() {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE));
    }

    public DefaultMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.graph = graph;
        view.addListener("Touch", this);
    }

    @Override
    public EnumSet<InteractiveElement> getManagedTypes() {
        return types;
    }

    public void release() {
        view.removeListener("Touch", this);
    }

    @Override
    public boolean onTouch(android.view.View v, MotionEvent event) {

        switch(event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                curElement = view.findGraphicElementAt(types,event.getX(), event.getY());

                if (curElement != null) {
                    mouseButtonPressOnElement(curElement, event);
                } else {
                    x1 = event.getX();
                    y1 = event.getY();
                    mouseButtonPress(event);
                    view.beginSelectionAt(x1, y1);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (curElement != null) {
                    elementMoving(curElement, event);
                } else {
                    view.selectionGrowsAt(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (curElement != null) {
                    mouseButtonReleaseOffElement(curElement, event);
                    curElement = null;
                } else {
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float t;

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

                    mouseButtonRelease(event, view.allGraphicElementsIn(types,x1, y1, x2, y2));
                    view.endSelectionAt(x2, y2);
                }
                break;
            default:
                break;
        }
        return true;
    }

    protected void mouseButtonPress(MotionEvent event) {
        view.requireFocus();

        // Unselect all.
        graph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
        graph.sprites().filter(s -> s.hasAttribute("ui.selected")).forEach(s -> s.removeAttribute("ui.selected"));
        graph.edges().filter(e -> e.hasAttribute("ui.selected")).forEach(e -> e.removeAttribute("ui.selected"));

    }

    protected void mouseButtonRelease(MotionEvent event,
                                      Iterable<GraphicElement> elementsInArea) {
        for (GraphicElement element : elementsInArea) {
            if (!element.hasAttribute("ui.selected"))
                element.setAttribute("ui.selected");
        }
    }

    protected void mouseButtonPressOnElement(GraphicElement element,
                                             MotionEvent event) {
        view.freezeElement(element, true);

        element.setAttribute("ui.clicked");
    }

    protected void elementMoving(GraphicElement element, MotionEvent event) {
        view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonReleaseOffElement(GraphicElement element,
                                                MotionEvent event) {
        view.freezeElement(element, false);
            element.removeAttribute("ui.clicked");
    }
}
