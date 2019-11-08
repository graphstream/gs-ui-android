package org.graphstream.ui.android_viewer;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.graphstream.ui.view.View;


public abstract class ViewPanel extends SurfaceView implements SurfaceHolder.Callback, View {

    // Holder
    SurfaceHolder mSurfaceHolder;

    /**
     * The view identifier.
     */
    private final String id;

    protected Context context ;

    public ViewPanel (Context context, final String identifier) {
        super(context);
        this.context = context ;

        if (null == identifier || identifier.isEmpty()) {
            throw new IllegalArgumentException("View id cannot be null/empty.");
        }
        id = identifier;

        setWillNotDraw(false);

        this.mSurfaceHolder = getHolder();
        this.mSurfaceHolder.addCallback(this);
    }

    public String getIdView() {
        return id;
    }

    public SurfaceHolder getSurface() {
        return mSurfaceHolder ;
    }

    public abstract void enableMouseOptions();
}