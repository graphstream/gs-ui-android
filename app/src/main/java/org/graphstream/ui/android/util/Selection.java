package org.graphstream.ui.android.util;

import org.graphstream.ui.android.renderer.SelectionRenderer;
import org.graphstream.ui.geom.Point3;

public class Selection {
	
	private boolean active = false;
	private Point3 lo = new Point3() ;
	private Point3 hi = new Point3() ;
	private SelectionRenderer renderer = null ;
	
	public void begins(double x, double y) {
		lo.x = x ;
		lo.y = y ;
		hi.x = x ;
		hi.y = y ;
	}
	
	public void grows(double x, double y) {
		hi.x = x ;
		hi.y = y ;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public float x1() {
		return (float)lo.x ;
	}
	
	public float x2() {
		return (float)hi.x ;
	}
	
	public float y1() {
		return (float)lo.y ;
	}
	
	public float y2() {
		return (float)hi.y ;
	}
	
	public float z1() {
		return (float)lo.z ;
	}
	
	public float z2() {
		return (float)hi.z ;
	}

	public SelectionRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(SelectionRenderer renderer) {
		this.renderer = renderer;
	}
}
