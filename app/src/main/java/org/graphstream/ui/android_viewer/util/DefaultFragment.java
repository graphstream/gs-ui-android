package org.graphstream.ui.android_viewer.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.graphstream.graph.Graph;
import org.graphstream.ui.android.AndroidFullGraphRenderer;
import org.graphstream.ui.android_viewer.AndroidViewer;
import org.graphstream.ui.android_viewer.DefaultView;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;

public class DefaultFragment extends Fragment {

    private Graph graph ;
    public static boolean autoLayout = true;
    private AndroidViewer viewer = null ;
    private AndroidFullGraphRenderer renderer ;

    public void init(Graph g, boolean autoLayout) {
        this.graph = g ;
        this.autoLayout = autoLayout;
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
        renderer = new AndroidFullGraphRenderer();
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

    public AndroidViewer getViewer() { return viewer; }
}
