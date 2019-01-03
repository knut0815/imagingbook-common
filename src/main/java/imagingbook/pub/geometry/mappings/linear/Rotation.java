/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.pub.geometry.mappings.linear;

import imagingbook.lib.math.Matrix;
import imagingbook.lib.settings.PrintPrecision;

public class Rotation extends AffineMapping {
	
	/**
	 * Creates a 2D rotation around the origin.
	 * @param alpha rotation angle (in radians)
	 */
	public Rotation(double alpha) {
		super(
			 Math.cos(alpha), -Math.sin(alpha), 0,
			 Math.sin(alpha),  Math.cos(alpha), 0);
	}
	
	public Rotation(Rotation r) {
		super(r);
	}

	// private constructor (used for getInverse() only)
	private Rotation(double a00, double a01, double a10, double a11) {
		super(a00, a01, 0, a10, a11, 0);
	}
	
	@Override
	public Rotation duplicate() {
		return new Rotation(this);
	}
	
	@Override
	public AffineMapping getInverse() {
		return new Rotation(a00, -a01, -a10, a11);
	}
	
	// ----------------------------------------------------------------------
	
	/**
	 * For testing only.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		PrintPrecision.set(6);
		Rotation R = new Rotation(0.5);
		double[][] A = R.getTransformationMatrix();
		
		System.out.println("A = \n" + Matrix.toString(A));
		System.out.println();
		
		AffineMapping Ri = R.getInverse();
		double[][] Ai = Ri.getTransformationMatrix();
		
		System.out.println("Ai = \n" + Matrix.toString(Ai));
//		
		double[][] I = Matrix.multiply(A, Ai);
		System.out.println("\ntest: should be the  identity matrix: \n" + Matrix.toString(I));
	}
}



