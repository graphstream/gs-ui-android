package org.graphstream.ui.android.viewer.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.graphstream.ui.graphicGraph.StyleGroup;

public class ColorManager {
    public static Paint paint = new Paint();

    public static int getStrokeColor(StyleGroup style, int i) {
        return getColor(style.getStrokeColor(i));
    }

    public static int getFillColor(StyleGroup style, int i) {
        return getColor(style.getFillColor(i));
    }

    public static int getTextColor(StyleGroup style, int i) {
        return getColor(style.getTextColor(i));
    }

    public static int getColor(org.graphstream.ui.graphicGraph.stylesheet.Color c) {
        return Color.argb(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
    }

    public static org.graphstream.ui.graphicGraph.stylesheet.Color getGraphstreamColor(int color) {
        return new org.graphstream.ui.graphicGraph.stylesheet.Color(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
    }
}
