/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.filters;

import imagingbook.lib.image.access.ScalarAccessor;

/**
 * Generic linear convolution filter implemented
 * by extending the {@link GenericFilter2D} class.
 * If applied to a vector-valued image (e.g. an RGB image) this
 * scalar filter is applied to each component, which is
 * automatically handled by {@link GenericFilter2D}.
 * Thus only the scalar version of the filter needs to be defined
 * (see  {@link #filterScalar(ScalarAccessor, int, int)}).
 * 
 * @author WB
 * @version 2020/12/29
 */
public class LinearFilter2D extends GenericFilter2D {
	
	private final float[][] H;		// the kernel
	private final int width, height;		// width/height of the kernel
	private final int xc, yc;		// 'hot spot' coordinates
	
	/**
	 * The preferred constructor.
	 * @param kernel the 2D filter (convolution) kernel
	 */
	public LinearFilter2D(Kernel2D kernel) {
		this.H = kernel.getH();
		this.width = kernel.getWidth();
		this.height = kernel.getHeight();
		this.xc = kernel.getXc();
		this.yc = kernel.getYc();
	}
	
	// --------------------------------------------------------------
	
	@Override
	protected float filterScalar(ScalarAccessor ia, final int u, final int v) {
		float sum = 0;
		for (int j = 0; j < height; j++) {
			int vj = v + j - yc;
			for (int i = 0; i < width; i++) {
				int ui = u + i - xc;
				sum = sum + ia.getVal(ui, vj) * H[i][j];
			}
		}
 		return sum;
	}
	
	// --------------------------------------------------------------
	
	/**
	 * Returns the kernel of this filter as a 2D {@code float} array.
	 * Provided for sub-classes who create their own kernel
	 * (e.g., {@link GaussianFilter}).
	 * 
	 * @return the filter kernel (no copy)
	 */
	public float[][] getKernel() {
		return this.H;
	}
}
