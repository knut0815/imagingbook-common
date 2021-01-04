/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.edgepreservingfilters;

import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.image.access.PixelPack.PixelSlice;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;


/**
 * Scalar version, without gradient array.
 * 
 * This code is based on the Anisotropic Diffusion filter proposed by Perona and Malik,
 * as proposed in Pietro Perona and Jitendra Malik, "Scale-space and edge detection 
 * using anisotropic diffusion", IEEE Transactions on Pattern Analysis 
 * and Machine Intelligence, vol. 12, no. 4, pp. 629-639 (July 1990).
 * 
 * The filter operates on all types of grayscale (scalar) and RGB color images.
 * This class is based on the ImageJ API and intended to be used in ImageJ plugins.
 * How to use: consult the source code of the related ImageJ plugins for examples.
 * 
 * @author W. Burger
 * @version 2021/01/02
 */
public class PeronaMalikFilterScalar extends GenericFilterScalar {
	
	private final int T; 		// number of iterations
	private final float alpha;
	private final ConductanceFunction g;
	
	// constructor - using default parameters
	public PeronaMalikFilterScalar (ImageProcessor ip) {
		this(ip, new Parameters());
	}
	
	// constructor - use this version to set all parameters
	public PeronaMalikFilterScalar (ImageProcessor ip, Parameters params) {
		super(ip, params.obs);
		this.T = params.iterations;
		this.alpha = params.alpha;
		this.g = ConductanceFunction.get(params.smoothRegions, params.kappa);
	}
	
	// ------------------------------------------------------
	
	@Override
	protected float filterPixel(PixelSlice source, int u, int v) {
		/*   
		 *  NH pixels:      directions:
		 *      p4              3
		 *   p3 p0 p1        2  x  0
		 *      p2              1
		 */
		float[] p = new float[5];
		p[0] = source.getVal(u, v);
		p[1] = source.getVal(u + 1, v);
		p[2] = source.getVal(u, v + 1);
		p[3] = source.getVal(u - 1, v);
		p[4] = source.getVal(u, v - 1);
			
		float result = p[0];
		for (int i = 1; i <= 4; i++) {
			float d = p[i] - p[0];
			result = result + alpha * (g.eval(Math.abs(d))) * (d);
		}
		
		return result;
	}

	@Override
	protected final boolean finished() {
		return (getPass() >= T);	// this filter needs T passes
	}

}
