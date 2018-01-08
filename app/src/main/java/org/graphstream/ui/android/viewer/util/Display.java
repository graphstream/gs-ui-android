package org.graphstream.ui.android.viewer.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class Display implements org.graphstream.util.Display {

    @Override
    public Viewer display(Graph g, boolean b) {
        throw new RuntimeException("Please create your own Activity !");
    }
}
