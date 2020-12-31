/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.filters;

import java.awt.Color;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.access.FloatPixelPack;
import imagingbook.lib.image.access.OutOfBoundsStrategy;
import imagingbook.lib.math.VectorNorm;
import imagingbook.lib.math.VectorNorm.NormType;

/**
 * Basic vector median filter for color images implemented
 * by extending the {@link GenericFilterVector} class.
 * 
 * @author W. Burger
 * @version 2020/12/31
 */
public class VectorMedianFilter extends GenericFilterVector {
	
	public static Color ModifiedColor = Color.black;
	
	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
		/** Distance norm to use */
		public NormType distanceNorm = NormType.L1;
		/** For testing only */
		public boolean markModifiedPixels = false;
		/** For testing only */
		public boolean showMask = false;
		/** Out-of-bounds strategy */
		public OutOfBoundsStrategy obs = OutOfBoundsStrategy.NEAREST_BORDER;
	}
	
	private final Parameters params;
	private final int[] modColor = {ModifiedColor.getRed(), ModifiedColor.getGreen(), ModifiedColor.getBlue()};
	private final FilterMask mask;
	private final int[][] supportRegion;		// supportRegion[i][c] with index i, color component c
	private final VectorNorm vNorm;
	
	public int modifiedCount = 0; // for debugging??
	
	//-------------------------------------------------------------------------------------
	
	public VectorMedianFilter(ImageProcessor ip) {	
		this(ip, new Parameters());
	}
	
	public VectorMedianFilter(ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.params = params;
		this.mask = new FilterMask(params.radius);
		this.supportRegion = new int[mask.getCount()][3];
		this.vNorm = params.distanceNorm.create();
		if (params.showMask) mask.show("Mask");
	}
	
	//-------------------------------------------------------------------------------------
	
	@Override
	protected float[] filterPixel(FloatPixelPack sources, int u, int v) {
		final int[] pCtr = new int[3];		// center pixel
		final float[] pCtrf = sources.getPixel(u, v);
		copyRgbTo(pCtrf, pCtr);
		getSupportRegion(sources, u, v);	// TODO: check method!
		double dCtr = aggregateDistance(pCtr, supportRegion); 
		double dMin = Double.MAX_VALUE;
		int jMin = -1;
		for (int j = 0; j < supportRegion.length; j++) {
			int[] p = supportRegion[j];
			double d = aggregateDistance(p, supportRegion);
			if (d < dMin) {
				jMin = j;
				dMin = d;
			}
		}
		int[] pmin = supportRegion[jMin];
		// modify this pixel only if the min aggregate distance of some
		// other pixel in the filter region is smaller
		// than the aggregate distance of the original center pixel:
		final float[] pF = new float[3];	// the returned color tupel
		if (dMin < dCtr) {	// modify this pixel
			if (params.markModifiedPixels) {
				copyRgbTo(modColor, pF);
				modifiedCount++;
			}
			else {
				copyRgbTo(pmin, pF);
			}
		}
		else {	// keep the original pixel value
			copyRgbTo(pCtr, pF);
		}
		return pF;
	}
	
	private int[][] getSupportRegion(FloatPixelPack src, int u, int v) {
		//final int[] p = new int[3];
		// fill 'supportRegion' for current mask position
		int n = 0;
		final int[][] maskArray = mask.getMask();
		final int maskCenter = mask.getCenter();
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					final float[] p = src.getPixel(ui, vj);
					copyRgbTo(p, supportRegion[n]);
					n = n + 1;
				}
			}
		}
		return supportRegion;
	}
	
	private void copyRgbTo(float[] source, int[] target) {
		target[0] = (int) source[0];
		target[1] = (int) source[1];
		target[2] = (int) source[2];
	}
	
	private void copyRgbTo(int[] source, float[] target) {
		target[0] = source[0];
		target[1] = source[1];
		target[2] = source[2];
	}
	
	// find the color with the smallest summed distance to all others.
	// brute force and thus slow
	private double aggregateDistance(int[] p, int[][] P) {
		double d = 0;
		for (int i = 0; i < P.length; i++) {
			d = d + vNorm.distance(p, P[i]);
		}
		return d;
	}

}
