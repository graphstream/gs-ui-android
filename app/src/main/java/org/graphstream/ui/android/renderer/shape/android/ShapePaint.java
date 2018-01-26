package org.graphstream.ui.android.renderer.shape.android;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import org.graphstream.ui.android.renderer.shape.android.baseShapes.Form;
import org.graphstream.ui.android.util.Background;
import org.graphstream.ui.graphicGraph.stylesheet.Colors;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.ImageCache;

public interface ShapePaint {

	public static float[] predefFractions2 = {0f, 1f} ;
	public static float[] predefFractions3 = {0f, 0.5f, 1f};
	public static float[] predefFractions4 = {0f, 0.33f, 0.66f, 1f };
	public static float[] predefFractions5 = {0f, 0.25f, 0.5f, 0.75f, 1f };
	public static float[] predefFractions6 = {0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f };
	public static float[] predefFractions7 = {0f, 0.1666f, 0.3333f, 0.4999f, 0.6666f, 0.8333f, 1f };
	public static float[] predefFractions8 = {0f, 0.1428f, 0.2856f, 0.4284f, 0.5712f, 0.7140f, 0.8568f, 1f };
	public static float[] predefFractions9 = {0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, .75f, 0.875f, 1f };
	public static float[] predefFractions10= {0f, 0.1111f, 0.2222f, 0.3333f, 0.4444f, 0.5555f, 0.6666f, 0.7777f, 0.8888f, 1f };

	public static float[][] predefFractions = {null, null, predefFractions2, predefFractions3, predefFractions4, predefFractions5,
			predefFractions6, predefFractions7, predefFractions8, predefFractions9, predefFractions10};

	public static ShapePaint apply(Style style) {
		return ShapePaint.apply(style, false);
	}

	public static ShapePaint apply(Style style, boolean forShadow) {
		if( forShadow ) {
			switch (style.getShadowMode()) {
				case GRADIENT_VERTICAL:
					return new ShapeVerticalGradientPaint(createColors( style, true ), createFractions( style, true ) );
				case GRADIENT_HORIZONTAL:
					return new ShapeHorizontalGradientPaint(createColors( style, true ), createFractions( style, true ) );
				case GRADIENT_DIAGONAL1:
					return new ShapeDiagonal1GradientPaint(createColors( style, true ), createFractions( style, true ) );
				case GRADIENT_DIAGONAL2:
					return new ShapeDiagonal2GradientPaint(createColors( style, true ), createFractions( style, true ) );
				case GRADIENT_RADIAL:
					return new ShapeRadialGradientPaint(createColors( style, true ), createFractions( style, true ) );
				case PLAIN:
					return new ShapePlainColorPaint(ColorManager.getShadowColor(style, 0));
				case NONE:
					return null;
				default:
					return null;
			}
		}
		else {
			switch (style.getFillMode()) {
				case GRADIENT_VERTICAL:
					return new ShapeVerticalGradientPaint(createColors( style, false ), createFractions( style, false ) );
				case GRADIENT_HORIZONTAL:
					return new ShapeHorizontalGradientPaint(createColors( style, false ), createFractions( style, false ) );
				case GRADIENT_DIAGONAL1:
					return new ShapeDiagonal1GradientPaint(createColors( style, false ), createFractions( style, false ) );
				case GRADIENT_DIAGONAL2:
					return new ShapeDiagonal2GradientPaint(createColors( style, false ), createFractions( style, false ) );
				case GRADIENT_RADIAL:
					return new ShapeRadialGradientPaint(createColors( style, false ), createFractions( style, false ) );
				case DYN_PLAIN:
					return new ShapeDynPlainColorPaint(createColors( style, false ));
				case PLAIN:
					return new ShapePlainColorPaint(ColorManager.getFillColor(style, 0));
				case IMAGE_TILED:
					return new ShapeImageTiledPaint(style.getFillImage());
				case IMAGE_SCALED:
					return new ShapeImageScaledPaint(style.getFillImage());
				case IMAGE_SCALED_RATIO_MAX:
					return new ShapeImageScaledRatioMaxPaint(style.getFillImage());
				case IMAGE_SCALED_RATIO_MIN:
					return new ShapeImageScaledRatioMinPaint(style.getFillImage());
				case NONE:
					return null;
				default:
					return null;
			}
		}
	}

