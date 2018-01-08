package org.graphstream.ui.android.viewer.util;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.graphstream.graph.Graph;
import org.graphstream.ui.android.viewer.AndroidViewer;
import org.graphstream.ui.android.viewer.DefaultView;
import org.graphstream.ui.android.viewer.basicRenderer.AndroidBasicGraphRenderer;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;

public class DefaultFragment extends Fragment {

    private static Graph graph ;
    public static boolean autoLayout = true;
    private AndroidViewer viewer ;
    private AndroidBasicGraphRenderer renderer ;

    public void init(Graph g) {
        graph = g ;
    }

    /**
     * 1- Create only one Viewer
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Debug","DefaultFragment : onCreate");
        viewer = new AndroidViewer(graph, AndroidViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
    }

    /**
     * 2- Link the context
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Debug","DefaultFragment : onAttach");
        renderer = new AndroidBasicGraphRenderer();
        renderer.setContext(context);
    }

    /**
     * 3- Create the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("Debug","DefaultFragment : onCreateView");
        DefaultView view = (DefaultView)viewer.addView(AndroidViewer.DEFAULT_VIEW_ID, renderer);

        if (autoLayout) {
            Layout layout = Layouts.newLayoutAlgorithm();
            viewer.enableAutoLayout(layout);
        }

        return view ;
    }
}
