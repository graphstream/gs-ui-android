package org.graphstream.ui.android.viewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

import org.graphstream.ui.android.viewer.basicRenderer.AndroidBasicGraphRenderer;
import org.graphstream.ui.view.GraphRendererBase;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public abstract class AndroidGraphRendererBase extends GraphRendererBase<SurfaceView, Canvas> {

    private Context context ;

    public AndroidGraphRendererBase() {}

    public void setContext(Context c) {
        this.context = c;
    }
    // Utilities
    public View createDefaultView(Viewer viewer, String viewId) {
        return new DefaultView(context, viewer, viewId, this);
    }

    protected void displayNothingToDo(Canvas g, int w, int h) {
        String msg1 = "Graph width/height/depth is zero !!";
        String msg2 = "Place components using the 'xyz' attribute.";

        Paint p = new Paint();

        p.setColor(Color.RED);

        g.drawLine(0, 0, w, h, p);
        g.drawLine(0, h, w, 0, p);

        double msg1length = p.measureText(msg1);
        double msg2length = p.measureText(msg2);

        double x = w / 2;
        double y = h / 2;

        p.setColor(Color.BLACK);

        g.drawText(msg1, (float) (x - msg1length / 2), (float) (y - 20), p);
        g.drawText(msg2, (float) (x - msg2length / 2), (float) (y + 20), p);
    }

}
