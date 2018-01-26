package org.graphstream.ui.android.util;

import android.graphics.Typeface;

public class Font {
    private Typeface font ;
    private float size ;

    public Font(Typeface font, float size) {
        this.font = font ;
        this.size = size ;
    }

    public Typeface getFont() {
        return font ;
    }

    public float getSizeFont() {
        return size ;
    }
}