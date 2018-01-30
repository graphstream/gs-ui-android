/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.android_viewer.basicRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.graphstream.ui.android.util.ColorManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.Values;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SizeMode;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.GraphMetrics;

public class NodeRenderer extends ElementRenderer {
	protected GraphMetrics metrics;

	protected Values size;

	protected Ellipse shape;

	protected double width, height, w2, h2;

	@Override
	protected void setupRenderingPass(StyleGroup group, Canvas g, Paint p,
			Camera camera) {
		metrics = camera.getMetrics();
		configureText(group, camera);
	}

	@Override
	protected void pushDynStyle(StyleGroup group, Canvas g, Paint p, Camera camera,
			GraphicElement element) {
		int color = ColorManager.getFillColor(group, 0);

		if (element != null && group.getFillMode() == FillMode.DYN_PLAIN) {
			color = interpolateColor(group, element);
		}

		p.setColor(color);

		if (group.getSizeMode() == SizeMode.DYN_SIZE) {
			Object s = element.getAttribute("ui.size");

			if (s != null) {
				width = metrics.lengthToGu(StyleConstants.convertValue(s));
				height = width;
				w2 = width / 2;
				h2 = height / 2;
			} else {
				size = group.getSize();
				width = metrics.lengthToGu(size, 0);
				height = size.size() > 1 ? metrics.lengthToGu(size, 1) : width;
				w2 = width / 2;
				h2 = height / 2;
			}
		}
	}

	@Override
	protected void pushStyle(StyleGroup group, Canvas g, Paint p, Camera camera) {
		size = group.getSize();
		shape = new Ellipse();
		width = metrics.lengthToGu(size, 0);
		height = size.size() > 1 ? metrics.lengthToGu(size, 1) : width;
		w2 = width / 2;
		h2 = height / 2;

		int color = ColorManager.getFillColor(group, 0);

		p.setColor(color);
	}

	@Override
	protected void elementInvisible(StyleGroup group, Canvas g, Paint p,
			Camera camera, GraphicElement element) {
	}

	@Override
	protected void renderElement(StyleGroup group, Canvas g, Paint p, Camera camera,
			GraphicElement element) {
		GraphicNode node = (GraphicNode) element;

        shape.setFrame((float)(node.x - w2), (float)(node.y - h2), (float)((node.x - w2)+width), (float)((node.y - h2)+height));
		p.setStyle(Paint.Style.FILL);
		g.drawOval(shape.left, shape.top, shape.right, shape.bottom, p);
		renderText(group, g, p, camera, element);
	}

	class Ellipse {
		protected float left = 0;
		protected float top = 0;
		protected float right = 0;
		protected float bottom = 0;

		public void setFrame(float left, float top, float right, float bottom) {
			this.left = left ;
			this.top = top ;
			this.right = right ;
			this.bottom = bottom ;
		}
	}
}