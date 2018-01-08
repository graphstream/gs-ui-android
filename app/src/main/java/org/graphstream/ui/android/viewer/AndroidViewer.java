package org.graphstream.ui.android.viewer;

import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.Log;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import java.util.Map;
import java.util.TreeMap;

/**
 * Set of views on a graphic graph.
 *
 * <p>
 * The viewer class is in charge of maintaining :
 * <ul>
 * <li>A "graphic graph" (a special graph that internally stores the graph under
 * the form of style sets of "graphic" elements, suitable to draw the graph, but
 * not to adapted to used it as a general graph),</li>
 * <li>The eventual proxy pipe from which the events come from (but graph events
 * can come from any kind of source),</li>
 * <li>A default view, and eventually more views on the graphic graph.</li>
 * <li>A flag that allows to repaint the view only if the graphic graph changed.
 * <li>
 * </ul>
 * </p>
 *
 * <p>
 * The graphic graph can be created by the viewer or given at construction (to
 * share it with another viewer).
 * </p>
 *
 * <p>
 * <u>Once created, the viewer runs in a loop inside the UI thread. You
 * cannot call methods on it directly if you are not in this thread</u>. The
 * only operation that you can use in other threads is the constructor, the
 * {@link #addView(View)}, {@link #removeView(String)} and the {@link #close()}
 * methods. Other methods are not protected from concurrent accesses.
 * </p>
 *
 * <p>
 * Some constructors allow a {@link ProxyPipe} as argument. If given, the
 * graphic graph is made listener of this pipe and the pipe is "pumped" during
 * the view loop. This allows to run algorithms on a graph in the main thread
 * (or any other thread) while letting the viewer run in the swing thread.
 * </p>
 *
 * <p>
 * Be very careful: due to the nature of graph events in GraphStream, the viewer
 * is not aware of events that occured on the graph <u>before</u> its creation.
 * There is a special mechanism that replay the graph if you use a proxy pipe or
 * if you pass the graph directly. However, when you create the viewer by
 * yourself and only pass a {@link Source}, the viewer <u>will not</u> display
 * the events that occured on the source before it is connected to it.
 * </p>
 */
public class AndroidViewer implements Viewer {

    /**
     * Name of the default view.
     */
    public static String DEFAULT_VIEW_ID = "defaultView";


    /**
     * How does the viewer synchronise its internal graphic graph with the graph
     * displayed. The graph we display can be in the Swing thread (as will be
     * the viewer, therefore in the same thread as the viewer), in another
     * thread, or on a distant machine.
     */
    public enum ThreadingModel {
        GRAPH_IN_GUI_THREAD, GRAPH_IN_ANOTHER_THREAD, GRAPH_ON_NETWORK
    };

    // Attribute

    /**
     * If true the graph we display is in another thread, the synchronisation
     * between the graph and the graphic graph must therefore use thread
     * proxies.
     */
    protected boolean graphInAnotherThread = true;

    /**
     * The graph observed by the views.
     */
    protected GraphicGraph graph;

    /**
     * If we have to pump events by ourself.
     */
    protected ProxyPipe pumpPipe;

    /**
     * If we take graph events from a source in this thread.
     */
    protected Source sourceInSameThread;

    /**
     * Timer in the Swing thread.
     */
    protected MyTimer timer ;

    /**
     * Delay of CountDownTimer.
     */
    public static final int DELAY_INTERVAL = 30;

    /**
     * The set of views.
     */
    protected final Map<String, View> views = new TreeMap<String, View>();

    /**
     * What to do when a view frame is closed.
     */
    protected CloseFramePolicy closeFramePolicy = CloseFramePolicy.EXIT;

    /**
     * Optional layout algorithm running in another thread.
     */
    protected LayoutRunner optLayout = null;

    /**
     * If there is a layout in another thread, this is the pipe coming from it.
     */
    protected ProxyPipe layoutPipeIn = null;

    /**
     * The graph or source of graph events is in another thread or on another
     * machine, but the pipe already exists. The graphic graph displayed by this
     * viewer is created.
     *
     * @param source
     *            The source of graph events.
     */
    public AndroidViewer(ProxyPipe source) {
        graphInAnotherThread = true;
        init(new GraphicGraph(newGGId()), source, (Source) null);
    }

