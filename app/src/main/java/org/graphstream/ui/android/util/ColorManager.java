package org.graphstream.ui.android.util;

import android.graphics.Color;
import android.graphics.Paint;

import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.Style;

public class ColorManager {
    public static Float dashes = null ;
    /**
     * Get fill awt color in styleGroup and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getFillColor(StyleGroup group, int id) {
        return getColor(group.getFillColor(id));
    }

    /**
     * Get fill awt color in group and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getFillColor(Style group, int id) {
        return getColor(group.getFillColor(id));
    }

    /**
     * Get stroke awt color in styleGroup and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getStrokeColor(StyleGroup group, int id) {
        return getColor(group.getStrokeColor(id));
    }

    /**
     * Get stroke awt color in group and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getStrokeColor(Style group, int id) {
        return getColor(group.getStrokeColor(id));
    }

    /**
     * Get canvas awt color in styleGroup and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getCanvasColor(StyleGroup group, int id) {
        return getColor(group.getCanvasColor(id));
    }

    /**
     * Get shadow awt color in styleGroup and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getShadowColor(StyleGroup group, int id) {
        return getColor(group.getShadowColor(id));
    }

    /**
     * Get shadow awt color in group and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getShadowColor(Style group, int id) {
        return getColor(group.getShadowColor(id));
    }

    /**
     * Get text awt color in group and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getTextColor(Style group, int id) {
        return getColor(group.getTextColor(id));
    }

    /**
     * Get text background awt color in group and convert to android Color
     * @param group
     * @param id
     * @return int
     */
    public static int getTextBackgroundColor(Style group, int id) {
        return getColor(group.getTextBackgroundColor(id));
    }

    public static int getColor(org.graphstream.ui.graphicGraph.stylesheet.Color c) {
        return Color.argb(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
    }

    public static org.graphstream.ui.graphicGraph.stylesheet.Color getGraphstreamColor(int color) {
        return new org.graphstream.ui.graphicGraph.stylesheet.Color(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
    }
}
