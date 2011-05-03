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
package org.openimaj.image.processing.face.parts;

import java.io.File;
import java.util.List;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processor.resize.ResizeProcessor;
import org.openimaj.math.geometry.shape.Rectangle;

import cern.colt.Arrays;

public class FacePipe {
	public static void main(String [] args) throws Exception {
		FImage image = ImageUtilities.readF(new File("/Users/jon/Software/vision_code/CLASS_facepipe_VJ_29-Sep-08b/047640.jpg"));
		
		HaarCascadeDetector detector = new HaarCascadeDetector("haarcascade_frontalface_alt.xml");
		List<Rectangle> faces = detector.detectObjects(image);
		
		FacialKeypointExtractor kptExt = new FacialKeypointExtractor();
		
		//faces.get(0).setBounds(240.00f,69.00f,199.00f,199.00f);
		
		for (Rectangle r : faces) {
			float [] BB = {r.x, r.x + r.width, r.y, r.y + r.height}; 
			float [] det = {(BB[0]+BB[1])/2, (BB[2]+BB[3])/2, (BB[1]-BB[0])/2, 1};
	        
			float scale = det[2]/(kptExt.model.imgsize/2 - kptExt.model.border);
			float tx = det[0]-scale*kptExt.model.imgsize/2;
			float ty = det[1]-scale*kptExt.model.imgsize/2;

			DisplayUtilities.display(image, "image");
			
			//TODO blur to prevent aliasing
			FImage patch = image.extractROI((int)(tx), (int)(ty), (int)(kptExt.model.imgsize*scale), (int)(kptExt.model.imgsize*scale));
			patch = patch.process(new ResizeProcessor(kptExt.model.imgsize, kptExt.model.imgsize));
			DisplayUtilities.display(patch, "patch");
			
			Pixel[] kpts = kptExt.extractFeatures(patch);
			
			FImage image2 = image.clone();
			for (Pixel pt : kpts) {
				//rescale to image coords
				pt.x = (int) (pt.x*scale + tx);
				pt.y = (int) (pt.y*scale + ty);
				image2.drawPoint(pt, 1f, 3);
			}
			
			float [] desc = FacialDescriptor.extdesc(image, kpts).featureVector;
			
			System.out.println(desc.length);
			System.out.println(Arrays.toString(desc));
			
			DisplayUtilities.display(image2);
		}
	}
}
