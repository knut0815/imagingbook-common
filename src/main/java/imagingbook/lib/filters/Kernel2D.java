package imagingbook.lib.filters;

import imagingbook.lib.math.Matrix;

/**
 * This class represents a 2D filter kernel.
 * 
 * @author WB
 * @version 2020/12/29
 */
public class Kernel2D {
	
	private final float[][] H;
	private final int xc, yc;
	private final int width, height;
	
	/**
	 * Convenience constructor.
	 * Assumes that the kernel's hot spot is at its center,
	 * does normalization by default.
	 * 
	 * @param H the 2D kernel array
	 */
	public Kernel2D(float[][] H) {
		this(H, (H[0].length - 1) / 2, (H.length - 1) / 2, true);
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param H the 2D kernel array
	 * @param xc the x-coordinate of the kernel's hot spot, default is (width-1)/2
	 * @param yc the y-coordinate of the kernel's hot spot, default is (height-1)/2
	 * @param normalize if true the kernel is normalized (to sum = 1)
	 */
	public Kernel2D(float[][] H, int xc, int yc, boolean normalize) {
		this.H = (normalize) ? normalize(H) : Matrix.duplicate(H);
		this.width = H[0].length;
		this.height = H.length;
		this.xc = xc;
		this.yc = yc;
		validate();
	}
	
	private void validate() {
		if (!Matrix.isRectangular(H))
			throw new IllegalArgumentException("non-rectangular filter kernel");
		// TODO: should we check if xc/yc is inside the kernel? does it matter?
	}
	
	public float[][] getH() {
		return H;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getXc() {
		return xc;
	}
	
	public int getYc() {
		return yc;
	}
	
	// ----------------------------------------------------------
	
	/**
	 * Normalizes the specified array such that its sum becomes 1.
	 * Throws an exception if the array's sum is zero.
	 * 
	 * @param A a 2D array
	 * @return the normalized array
	 */
	public static float[][] normalize(float[][] A) throws ArithmeticException {
		float s = (float) (1.0 / Matrix.sum(A));
		if (!Double.isFinite(s))	// isZero(s)
			throw new ArithmeticException("zero kernel sum, cannot normalize");
		return Matrix.multiply(s, A);
	}

}