    /**
     * We draw a pre-existing graphic graph. The graphic graph is maintained by
     * its creator.
     *
     * @param graph
     *            THe graph to draw.
     */
    public AndroidViewer(GraphicGraph graph) {
        graphInAnotherThread = false;
        init(graph, (ProxyPipe) null, (Source) null);
    }

    /**
     * New viewer on an existing graph. The viewer always run in the Swing
     * thread, therefore, you must specify how it will take graph events from
     * the graph you give. If the graph you give will be accessed only from the
     * Swing thread use ThreadingModel.GRAPH_IN_GUI_THREAD. If the graph you use
     * is accessed in another thread use ThreadingModel.GRAPH_IN_ANOTHER_THREAD.
     * This last scheme is more powerful since it allows to run algorithms on
     * the graph in parallel with the viewer.
     *
     * @param graph
     *            The graph to render.
     * @param threadingModel
     *            The threading model.
     */
    public AndroidViewer(Graph graph, ThreadingModel threadingModel) {
        Log.e("Debug","AndroidViewer : const");

        switch (threadingModel) {
            case GRAPH_IN_GUI_THREAD:
                graphInAnotherThread = false;
                init(new GraphicGraph(newGGId()), (ProxyPipe) null, graph);
                enableXYZfeedback(true);
                break;
            case GRAPH_IN_ANOTHER_THREAD:
                graphInAnotherThread = true;
                ThreadProxyPipe tpp = new ThreadProxyPipe();
                tpp.init(graph, true);

                init(new GraphicGraph(newGGId()), tpp, (Source) null);
                enableXYZfeedback(false);
                break;
            case GRAPH_ON_NETWORK:
                throw new RuntimeException("TO DO, sorry !:-)");
        }
    }
    /**
     * Initialise the viewer.
     *
     * @param graphicGraph
     *            The graphic graph.
     * @param proxyPipe
     *            The source of events from another thread or machine (null if
     *            source != null).
     * @param source
     *            The source of events from this thread (null if ppipe != null).
     */
    public void init(GraphicGraph graphicGraph, ProxyPipe proxyPipe, Source source) {
        this.graph = graphicGraph;
        this.pumpPipe = proxyPipe;
        this.sourceInSameThread = source;

        this.timer = new MyTimer();

        assert ((proxyPipe != null && source == null) || (proxyPipe == null && source != null));

        if (pumpPipe != null)
            pumpPipe.addSink(graph);
        if (sourceInSameThread != null) {
            if (source instanceof Graph)
                replayGraph((Graph) source);
            sourceInSameThread.addSink(graph);
        }

        timer.start();
    }

    /**
     * Create a new unique identifier for a graph.
     *
     * @return The new identifier.
     */
    public String newGGId() {
        return String.format("GraphicGraph_%d", (int) (Math.random() * 10000));
    }

    /**
     * Close definitively this viewer and all its views.
     */
    public void close() {
        synchronized (views) {
            disableAutoLayout();

            for (View view : views.values())
                view.close(graph);

            timer.stop();

            if (pumpPipe != null)
                pumpPipe.removeSink(graph);
            if (sourceInSameThread != null)
                sourceInSameThread.removeSink(graph);

            graph = null;
            pumpPipe = null;
            sourceInSameThread = null;
            timer = null;
        }
    }

    /**
     * Create a new instance of the default graph renderer. The default graph
     * renderer class is given by the "org.graphstream.ui" system
     * property. If the class indicated by this property is not usable (not in
     * the class path, not of the correct type, etc.) or if the property is not
     * present a SwingBasicGraphRenderer is returned.
     */
    public static GraphRenderer<?, ?> newGraphRenderer() {
        return null;// new AndroidBasicGraphRenderer();
    }

    /**
     * What to do when a frame is closed.
     */
    @Override
    public CloseFramePolicy getCloseFramePolicy() {
        return closeFramePolicy;
    }

    /**
     * New proxy pipe on events coming from the viewer through a thread.
     *
     * @return The new proxy pipe.
     */
    public ProxyPipe newThreadProxyOnGraphicGraph() {
        ThreadProxyPipe tpp = new ThreadProxyPipe();
        tpp.init(graph);
        return tpp;
    }

