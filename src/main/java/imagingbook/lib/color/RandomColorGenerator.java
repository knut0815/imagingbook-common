/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/

package imagingbook.lib.color;

import java.awt.Color;
import java.util.Random;

/**
 * Instances of this class create a sequence of random colors.
 * TODO: Improve randomness (currently not too good, much red ...), modify saturation?. 
 * TODO: Add color type/saturation/pastel ...
 * 
 * @author WB
 *
 */
public class RandomColorGenerator {
	
	private float h = 0.0f;
	private float s = 0.9f;
	private float b = 0.9f;
	Random rnd; 

	public RandomColorGenerator() {
		rnd = new Random();
	}
	
	public RandomColorGenerator(int seed) {
		rnd = new Random(seed);
	}
	
	public Color nextColor() {
		//h = (h + 0.8f + 0.3f * (float)rnd.nextGaussian()) % 1.0f;
		//h = (float) (h + rnd.nextGaussian()) % 1.0f;
		h = (h + 0.1f + 0.8f * rnd.nextFloat()) % 1.0f;
		return Color.getHSBColor(h, s, b); 
	}
	
}
