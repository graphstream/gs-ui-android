package org.graphstream.ui.android.renderer.shape.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import org.graphstream.ui.android.Backend;
import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.android.util.Font;
import org.graphstream.ui.android.util.FontCache;
import org.graphstream.ui.android_viewer.util.ImageCache;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.IconMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.view.camera.DefaultCamera2D;

public abstract class IconAndText {
    /** Overall width of the icon and text with all space and padding included. */
    //protected double width;
    /** Overall height of the icon and text with all space and padding included. */
    //protected double height;
    /** Overall descent of the icon and text with all space and padding included. */
    protected double descent;
    /** Overall ascent of the icon and text with all space and padding included. */
    protected double ascent ;
    protected TextBox text;
    protected double offx;
    protected double offy;
    protected double padx;
    protected double pady;

    public IconAndText(TextBox text, double offx, double offy, double padx, double pady) {
        this.descent = text.getDescent() ;
        this.ascent = text.getAscent();		this.text = text ;
        this.offx = offx ;
        this.offy = offy ;
        this.padx = padx ;
        this.pady = pady ;
    }

    public static IconAndText apply(Style style, DefaultCamera2D camera, GraphicElement element) {
        Bitmap icon = null ;
        TextBox text = TextBox.apply(camera, style);
        Values padd = style.getPadding();
        Values off = style.getTextOffset();
        double padx = camera.getMetrics().lengthToPx(padd, 0);
        double pady = padx ;
        if ( padd.size() > 1 )
            pady = camera.getMetrics().lengthToPx(padd, 1);
        double offx = camera.getMetrics().lengthToPx(off, 0);
        double offy = padx;
        if ( padd.size() > 1 )
            offy = camera.getMetrics().lengthToPx(off, 1);

        if( style.getIconMode() != IconMode.NONE ) {
            String url = style.getIcon();

            if( url.equals( "dynamic" ) ) {
                if( element.hasLabel( "ui.icon" ) )
                    url = element.getLabel( "ui.icon" ).toString();
                else
                    url = null;
            }

            if( url != null ) {
                icon = ImageCache.loadImage(url);
            }
        }

        if (icon == null) {
            return new IconAndTextOnlyText(text, offx, offy, padx, pady);
        }
        else {
            switch (style.getIconMode()) {
                case AT_LEFT:
                    return new IconAtLeftAndText( icon, text, offx, offy, padx, pady );
                case AT_RIGHT:
                    return new IconAtLeftAndText( icon, text, offx, offy, padx, pady );
                case ABOVE:
                    return new IconAtLeftAndText( icon, text, offx, offy, padx, pady );
                case UNDER:
                    return new IconAtLeftAndText( icon, text, offx, offy, padx, pady );
                default:
                    throw new RuntimeException("???");
            }
        }
    }

    public abstract void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom) ;
    public abstract void setIcon(Backend backend, String url) ;
    public abstract void setText(Backend backend, String text);
    public abstract double getWidth();
    public abstract double getHeight();
    public abstract String getText(Backend backend);
}

class IconAndTextOnlyText extends IconAndText {
    public IconAndTextOnlyText(TextBox text, double offx, double offy, double padx, double pady ) {
        super(text, offx, offy, padx, pady);
    }

    public double getWidth() {
        return text.getWidth()+padx*2;
    }

    public double getHeight() {
        return text.getAscent()+text.getDescent()+pady*2 ;
    }

    public void setText(Backend backend, String text) {
        this.text.setText(text, backend);
    }

    public String getText(Backend backend) {
        return this.text.getText();
    }

    public void setIcon(Backend backend, String url) {}

    public void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom) {
        this.text.render(backend, offx+xLeft, offy+yBottom - descent);
    }
}

class IconAtLeftAndText extends IconAndText {
    private Bitmap icon ;

    public IconAtLeftAndText(Bitmap icon, TextBox text, double offx, double offy, double padx, double pady ) {
        super(text, offx, offy, padx, pady);
        //this.width = text.getWidth() + icon.getWidth(null) + 5 + padx*2 ;
        //this.height = Math.max(icon.getHeight(null), text.ascent + text.descent) + pady*2;
        this.icon = icon ;
    }


    public void setText(Backend backend, String text) {
        this.text.setText(text, backend);
    }

    public String getText(Backend backend) {
        return this.text.getText();
    }

    public void setIcon(Backend backend, String url) {
        ImageCache.loadImage(url);
        if (icon == null) {
            icon = ImageCache.dummyImage();
        }
    }

    public void render(Backend backend, DefaultCamera2D camera, double xLeft, double yBottom) {
        Canvas g = backend.graphics2D();
        Matrix m = new Matrix();
        float[] trans = {1f, 0f, 0f, 1f, (float)(offx+xLeft), (float)(offy+(yBottom-(getHeight()/2))-(icon.getHeight()/2)+pady)} ;
        m.mapPoints(trans);
        g.drawBitmap(icon, m, ColorManager.paint);

        double th = text.getAscent() + text.getDescent();
        double dh = 0f ;
        if(icon.getHeight() > th)
            dh = ((icon.getHeight() - th) / 2f) ;

        this.text.render(backend, offx+xLeft + icon.getWidth() + 5, offy+yBottom - dh - descent);
    }

