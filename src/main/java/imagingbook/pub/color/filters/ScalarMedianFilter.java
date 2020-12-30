/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.color.filters;

import java.util.Arrays;

import imagingbook.lib.filters.GenericFilter2D;
import imagingbook.lib.image.access.ScalarAccessor;

/**
 * Ordinary (scalar) median filter for color images implemented
 * by extending the {@link GenericFilter2D} class.
 * Color images are filtered individually in all channels.
 * @author W. Burger
 * @version 2020/12/28
 */
public class ScalarMedianFilter extends GenericFilter2D {
	
	public static class Parameters {
		/** Filter radius */
		public double radius = 3.0;
	}
	
	final Parameters params;
	FilterMask mask;

	//-------------------------------------------------------------------------------------
	
	public ScalarMedianFilter() {
		this(new Parameters());
	}
	
	public ScalarMedianFilter(Parameters params) {
		this.params = params;
		this.mask = new FilterMask(params.radius);
	}
		
	//-------------------------------------------------------------------------------------

	@Override
	protected void filterScalar(ScalarAccessor source, ScalarAccessor target, int u, int v) {
		final int maskCount = mask.getCount();
		final float[] p = new float[maskCount];
		final int medianIndex = maskCount/2;
		final int maskCenter = mask.getCenter();
		final int[][] maskArray = mask.getMask();
		int k = 0;
		for (int i = 0; i < maskArray.length; i++) {
			int ui = u + i - maskCenter;
			for (int j = 0; j < maskArray[0].length; j++) {
				if (maskArray[i][j] > 0) {
					int vj = v + j - maskCenter;
					p[k] = source.getVal(ui, vj);
					k = k + 1;
				}
			}
		}
		Arrays.sort(p);
		target.setVal(u, v, p[medianIndex]);
		//return p[medianIndex];
	}

//	@Override
//	protected float[] filterVector(ImageAccessor source, int u, int v) {
//		final int maskCount = mask.getCount();
//		final float[] pR = new float[maskCount];
//		final float[] pG = new float[maskCount];
//		final float[] pB = new float[maskCount];
//		//final int[] pctr = new int[3];
//		//final float[] pF = new float[3];
//		final int medianIndex = maskCount/2;
//		final int maskCenter = mask.getCenter();
//		final int[][] maskArray = mask.getMask();
//		int k = 0;
//		for (int i=0; i<maskArray.length; i++) {
//			int ui = u + i - maskCenter;
//			for (int j=0; j<maskArray[0].length; j++) {
//				if (maskArray[i][j] > 0) {
//					int vj = v + j - maskCenter;
//					float[] pctr = source.getPix(ui,vj);
//					pR[k] = pctr[0];
//					pG[k] = pctr[1];
//					pB[k] = pctr[2];
//					k = k + 1;
//				}
//			}
//		}
//		Arrays.sort(pR); //pF[0] = pR[medianIndex];
//		Arrays.sort(pG); //pF[1] = pG[medianIndex];
//		Arrays.sort(pB); //pF[2] = pB[medianIndex];
//		return new float[] { pR[medianIndex], pG[medianIndex], pB[medianIndex] };
// 	}
}