    /**
     * New viewer pipe on the events coming from the viewer through a thread.
     *
     * @return The new viewer pipe.
     */
    public ViewerPipe newViewerPipe() {
        ThreadProxyPipe tpp = new ThreadProxyPipe();
        tpp.init(graph, false);

        enableXYZfeedback(true);

        return new ViewerPipe(String.format("viewer_%d", (int) (Math.random() * 10000)), tpp);
    }

    /**
     * The underlying graphic graph. Caution : Use the returned graph only in
     * the UI thread !!
     */
    public GraphicGraph getGraphicGraph() {
        return graph;
    }

    /**
     * The view that correspond to the given identifier.
     *
     * @param id
     *            The view identifier.
     * @return A view or null if not found.
     */
    public View getView(String id) {
        synchronized (views) {
            return views.get(id);
        }
    }

    /**
     * The default view. This is a shortcut to a call to
     * {@link #getView(String)} with {@link #DEFAULT_VIEW_ID} as parameter.
     *
     * @return The default view or null if no default view has been installed.
     */
    public ViewPanel getDefaultView() {
        return (DefaultView) getView(DEFAULT_VIEW_ID);
    }

    /**
     * Build the default graph view and insert it. The view identifier is
     * {@link #DEFAULT_VIEW_ID}. You can request the view to be open in its own
     * frame.
     *
     * @param openInAFrame
     *            It true, the view is placed in a frame, else the view is only
     *            created and you must embed it yourself in your application.
     */
    public View addDefaultView(boolean openInAFrame) {
        synchronized (views) {
            GraphRenderer<?, ?> renderer = newGraphRenderer();
            View view = renderer.createDefaultView(this, DEFAULT_VIEW_ID);

            addView(view);

            if (openInAFrame)
                view.openInAFrame(true);

            return view;
        }
    }

    /**
     * Add a view using its identifier. If there was already a view with this
     * identifier, it is closed and returned (if different of the one added).
     *
     * @param view
     *            The view to add.
     * @return The old view that was at the given identifier, if any, else null.
     */
    public View addView(View view) {
        synchronized (views) {
            View old = views.put(view.getIdView(), view);

            if (old != null && old != view)
                old.close(graph);

            return old;
        }
    }

    /**
     * Add a new default view with a specific renderer. If a view with the same
     * id exists, it is removed and closed. By default the view is open in a
     * frame.
     *
     * @param id
     *            The new view identifier.
     * @param renderer
     *            The renderer to use.
     * @return The created view.
     */
    public View addView(String id, GraphRenderer<?, ?> renderer) {
        return addView(id, renderer, true);
    }

    /**
     * Same as {@link #addView(String, GraphRenderer)} but allows to specify
     * that the view uses a frame or not.
     *
     * @param id
     *            The new view identifier.
     * @param renderer
     *            The renderer to use.
     * @param openInAFrame
     *            If true the view is open in a frame, else the returned view is
     *            a JPanel that can be inserted in a GUI.
     * @return The created view.
     */
    public View addView(String id, GraphRenderer<?, ?> renderer, boolean openInAFrame) {
        synchronized (views) {
            View view = renderer.createDefaultView(this, id);
            addView(view);

            if (openInAFrame)
                view.openInAFrame(true);

            return view;
        }
    }

    /**
     * Remove a view. The view is not closed.
     *
     * @param id
     *            The view identifier.
     */
    public void removeView(String id) {
        synchronized (views) {
            views.remove(id);
        }
    }

    /**
     * Compute the overall bounds of the graphic graph according to the nodes
     * and sprites positions. We can only compute the graph bounds from the
     * nodes and sprites centres since the node and graph bounds may in certain
     * circumstances be computed according to the graph bounds. The bounds are
     * stored in the graph metrics.
     */
    public void computeGraphMetrics() {
        graph.computeBounds();

        synchronized (views) {
            Point3 lo = graph.getMinPos();
            Point3 hi = graph.getMaxPos();
            for (final View view : views.values()) {
                Camera camera = view.getCamera();
                if (camera != null) {
                    camera.setBounds(lo.x, lo.y, lo.z, hi.x, hi.y, hi.z);
                }
            }
        }
    }

