/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import java.util.Deque;
import java.util.LinkedList;

import ij.process.ByteProcessor;
import imagingbook.pub.geometry.basic.Point;

/**
 * Binary region labeler based on a depth-first flood filling
 * algorithm (using a queue).
 * 
 * @author WB
 * @version 2020/04/01
 */
public class SegmentationDepthFirst extends BinaryRegionSegmentation {
	
	/**
	 * Creates a new depth-first region labeling.
	 * 
	 * @param ip the binary input image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SegmentationDepthFirst(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SegmentationDepthFirst(ByteProcessor ip, NeighborhoodType nh) {
		super(ip, nh);
	}
	
	@Override
	protected boolean applySegmentation() {
		for (int v = 0; v < height; v++) {
			for (int u = 0; u < width; u++) {
				if (getLabel(u, v) == FOREGROUND) {
					// start a new region
					int label = getNextLabel();
					//IJ.log(String.format("assigning label %d at (%d,%d), maxLabel=%d", label, u, v, maxLabel));
					floodFill(u, v, label);
				}
			}
		}
		return true;
	}

	private void floodFill(int u, int v, int label) {
		Deque<Point> S = new LinkedList<Point>();	//stack contains pixel coordinates
		S.push(Point.create(u, v));
		while (!S.isEmpty()){
			Point p = S.pop();
			int x = (int) p.getX();
			int y = (int) p.getY();
			if ((x >= 0) && (x < width) && (y >= 0) && (y < height)	&& getLabel(x, y) == FOREGROUND) {
				setLabel(x, y, label);
				S.push(Point.create(x + 1, y));
				S.push(Point.create(x, y + 1));
				S.push(Point.create(x, y - 1));
				S.push(Point.create(x - 1, y));
				if (neighborhood == NeighborhoodType.N8) {
					S.push(Point.create(x + 1, y + 1));
					S.push(Point.create(x - 1, y + 1));
					S.push(Point.create(x + 1, y - 1));
					S.push(Point.create(x - 1, y - 1));
				}
			}
		}
	}

}
