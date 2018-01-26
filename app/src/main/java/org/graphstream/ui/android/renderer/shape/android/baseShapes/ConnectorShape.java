package org.graphstream.ui.android.renderer.shape.android.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.android.Backend;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.android.renderer.ConnectorSkeleton;
import org.graphstream.ui.android.renderer.Skeleton;
import org.graphstream.ui.android.renderer.shape.Connector;
import org.graphstream.ui.android.renderer.shape.Decorable;
import org.graphstream.ui.android.renderer.shape.Shape;

public abstract class ConnectorShape extends Connector implements Shape {
	
	public Decorable decorable ;
	
	public ConnectorShape() {
		this.decorable = new Decorable();
	}
	
	public void configureForGroup(Backend bck, Style style, DefaultCamera2D camera) {
		decorable.configureDecorableForGroup(style, camera);
		configureConnectorForGroup(style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, DefaultCamera2D camera) {
		decorable.configureDecorableForElement(bck, camera, element, skel);
		configureConnectorForElement(camera, (GraphicEdge)element, (ConnectorSkeleton)skel /* TODO check this ! */);
	}
}