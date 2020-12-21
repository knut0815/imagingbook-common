/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.regions;

import ij.IJ;
import ij.process.ByteProcessor;

/**
 * Binary region labeler based on a recursive flood filling
 * algorithm. 
 * 
 * @author WB
 * @version 2020/12/17
 */
public class SegmentationRecursive extends BinaryRegionSegmentation {

	/**
	 * Creates a new region labeling.
	 * 
	 * @param ip the binary input image with 0 values for background pixels and values &gt; 0
	 * for foreground pixels.
	 */
	public SegmentationRecursive(ByteProcessor ip) {
		this(ip, DEFAULT_NEIGHBORHOOD);
	}
	
	public SegmentationRecursive(ByteProcessor ip, NeighborhoodType nh) {
		super(ip, nh);
	}
	
	@Override
	protected boolean applySegmentation() {
		try{
			for (int v = 0; v < height; v++) {
				for (int u = 0; u < width; u++) {
					if (getLabel(u, v) == FOREGROUND) {	// = unlabeled foreground
						// start a new region
						int label = getNextLabel();
						floodFill(u, v, label);
					}
				}
			}
		} catch(StackOverflowError e) { 
//			IJ.error(SegmentationRecursive.class.getSimpleName(), 
//					"A StackOverflowError occurred!\n" + "Result is not valid!");
			return false;
		}
		return true;
	}

	private void floodFill(int x, int y, int label) {
		if ((x >= 0) && (x < width) && (y >= 0) && (y < height) && getLabel(x, y) == FOREGROUND) {
			setLabel(x, y, label);
			floodFill(x + 1, y, label);
			floodFill(x, y + 1, label);
			floodFill(x, y - 1, label);
			floodFill(x - 1, y, label);
			if (neighborhood == NeighborhoodType.N8) {
				floodFill(x + 1, y + 1, label);
				floodFill(x - 1, y + 1, label);
				floodFill(x + 1, y - 1, label);
				floodFill(x - 1, y - 1, label);
			}
		}
	}

}
