package imagingbook.lib.image.data;

import java.util.Arrays;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.color.Rgb;
import imagingbook.lib.image.access.GridIndexer2D;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * This class defines a generic container for scalar and
 * vector-valued image data, using float-values throughout.
 * 
 * @author WB
 * @version 2021/01/12
 */
public class PixelPack {
	
	/** The default out-of-bounds strategy (see {@link OutOfBoundsStrategy}). */
	public static final OutOfBoundsStrategy DefaultOutOfBoundsStrategy = OutOfBoundsStrategy.NEAREST_BORDER;

	private final int depth;
	private final float[][] pixels;
	private final int length;
	private final GridIndexer2D indexer;
	
	// --------------------------------------------------------------------
	
	/**
	 * Constructor. Creates a blank (zero-valued) pack of pixel data.
	 * @param width the image width
	 * @param height the image height
	 * @param depth the number of channels (slices)
	 * @param obs the strategy to be used when reading from out-of-bounds coordinates
	 */
	public PixelPack(int width, int height, int depth, OutOfBoundsStrategy obs) {
		this.depth = depth;
		this.length = width * height;
		this.pixels = new float[depth][length];
		this.indexer = GridIndexer2D.create(width, height, obs);
	}
	
	/**
	 * Constructor. Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object. Uses
	 * {@link #DefaultOutOfBoundsStrategy} as the out-of-bounds strategy
	 * (see {@link OutOfBoundsStrategy}).
	 * @param ip the source image
	 */
	public PixelPack(ImageProcessor ip) {
		this(ip, DefaultOutOfBoundsStrategy);
	}
	
	/**
	 * Creates a pack of pixel data from the given 
	 * {@link ImageProcessor} object, using the specified out-of-bounds strategy.
	 * @param ip the source image
	 * @param obs the strategy to be used when reading from out-of-bounds coordinates
	 */
	public PixelPack(ImageProcessor ip, OutOfBoundsStrategy obs) {
		this(ip.getWidth(), ip.getHeight(), ip.getNChannels(), obs);
		copyFromImageProcessor(ip, this.pixels);
	}
	
	/**
	 * Constructor. Duplicates an existing {@link PixelPack} without copying 
	 * the contained pixel data.
	 * @param orig the original {@link PixelPack}
	 */
	public PixelPack(PixelPack orig) {
		this(orig, false);
	}
	
	/**
	 * Constructor. Duplicates an existing {@link PixelPack}, optionally copying 
	 * the contained pixel data.
	 * @param orig the original {@link PixelPack}
	 * @param copyData set true to copy pixel data
	 */
	public PixelPack(PixelPack orig, boolean copyData) {
		this(orig.getWidth(), orig.getHeight(), orig.getDepth(), orig.indexer.getOutOfBoundsStrategy());
		if (copyData) {
			orig.copyTo(this);
		}
	}
	
	// --------------------------------------------------------------------

	/**
	 * Returns the pixel data at the specified position as a float-vector.
	 * If the supplied array is non-null, it is filled in and returned,
	 * otherwise a new array is returned.
	 * The length of this array corresponds to the number of slices in this
	 * pixel pack.
	 * The values returned at out-of-bounds positions depends on this
	 * pixel-pack's out-of-bounds strategy.
	 * 
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals a suitable 
	 * @return the array of pixel data
	 */
	public float[] getPixel(int u, int v, float[] vals) {
		if (vals == null) 
			vals = new float[depth];
		final int i = indexer.getIndex(u, v);
		if (i < 0) {	// i = -1 --> default value (zero)
			Arrays.fill(vals, 0);
		}
		else {	
			for (int k = 0; k < depth; k++) {
				vals[k] = pixels[k][i];
			}
		}
		return vals;
	}
	
	// returns a new pixel array
	public float[] getPixel(int u, int v) {
		return getPixel(u, v, new float[depth]);
	}
	
	/**
	 * Sets the pixel data at the specified pixel position.
	 * The length of the value array corresponds to the number of slices in this
	 * pixel pack.
	 * @param u the x-position
	 * @param v the y-position
	 * @param vals a float vector with the values for this pixel
	 */
	public void setPixel(int u, int v, float ... vals) {
		final int i = indexer.getIndex(u, v);
		if (i >= 0) {
			for (int k = 0; k < depth && k < vals.length; k++) {
				pixels[k][i] = vals[k];
			}
		}
	}
	
