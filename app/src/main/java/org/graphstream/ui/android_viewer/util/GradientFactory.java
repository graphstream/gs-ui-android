package org.graphstream.ui.android_viewer.util;

import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.Log;

import org.graphstream.ui.android.util.Background;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.android.util.ColorManager;

public class GradientFactory {
	/**
	 * Generate a gradient in the given pixel area following the given style.
	 * This produces a gradient only if the style fill-mode is compatible.
	 *
	 * @param x0
	 *            The left corner of the area.
	 * @param y0
	 *            The bottom corner of the area.
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 * @param style
	 *            The style.
	 * @return A gradient paint or null if the style does not specify a
	 *         gradient.
	 */
	public static Background gradientInArea(int x0, int y0, int width, int height,
											Style style) {
		switch (style.getFillMode()) {
			case GRADIENT_VERTICAL:
				return linearGradientFromStyle(x0, y0, x0, y0 + height, style);
			case GRADIENT_HORIZONTAL:
				return linearGradientFromStyle(x0, y0, x0 + width, y0, style);
			case GRADIENT_DIAGONAL1:
				return linearGradientFromStyle(x0, y0, x0 + width, y0 + height,
						style);
			case GRADIENT_DIAGONAL2:
				return linearGradientFromStyle(x0 + width, y0, x0, y0 + height,
						style);
			case GRADIENT_RADIAL:
				return radialGradientFromStyle(x0 + (width / 2), y0 + (height / 2),
						width > height ? width / 2 : height / 2, style);
			default:
				return null;
		}
	}

	/**
	 * Generate a linear gradient between two given points corresponding to the
	 * given style.
	 *
	 * @param x0
	 *            The start point abscissa.
	 * @param y0
	 *            The start point ordinate.
	 * @param x1
	 *            The end point abscissa.
	 * @param y1
	 *            The end point ordinate.
	 * @param style
	 *            The style.
	 * @return A paint for the gradient or null if the style specifies no
	 *         gradient (the fill mode is not a linear gradient or there is only
	 *         one fill colour).
	 */
	public static Background linearGradientFromStyle(float x0, float y0, float x1,
												float y1, Style style) {
		Background paint = null;

		if (style.getFillColorCount() > 1) {
			switch (style.getFillMode()) {
				case GRADIENT_DIAGONAL1:
				case GRADIENT_DIAGONAL2:
				case GRADIENT_HORIZONTAL:
				case GRADIENT_VERTICAL:
					paint = new Background(new LinearGradient(x0, y0, x1, y1,
							createColors(style), createFractions(style), Shader.TileMode.CLAMP));
					break;
				default:
					break;
			}
		}

		return paint;
	}

	public static Background radialGradientFromStyle(float cx, float cy,
												float radius, Style style) {
		return radialGradientFromStyle(cx, cy, radius, cx, cy, style);
	}

	/**
	 * Generate a radial gradient between whose center is at (cx,cy) with the
	 * given radius. The focus (fx,fy) is the start position of the gradient in
	 * the circle.
	 *
	 * @param cx
	 *            The center point abscissa.
	 * @param cy
	 *            The center point ordinate.
	 * @param fx
	 *            The start point abscissa.
	 * @param fy
	 *            The start point ordinate.
	 * @param radius
	 *            The gradient radius.
	 * @param style
	 *            The style.
	 * @return A paint for the gradient or null if the style specifies no
	 *         gradient (the fill mode is not a radial gradient or there is only
	 *         one fill colour).
	 */
	public static Background radialGradientFromStyle(float cx, float cy,
												float radius, float fx, float fy, Style style) {
		Background paint = null;

        if (style.getFillColorCount() > 1
                && style.getFillMode() == FillMode.GRADIENT_RADIAL) {
            float fractions[] = createFractions(style);
            int colors[] = createColors(style);
            paint = new Background(new RadialGradient(cx, cy, radius, colors, fractions,
                    Shader.TileMode.MIRROR));
        }


		return paint;
	}

	protected static float[] createFractions(Style style) {
		int n = style.getFillColorCount();

		if (n < predefFractions.length)
			return predefFractions[n];

		float fractions[] = new float[n];
		float div = 1f / (n - 1);

		for (int i = 1; i < (n - 1); i++)
			fractions[i] = div * i;

		fractions[0] = 0f;
		fractions[n - 1] = 1f;

		return fractions;
	}

	protected static int[] createColors(Style style) {
		int n = style.getFillColorCount();
		int colors[] = new int[n];

		for (int i = 0; i < n; i++)
			colors[i] = ColorManager.getFillColor(style, i);

		return colors;
	}

	public static float[][] predefFractions = new float[11][];
	public static float[] predefFractions2 = { 0f, 1f };
	public static float[] predefFractions3 = { 0f, 0.5f, 1f };
	public static float[] predefFractions4 = { 0f, 0.33f, 0.66f, 1f };
	public static float[] predefFractions5 = { 0f, 0.25f, 0.5f, 0.75f, 1f };
	public static float[] predefFractions6 = { 0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f };
	public static float[] predefFractions7 = { 0f, 0.1666f, 0.3333f, 0.4999f,
			0.6666f, 0.8333f, 1f };
	public static float[] predefFractions8 = { 0f, 0.1428f, 0.2856f, 0.4284f,
			0.5712f, 0.7140f, 0.8568f, 1f };
	public static float[] predefFractions9 = { 0f, 0.125f, 0.25f, 0.375f, 0.5f,
			0.625f, .75f, 0.875f, 1f };
	public static float[] predefFractions10 = { 0f, 0.1111f, 0.2222f, 0.3333f,
			0.4444f, 0.5555f, 0.6666f, 0.7777f, 0.8888f, 1f };

	static {
		predefFractions[0] = null;
		predefFractions[1] = null;
		predefFractions[2] = predefFractions2;
		predefFractions[3] = predefFractions3;
		predefFractions[4] = predefFractions4;
		predefFractions[5] = predefFractions5;
		predefFractions[6] = predefFractions6;
		predefFractions[7] = predefFractions7;
		predefFractions[8] = predefFractions8;
		predefFractions[9] = predefFractions9;
		predefFractions[10] = predefFractions10;
	}
}