package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.android.shapePart.Fillable;
import org.graphstream.ui.android.renderer.shape.android.shapePart.Shadowable;
import org.graphstream.ui.android.renderer.shape.android.shapePart.Strokable;

public abstract class AreaConnectorShape extends ConnectorShape {
	
	public Fillable fillable ;
	public Strokable strokable ;
	public Shadowable shadowable ;
	
	public AreaConnectorShape() {
		this.fillable = new Fillable();
		this.strokable = new Strokable();
		this.shadowable = new Shadowable();
	}
	
	public void configureForGroup(Backend bck, Style style, DefaultCamera2D camera) {
		fillable.configureFillableForGroup(bck, style, camera);
		strokable.configureStrokableForGroup(style, camera);
		shadowable.configureShadowableForGroup(style, camera);
		super.configureForGroup(bck, style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, DefaultCamera2D camera) {
		fillable.configureFillableForElement(element.getStyle(), camera, element);
		super.configureForElement(bck, element, skel, camera);
	}
}