	/**
	 * Copies the contents of one pixel pack to another.
	 * The involved pixel packs must have the same dimensions.
	 * @param other another pixel pack
	 */
	public void copyTo(PixelPack other) {
		if (!this.isCompatibleTo(other)) {
			throw new IllegalArgumentException("pixel packs of incompatible size, cannot copy");
		}
		for (int k = 0; k < this.depth; k++) {
			System.arraycopy(this.pixels[k], 0, other.pixels[k], 0, this.length);
		}
	}
	
	/**
	 * Checks is this pixel pack has the same dimensions as another
	 * pixel pack, i.e., can be copied to it.
	 * @param other the other pixel pack
	 * @return true if both have the same dimensions
	 */
	public boolean isCompatibleTo(PixelPack other) {
		if (this.pixels.length == other.pixels.length && this.length == other.length) { // TODO: check width/height too
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Returns a reference to the specified {@link PixelSlice}.
	 * An exception is thrown if the specified slice does not exist.
	 * @param k the slice index (0,...,K-1)
	 * @return a reference to the pixel slice
	 */
	public PixelSlice getSlice(int k) throws IllegalArgumentException {
		if (k < 0 || k >= depth) {
			throw new IllegalArgumentException("illegal slice number " + k);
		}
		return new PixelSlice(k);
	}
	
	/**
	 * Creates and returns a new {@link PixelSlice} with the same dimensions
	 * and out-of-bounds strategy as this {@link PixelPack}.
	 * @return a new pixel slice
	 */
	public PixelSlice getEmptySlice() {
		return new PixelSlice();
	}
	
	/**
	 * Returns a reference to this {@link PixelPack}'s internal data
	 * array, which is always two-dimensional:
	 * dimension 1 is the slice index,
	 * dimension 2 is the pixel index (each slice is a 1D array).
	 * @return the pixel pack's data array
	 */
	public float[][] getPixels() {
		return pixels;
	}
	
	/**
	 * Returns the width of the associated image.
	 * @return the image width
	 */
	public int getWidth() {
		return this.indexer.getWidth();
	}
	
	/**
	 * Returns the height of the associated image.
	 * @return the image height
	 */
	public int getHeight() {
		return this.indexer.getHeight();
	}
	
	/**
	 * Returns the depth (number of slices) of the associated image.
	 * @return the image depth
	 */
	public int getDepth() {
		return this.depth;
	}
	
	/**
	 * Returns the out-of-bounds strategy.
	 * @return the out-of-bounds strategy
	 */
	public OutOfBoundsStrategy getOutOfBoundsStrategy() {
		return this.indexer.getOutOfBoundsStrategy();
	}

	/**
	 * Sets all values of this pixel pack to zero.
	 */
	public void zero() {
		for (int k = 0; k < depth; k++) {
			getSlice(k).zero();
		}
	}
	
	/**
	 * Returns the pixel values in the 3x3 neighborhood around the
	 * specified position.
	 * The returned float-array has the structure {@code [x][y][k]},
	 * with x,y = 0,...,2  and k is the slice index.
	 * 
	 * @param uc the center x-position
	 * @param vc the center x-position
	 * @param nh a float array to be filled in (or null)
	 * @return the neighborhood array
	 */
	public float[][][] get3x3Neighborhood(int uc, int vc, float[][][] nh) {
		if (nh == null) 
			nh = new float[3][3][depth];
		for (int i = 0; i < 3; i++) {
			int u = uc - 1 + i;
			for (int j = 0; j < 3; j++) {
				int v = vc - 1 + j;
				nh[i][j] = getPixel(u, v);
			}
		}
		return nh;
	}
	
	/**
	 * Copies the contents of this pixel pack to the supplied {@link ImageProcessor}
	 * instance, if compatible. Otherwise an exception is thrown.
	 * 
	 * @param ip the target image processor
	 */
	public void copyToImageProcessor(ImageProcessor ip) throws IllegalArgumentException {
		if (this.getWidth() != ip.getWidth() || this.getHeight() != ip.getHeight()) {
			throw new IllegalArgumentException("cannot copy to image processor, incompatible width or height");
		}
		if (this.getDepth() != ip.getNChannels()) {
			throw new IllegalArgumentException("cannot copy to image processor, wrong depth");
		}
		copyToImageProcessor(this.pixels, ip);
	}
	
	// -------------------------------------------------------------------
	
	/**
	 * Inner class representing a single (scalar-valued) pixel slice of a 
	 * (vector-valued) {@link PixelPack}.
	 *
	 */
	public class PixelSlice {
		private final int idx;
		private final float[] vals;
		
		/**
		 * Constructor. Creates a pixel slice for the specified component.
		 * @param idx the slice (component) index
		 */
		PixelSlice(int idx) {
			this.idx = idx;
			this.vals = pixels[idx];
		}
		
		/** Constructor. Creates an empty (zero-values) pixel slice with the same
		 * properties as the containing pixel pack but not associated
		 * with it.
		 */
		PixelSlice() {
			this.idx = -1;
			this.vals = new float[length];
		}
		
		/**
		 * Returns the slice value for the specified image position.
		 * @param u the x-position
		 * @param v the y-position
		 * @return the slice value
		 */
		public float getVal(int u, int v) {
			int i = indexer.getIndex(u, v);
			return (i >= 0) ? vals[i] : 0;
		}
		
		/**
		 * Sets the slice value at the specified image position.
		 * @param u the x-position
		 * @param v the y-position
		 * @param val the new value
		 */
		public void setVal(int u, int v, float val) {
			int i = indexer.getIndex(u, v);
			if (i >= 0) {
				vals[i] = val;
			}
		}
		
		/**
		 * Returns the slice index for this pixel slice, i.e, the
		 * component number in the containing pixel pack.
		 * -1 is returned if the pixel slice is not associated with
		 * a pixel pack.
		 * @return the slice index
		 */
		public int getIndex() {
			return idx;
		}
		
		public float[] getPixels() {
			return vals;
		}
		
		public int getLength() {
			return vals.length;
		}
		
		public int getWidth() {
			return PixelPack.this.getWidth();
		}
		
		public int getHeight() {
			return PixelPack.this.getHeight();
		}
		
		public void zero() {
			Arrays.fill(this.vals, 0);
		}
		
		public void copyTo(PixelSlice other) {
			System.arraycopy(this.vals, 0, other.vals, 0, this.vals.length);
		}
		
		// returns nh[x][y]
		public float[][] get3x3Neighborhood(int uc, int vc, float[][] nh) {
			if (nh == null) 
				nh = new float[3][3];
			for (int i = 0; i < 3; i++) {
				int u = uc - 1 + i;
				for (int j = 0; j < 3; j++) {
					int v = vc - 1 + j;
					nh[i][j] = getVal(u, v);
				}
			}
			return nh;
		}
	}
	
	// -------------------------------------------------------------------
	
	public static void copyFromImageProcessor(ImageProcessor ip, float[][] P) {
		if (ip instanceof ByteProcessor)
			copyFromByteProcessor((ByteProcessor)ip, P);
		else if (ip instanceof ShortProcessor)
			copyFromShortProcessor((ShortProcessor)ip, P);
		else if (ip instanceof FloatProcessor)
			copyFromFloatProcessor((FloatProcessor)ip, P);
		else if (ip instanceof ColorProcessor)
			copyFromColorProcessor((ColorProcessor)ip, P);
		else 
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
	}
	
	public static void copyFromByteProcessor(ByteProcessor ip, float[][] P) {
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xff & pixels[i];
		}
	}
	
	public static void copyFromShortProcessor(ShortProcessor ip, float[][] P) {
		short[] pixels = (short[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			P[0][i] = 0xffff & pixels[i];
		}
	}
	
	public static void copyFromFloatProcessor(FloatProcessor ip, float[][] P) {
		float[] pixels = (float[]) ip.getPixels();
		System.arraycopy(pixels, 0, P[0], 0, pixels.length);
	}
	
	public static void copyFromColorProcessor(ColorProcessor ip, float[][] P) {
		final int[] pixels = (int[]) ip.getPixels();
		final int[] rgb = new int[3];
		for (int i = 0; i < pixels.length; i++) {
			Rgb.intToRgb(pixels[i], rgb);
			P[0][i] = rgb[0];
			P[1][i] = rgb[1];
			P[2][i] = rgb[2];
		}
	}
	
	// -------------------------------------------------------------------
	
//	public static float[][] fromImageProcessor(ImageProcessor ip) {
//		if (ip instanceof ByteProcessor)
//			return fromByteProcessor((ByteProcessor)ip);
//		if (ip instanceof ShortProcessor)
//			return fromShortProcessor((ShortProcessor)ip);
//		if (ip instanceof FloatProcessor)
//			return fromFloatProcessor((FloatProcessor)ip);
//		if (ip instanceof ColorProcessor)
//			return fromColorProcessor((ColorProcessor)ip);
//		throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
//	}
//	
//	public static float[][] fromByteProcessor(ByteProcessor ip) {
//		byte[] pixels = (byte[]) ip.getPixels();
//		float[] P = new float[pixels.length];
//		for (int i = 0; i < pixels.length; i++) {
//			P[i] = 0xff & pixels[i];
//		}
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromShortProcessor(ShortProcessor ip) {
//		short[] pixels = (short[]) ip.getPixels();
//		float[] P = new float[pixels.length];
//		for (int i = 0; i < pixels.length; i++) {
//			P[i] = 0xffff & pixels[i];
//		}
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromFloatProcessor(FloatProcessor ip) {
//		float[] pixels = (float[]) ip.getPixels();
//		float[] P = pixels.clone();
//		return new float[][] {P};
//	}
//	
//	public static float[][] fromColorProcessor(ColorProcessor ip) {
//		int[] pixels = (int[]) ip.getPixels();
//		float[] R = new float[pixels.length];
//		float[] G = new float[pixels.length];
//		float[] B = new float[pixels.length];
//		int[] RGB = new int[3];
//		for (int i = 0; i < pixels.length; i++) {
//			Rgb.intToRgb(pixels[i], RGB);
//			R[i] = RGB[0];
//			G[i] = RGB[1];
//			B[i] = RGB[2];
//		}
//		return new float[][] {R, G, B};
//	}
	

	// --------------------------------------------------------------------
	
	public static ImageProcessor copyToImageProcessor(float[][] sources, ImageProcessor ip) {
		if (ip instanceof ByteProcessor)
			copyToByteProcessor(sources, (ByteProcessor)ip);
		else if (ip instanceof ShortProcessor)
			copyToShortProcessor(sources, (ShortProcessor)ip);
		else if (ip instanceof FloatProcessor)
			copyToFloatProcessor(sources, (FloatProcessor)ip);
		else if (ip instanceof ColorProcessor)
			copyToColorProcessor(sources, (ColorProcessor)ip);
		else
			throw new IllegalArgumentException("unknown processor type " + ip.getClass().getSimpleName());
		return ip;
	}
	
	public static void copyToByteProcessor(float[][] sources, ByteProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		float[] P = sources[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampByte(Math.round(P[i]));
			pixels[i] = (byte) val;
		}
	}
	
	public static void copyToShortProcessor(float[][] sources, ShortProcessor ip) {
		short[] pixels = (short[]) ip.getPixels();
		float[] P = sources[0];
		for (int i = 0; i < pixels.length; i++) {
			int val = clampShort(Math.round(P[i]));
			pixels[i] = (short) val;
		}
	}
	
	public static void copyToFloatProcessor(float[][] sources, FloatProcessor ip) {
		float[] pixels = (float[]) ip.getPixels();
		float[] P = sources[0];
		System.arraycopy(P, 0, pixels, 0, P.length);
	}
	
	public static void copyToColorProcessor(float[][] sources, ColorProcessor ip) {
		int[] pixels = (int[]) ip.getPixels();
		float[] R = sources[0];
		float[] G = sources[1];
		float[] B = sources[2];
		for (int i = 0; i < pixels.length; i++) {
			int r = clampByte(Math.round(R[i]));
			int g = clampByte(Math.round(G[i]));
			int b = clampByte(Math.round(B[i]));
			pixels[i] = Rgb.rgbToInt(r, g, b);
		}
	}
	
	private static int clampByte(int val) {
		if (val < 0) return 0;
		if (val > 255) return 255;
		return val;
	}
	
	private static int clampShort(int val) {
		if (val < 0) return 0;
		if (val > 65535) return 65535;
		return val;
	}

}