	/**
	 * An array of floats regularly spaced in range [0,1], the number of floats is given by the
	 * style fill-color count.
	 * @param style The style to use.
	 */
	static float[] createFractions(Style style, Boolean forShadow) {
		if( forShadow )
			return createFractions( style, style.getShadowColorCount() );
		else
			return createFractions( style, style.getFillColorCount() );
	}

	static float[] createFractions(Style style, int n) {
		if( n < predefFractions.length ) {
			return predefFractions[n];
		}
		else {
			float[] fractions = new float[n];
			float div = 1f / (n - 1);

			for( int i = 0 ; i < n ; i++ )
				fractions[i] = div * i;

			fractions[0] = 0f ;
			fractions[n-1] = 1f ;

			return fractions;
		}
	}

	/**
	 * The array of colors in the fill-color property of the style.
	 * @param style The style to use.
	 */
	static int[] createColors( Style style, boolean forShadow ) {
		if( forShadow )
			return createColors( style, style.getShadowColorCount(), style.getShadowColors() );
		else
			return createColors( style, style.getFillColorCount(),   style.getFillColors() );
	}

	static int[] createColors( Style style, int n, Colors theColors ) {
		int[] colors = new int[n];

		for (int i = 0 ; i < theColors.size() ; i++) {
			colors[i] = ColorManager.getColor(theColors.get(i)) ;
		}

		return colors ;
	}

	static int interpolateColor( Colors colors, double value ) {
		int n = colors.size();
		int c = ColorManager.getColor(colors.get(0));

		if( n > 1 ) {
			double v = value ;
			if( value < 0 )
				v = 0;
			else if( value > 1 )
				v = 1;


			if( v == 1 ) {
				c = ColorManager.getColor(colors.get(n-1));	// Simplification, faster.
			}
			else if( v != 0 ) {	// If value == 0, color is already set above.
				double div = 1.0 / (n-1);
				int col = (int) ( value / div ) ;

				div = ( value - (div*col) ) / div ;

                org.graphstream.ui.graphicGraph.stylesheet.Color color0 =
                        ColorManager.getGraphstreamColor(ColorManager.getColor(colors.get( col )));
                org.graphstream.ui.graphicGraph.stylesheet.Color color1 =
                        ColorManager.getGraphstreamColor(ColorManager.getColor(colors.get( col + 1 )));
                double red = ((color0.getRed() * (1 - div)) + (color1
                        .getRed() * div)) ;
                double green = ((color0.getGreen() * (1 - div)) + (color1
                        .getGreen() * div)) ;
                double blue = ((color0.getBlue() * (1 - div)) + (color1
                        .getBlue() * div)) ;
                double alpha = ((color0.getAlpha() * (1 - div)) + (color1
                        .getAlpha() * div)) ;

                c = ColorManager.getColor(new org.graphstream.ui.graphicGraph.stylesheet.Color(
                        (int)red, (int)green, (int)blue, (int)alpha));
			}
		}

		return c ;
	}

	default int interpolateColor( int[] colors, double value ) {
		int n = colors.length;
		int c = colors[0];

		if( n > 1 ) {
			double v = value ;
			if( value < 0 )
				v = 0;
			else if( value > 1 )
				v = 1;


			if( v == 1 ) {
				c = colors[n-1];	// Simplification, faster.
			}
			else if( v != 0 ) {	// If value == 0, color is already set above.
				double div = 1.0 / (n-1);
				int col = (int) ( value / div ) ;

				div = ( value - (div*col) ) / div ;

                org.graphstream.ui.graphicGraph.stylesheet.Color color0 =
                        ColorManager.getGraphstreamColor(colors[ col ]);
                org.graphstream.ui.graphicGraph.stylesheet.Color color1 =
                        ColorManager.getGraphstreamColor(colors[ col + 1 ]);
                double red = ((color0.getRed() * (1 - div)) + (color1
                        .getRed() * div)) ;
                double green = ((color0.getGreen() * (1 - div)) + (color1
                        .getGreen() * div)) ;
                double blue = ((color0.getBlue() * (1 - div)) + (color1
                        .getBlue() * div)) ;
                double alpha = ((color0.getAlpha() * (1 - div)) + (color1
                        .getAlpha() * div)) ;

                c = ColorManager.getColor(new org.graphstream.ui.graphicGraph.stylesheet.Color(
                        (int)red, (int)green, (int)blue, (int)alpha));
			}
		}

		return c ;
	}

