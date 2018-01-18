package org.graphstream.ui.android_viewer;

import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.Log;

import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.android_viewer.basicRenderer.AndroidBasicGraphRenderer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

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
public class AndroidViewer extends Viewer {

    /**
     * Timer in the Graphic thread.
     */
    protected MyTimer timer ;

    /**
     * Delay of CountDownTimer.
     */
    public static final int DELAY_INTERVAL = 30;

    /**
     * Name of the default view.
     */
    public static String DEFAULT_VIEW_ID = "defaultView";

    public String getDefaultID() {
        return DEFAULT_VIEW_ID ;
    }

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

    public GraphRenderer<?, ?> newDefaultGraphRenderer() {
        return new AndroidBasicGraphRenderer();
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
