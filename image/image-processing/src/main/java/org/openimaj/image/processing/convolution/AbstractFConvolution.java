/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.image.processing.convolution;

import org.openimaj.image.FImage;
import org.openimaj.image.processor.KernelProcessor;
import org.openimaj.image.processor.PixelProcessor;

class SumProcessor implements PixelProcessor<Float> {
	float sum = 0.0F;
	@Override
	public Float processPixel(Float pixel, Number[]... otherpixels) {
		sum += pixel;
		return pixel;
	}
}

/**
 * Abstract base class for implementation of classes that perform 
 * convolution operations on @link{FImage}s as a @link{KernelProcessor}, 
 * with the kernel itself formed from and @link{FImage}.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 */
public abstract class AbstractFConvolution implements KernelProcessor<Float, FImage> {
	SumProcessor sumprocessor = new SumProcessor();
	FImage kernel;
	
	/**
	 * Construct the convolution operator with the given kernel
	 * @param kernel the kernel
	 */
	public AbstractFConvolution(FImage kernel) {
		this.kernel = kernel;
	}
	
	@Override
	public int getKernelHeight() {
		return kernel.height;
	}

	@Override
	public int getKernelWidth() {
		return kernel.width;
	}
	
	@Override
	public Float processKernel(FImage patch) {
		patch.multiplyInline(kernel);

		//performance enhancement!
		float sum = 0;
		for (int r=0; r<kernel.height; r++)
			for (int c=0; c<kernel.width; c++)
				sum += patch.pixels[r][c];
		return sum;
	}
}