	public abstract class ShapeAreaPaint extends Area implements ShapePaint {
		public abstract Background paint(double xFrom, double yFrom, double xTo, double yTo, double px2gu ) ;
		public Background paint(Form shape, double px2gu) {
			RectF s = shape.getBounds();

			return paint(s.left, s.top, s.right, s.bottom, px2gu) ;
		}
	}

	public abstract class ShapeColorPaint implements ShapePaint {
		public abstract Background paint(double value, int optColor);
	}


	public abstract class ShapeGradientPaint extends ShapeAreaPaint {
		protected int[] colors ;
		protected float[] fractions ;

		public ShapeGradientPaint(int[] colors , float[] fractions) {
			this.colors = colors;
			this.fractions = fractions;
		}

		public Background paint(double xFrom, double yFrom, double xTo, double yTo, double px2gu ) {
			if( colors.length > 1 ) {
				double x0 = xFrom;
				double y0 = yFrom;
				double x1 = xTo;
				double y1 = yTo;

				if( x0 > x1 ) { double tmp = x0; x0 = x1; x1 = tmp ;}
				if( y0 > y1 ) { double tmp = y0; y0 = y1; y1 = tmp ;}
				if( x0 == x1 ) { x1 = x0 + 0.001f ;}
				if( y0 == y1 ) { y1 = y0 + 0.001f ;}

				return realPaint( x0, y0, x1, y1 );
			}
			else {
				if( colors.length > 0 )
					return new Background( colors[0] );
				else
					return new Background( Color.rgb(255, 255, 255) );
			}
		}

		public abstract Background realPaint( double x0, double y0, double x1, double y1) ;
	}

	public class ShapeVerticalGradientPaint extends ShapeGradientPaint {
		public ShapeVerticalGradientPaint( int[] colors, float[] fractions) {
			super(colors, fractions);
		}

		public Background realPaint( double x0, double y0, double x1, double y1 ) {
			return new Background( new LinearGradient( (float)x0, (float)y0, (float)x0, (float)y1, colors, fractions, Shader.TileMode.CLAMP ));
		}
	}

	public class ShapeHorizontalGradientPaint extends ShapeGradientPaint {
		public ShapeHorizontalGradientPaint( int[] colors, float[] fractions) {
			super(colors, fractions);
		}

		public Background realPaint( double x0, double y0, double x1, double y1 ) {
			return new Background( new LinearGradient( (float)x0, (float)y0, (float)x1, (float)y0, colors, fractions, Shader.TileMode.CLAMP ));
		}
	}

	public class ShapeDiagonal1GradientPaint extends ShapeGradientPaint {
		public ShapeDiagonal1GradientPaint( int[] colors, float[] fractions) {
			super(colors, fractions);
		}

		public Background realPaint( double x0, double y0, double x1, double y1 ) {
			return new Background( new LinearGradient( (float)x0, (float)y0, (float)x1, (float)y1, colors, fractions, Shader.TileMode.CLAMP ));
		}
	}

	public class ShapeDiagonal2GradientPaint extends ShapeGradientPaint {
		public ShapeDiagonal2GradientPaint( int[] colors, float[] fractions) {
			super(colors, fractions);
		}

		public Background realPaint( double x0, double y0, double x1, double y1 ) {
			return new Background( new LinearGradient( (float)x0, (float)y1, (float)x1, (float)y0, colors, fractions, Shader.TileMode.CLAMP ));
		}
	}

	public class ShapeRadialGradientPaint extends ShapeGradientPaint {
		public ShapeRadialGradientPaint( int[] colors, float[] fractions) {
			super(colors, fractions);
		}

		public Background realPaint( double x0, double y0, double x1, double y1 ) {
			double w = ( x1 - x0 ) / 2;
			double h = ( y1 - y0 ) / 2;
			double cx = x0 + w;
			double cy = y0 + h;

			float rad = (float) h ;
			if( w > h )
				rad = (float) w ;
			return new Background( new RadialGradient( (float)cx, (float)cy, rad, colors, fractions, Shader.TileMode.CLAMP ));
		}
	}