    public double getWidth() {
        return text.getWidth() + icon.getWidth() + 5 + padx*2;
    }


    public double getHeight() {
        return Math.max(icon.getHeight(), text.getAscent() + text.getDescent()) + pady*2;
    }
}


/** A simple wrapper for a font and a text string. */
abstract class TextBox {
    /** The text string. */
    String textData;

    /** Renders the text at the given coordinates. */
    public abstract void render(Backend backend, double xLeft, double yBottom);
    /** Set the text string to paint. */
    public abstract void setText(String text, Backend backend);
    public abstract String getText();

    public abstract double getWidth();
    public abstract double getHeight();
    public abstract double getDescent();
    public abstract double getAscent();
//	public abstract double getAscentDescent();


    /**
     * Factory companion object for text boxes.
     */
//	static FontRenderContext defaultFontRenderContext = new FontRenderContext(new AffineTransform(), true, true);

    public static TextBox apply(DefaultCamera2D camera, Style style) {
        String fontName  = style.getTextFont();
        TextStyle fontStyle = style.getTextStyle();
        Value fontSize  = style.getTextSize();
        int textColor = ColorManager.getTextColor(style, 0);
        int bgColor = -1;
        boolean rounded = false;

        switch (style.getTextBackgroundMode()) {
            case NONE: break;
            case PLAIN:
                rounded = false;
                bgColor = ColorManager.getTextBackgroundColor(style, 0);
                break;
            case ROUNDEDBOX:
                rounded = true;
                bgColor = ColorManager.getTextBackgroundColor(style, 0);
                break;
            default: break;
        }

        Values padding = style.getTextPadding();
        double padx = camera.getMetrics().lengthToPx(padding, 0);
        double pady = padx ;
        if(padding.size() > 1)
            camera.getMetrics().lengthToPx(padding, 1);

        return TextBox.apply(fontName, fontStyle, (int)fontSize.value, textColor, bgColor, rounded, padx, pady);
    }

    public static TextBox apply(String fontName, TextStyle style, int fontSize, int textColor, int bgColor,
                                boolean rounded, double padx, double pady) {
        return new AndroidTextBox(FontCache.getFont( fontName, style, fontSize ), textColor, bgColor, rounded, padx, pady);
    }
}

class AndroidTextBox extends TextBox {

    Font font;
    int textColor;
    int bgColor;
    boolean rounded;
    double padx;
    double pady;

    Rect bounds ;

    public AndroidTextBox(Font font, int textColor, int bgColor, boolean rounded, double padx, double pady) {
        this.font = font ;
        this.textColor = textColor ;
        this.bgColor = bgColor ;
        this.rounded = rounded ;
        this.padx = padx ;
        this.pady = pady ;

        this.textData = null ;
        this.bounds = new Rect(0, 0, 0, 0);
    }


    /** Changes the text and compute its bounds. This method tries to avoid recomputing bounds
     *  if the text does not really changed. */
    public void setText(String text, Backend backend) {
        if(text != null && text.length() > 0) {
            if (textData != text || !textData.equals(text)) {
                this.textData = text ;
                ColorManager.paint.getTextBounds(textData, 0, textData.length(), this.bounds);
            }
            else {
                this.textData = null ;
                this.bounds = new Rect(0, 0, 0, 0);
            }
        }
    }

    @Override
    public String getText() {
        return textData;
    }

    public double getWidth() {
        if ( bounds != null )
            return bounds.right-bounds.left ;
        else
            return 0 ;
    }

    public double getHeight() {
        if ( bounds != null )
            return bounds.bottom-bounds.top ;
        else
            return 0 ;
    }

    /**
     *  The logical bounds are based on font metrics information.
     *  The width is based on the glyph advances and the height on the ascent, descent, and line gap.
     *  Except for the last line which does not include the line gap.
     * @return
     */
    @Override
    public double getAscent() {
        if ( bounds != null ) {
            return bounds.top ;
        }
        else
            return 0 ;
    }

    @Override
    public double getDescent() {
        if ( bounds != null ) {
            return bounds.bottom;
        }
        else
            return 0 ;
    }


    public void render(Backend backend, double xLeft, double yBottom) {

        if ( bounds != null ) {
            Canvas g = backend.graphics2D();

            if (bgColor != -1) {
                double a = getAscent() ;
                double h = a + getDescent() ;

                ColorManager.paint.setColor(bgColor);
                ColorManager.paint.setStyle(Paint.Style.FILL);
                if(rounded) {
                    g.drawRoundRect((float)(xLeft-padx), (float)(yBottom-(a+pady)), (float)(getWidth()+1+(padx+padx)), (float)(h+(pady+pady)), 6, 6, ColorManager.paint);
                } else {
                    g.drawRect((float)(xLeft-padx), (float)(yBottom-(a+pady)), (float)(getWidth()+1+(padx+padx)), (float)(h+(pady+pady)), ColorManager.paint);
                }
            }
            ColorManager.paint.setColor(textColor);
            ColorManager.paint.setTextSize(font.getSizeFont());
            ColorManager.paint.setTypeface(font.getFont());

            g.drawText(textData, (float)xLeft, (float)yBottom, ColorManager.paint);
        }
    }
}