    /**
     * What to do when the frame containing one or more views is closed.
     *
     * @param policy
     *            The close frame policy.
     */
    public void setCloseFramePolicy(CloseFramePolicy policy) {
        synchronized (views) {
            closeFramePolicy = policy;
        }
    }

    /**
     * Enable or disable the "xyz" attribute change when a node is moved in the
     * views. By default the "xyz" attribute is changed.
     *
     * By default, each time a node of the graphic graph is moved, its "xyz"
     * attribute is reset to follow the node position. This is useful only if
     * someone listen at the graphic graph or use the graphic graph directly.
     * But this operation is quite costly. Therefore by default if this viewer
     * runs in its own thread, and the main graph is in another thread, xyz
     * attribute change will be disabled until a listener is added.
     *
     * When the viewer is created to be used only in the swing thread, this
     * feature is always on.
     */
    public void enableXYZfeedback(boolean on) {
        synchronized (views) {
            graph.feedbackXYZ(on);
        }
    }

    /**
     * Launch an automatic layout process that will position nodes in the
     * background.
     */
    public void enableAutoLayout() {
        enableAutoLayout(Layouts.newLayoutAlgorithm());
    }

    /**
     * Launch an automatic layout process that will position nodes in the
     * background.
     *
     * @param layoutAlgorithm
     *            The algorithm to use (see Layouts.newLayoutAlgorithm() for the
     *            default algorithm).
     */
    public void enableAutoLayout(Layout layoutAlgorithm) {
        synchronized (views) {
            if (optLayout == null) {
                // optLayout = new LayoutRunner(graph, layoutAlgorithm, true,
                // true);
                optLayout = new LayoutRunner(graph, layoutAlgorithm, true, false);
                graph.replay();
                layoutPipeIn = optLayout.newLayoutPipe();
                layoutPipeIn.addAttributeSink(graph);
            }
        }
    }

    /**
     * Disable the running automatic layout process, if any.
     */
    public void disableAutoLayout() {
        synchronized (views) {
            if (optLayout != null) {
                ((ThreadProxyPipe) layoutPipeIn).unregisterFromSource();
                layoutPipeIn.removeSink(graph);
                layoutPipeIn = null;
                optLayout.release();
                optLayout = null;
            }
        }
    }

    /** Dirty replay of the graph. */
    public void replayGraph(Graph graph) {
        // Replay all graph attributes.

        graph.attributeKeys().forEach(key -> {
            graph.setAttribute(key, graph.getAttribute(key));
        });

        // Replay all nodes and their attributes.

        graph.nodes().forEach(node -> {
            Node n = this.graph.addNode(node.getId());

            node.attributeKeys().forEach(key -> {
                n.setAttribute(key, node.getAttribute(key));
            });
        });

        // Replay all edges and their attributes.

        graph.edges().forEach(edge -> {
            Edge e = graph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId(),
                    edge.isDirected());

            edge.attributeKeys().forEach(key -> {
                e.setAttribute(key, edge.getAttribute(key));
            });
        });
    }

    class MyTimer extends CountDownTimer
    {
        private boolean loop = true ;

        public MyTimer() {
            super(30000, DELAY_INTERVAL); //interval/ticks each second.
        }

        @Override
        public void onFinish() {
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Debug", "tick");
            //fired on each interval
            synchronized (views) {
                if (pumpPipe != null)
                    pumpPipe.pump();

                if (layoutPipeIn != null)
                    layoutPipeIn.pump();

                // Prevent the timer from using a empty graph to display
                if(graph != null){
                    boolean changed = graph.graphChangedFlag();

                    if (changed) {
                        computeGraphMetrics();

                        for (View view : views.values()) {
                            Canvas canvas = null;
                            if(view instanceof DefaultView) {
                                DefaultView dView = (DefaultView)view ;

                                try {
                                    canvas = dView.getSurface().lockCanvas();
                                    synchronized (dView.getSurface()) {
                                        dView.postInvalidate();
                                    }
                                }
                                finally {
                                    // Release Canvas for print
                                    if (canvas != null)
                                        dView.getSurface().unlockCanvasAndPost(canvas);
                                }
                            }
                        }
                    }

                    graph.resetGraphChangedFlag();
                }
            }

            if ( !loop ) {
                cancel();
            }

        }

        public void stop() {
            this.loop = false;
        }
    }
}