	public class ShapePlainColorPaint extends ShapeColorPaint {
		public int color;
		public ShapePlainColorPaint( int color ) {
			this.color = color ;
		}
		public Background paint( double value, int optColor ) { return new Background(this.color);}
	}

	public class ShapeDynPlainColorPaint extends ShapeColorPaint {
		public int[] colors;
		public ShapeDynPlainColorPaint( int[] colors ) {
			this.colors = colors ;
		}
		public Background paint( double value, int optColor ) {
			if(optColor != -1)
				return new Background( optColor );
			else
				return new Background(interpolateColor( colors, value ));
		}
	}

	public class ShapeImageTiledPaint extends ShapeAreaPaint {
		private String drawable ;

		public ShapeImageTiledPaint( String drawable ) {
			this.drawable = drawable ;
		}

        public Background paint( double xFrom, double yFrom, double xTo, double yTo, double px2gu ) {
            Bitmap img = ImageCache.loadImage(drawable) ;

            if (img != null) {
                return new Background( img, xFrom, yFrom, (int)(img.getWidth()/px2gu), (int)(-(img.getHeight()/px2gu)) );
            }
            else {
                img = ImageCache.dummyImage();
                return new Background( img, xFrom, yFrom, (int)(img.getWidth()*px2gu), (int)(-(img.getHeight()*px2gu)) );
            }
		}
	}

	public class ShapeImageScaledPaint extends ShapeAreaPaint {
		private String drawable ;

		public ShapeImageScaledPaint( String drawable ) {
			this.drawable = drawable ;
		}

		public Background paint( double xFrom, double yFrom, double xTo, double yTo, double px2gu ) {
			Bitmap img = ImageCache.loadImage(drawable) ;

			if (img != null) {
				return new Background( img, xFrom, yFrom, (int)(xTo-xFrom), (int)(-(yTo-yFrom)) );
			}
			else {
				img = ImageCache.dummyImage();
				return new Background( img, xFrom, yFrom, (int)(xTo-xFrom), (int)(-(yTo-yFrom)) ) ;
			}
		}
	}

	public class ShapeImageScaledRatioMaxPaint extends ShapeAreaPaint {
		private String drawable ;

		public ShapeImageScaledRatioMaxPaint( String drawable ) {
			this.drawable = drawable ;
		}

		public Background paint( double xFrom, double yFrom, double xTo, double yTo, double px2gu ) {
			Bitmap img = ImageCache.loadImage(drawable) ;

			if (img != null) {
				double w = xTo-xFrom;
				double h = yTo-yFrom;
				double ratioi = (double)img.getWidth() / (double)img.getHeight();
				double ration = w / h;

				if( ratioi > ration ) {
					double neww = h * ratioi;
					return new Background( img, xFrom-((neww-w)/2), yFrom, (int)neww, (int)-h );
				} else {
					double newh = w / ratioi;
					return new Background( img, xFrom, yFrom-((newh-h)/2), (int)w, (int)-newh );
				}
			}
			else {
				img = ImageCache.dummyImage();
				return new Background( img, xFrom, yFrom, (int)(xTo-xFrom), (int)(-(yTo-yFrom)) );
			}
		}
	}

	public class ShapeImageScaledRatioMinPaint extends ShapeAreaPaint {
		private String drawable ;

		public ShapeImageScaledRatioMinPaint( String drawable ) {
			this.drawable = drawable ;
		}

		public Background paint(double xFrom, double yFrom, double xTo, double yTo, double px2gu ) {
			Bitmap img = ImageCache.loadImage(drawable) ;

			if (img != null) {
				double w = xTo-xFrom ;
				double h = yTo-yFrom ;
				double ratioi = (double)img.getWidth() / (double)img.getHeight();
				double ration = w / h ;

				if( ration > ratioi ) {
					double neww = h * ratioi ;
					return new Background( img, xFrom+((w-neww)/2), yFrom, (int)neww, (int)-h );
				} else {
					double newh = w / ratioi ;
					return new Background( img, xFrom, yFrom-((h-newh)/2), (int)w, (int)-newh );
				}
			}
			else {
				img = ImageCache.dummyImage();
				return new Background( img, xFrom, yFrom, (int)(xTo-xFrom), (int)(-(yTo-yFrom)) );
			}
		}
	}
}
