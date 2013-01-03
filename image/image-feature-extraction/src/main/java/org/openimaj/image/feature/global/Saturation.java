package org.openimaj.image.feature.global;

import org.openimaj.citation.annotation.Reference;
import org.openimaj.citation.annotation.ReferenceType;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.FeatureVectorProvider;
import org.openimaj.image.FImage;
import org.openimaj.image.MBFImage;
import org.openimaj.image.analyser.ImageAnalyser;
import org.openimaj.image.mask.AbstractMaskedObject;

/**
 * Estimate the saturation of an image using the RGB approximation of
 * avg(max(R,G,B) - min(R,G,B)).
 * 
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Reference(
		type = ReferenceType.Inproceedings,
		author = { "Jose San Pedro", "Stefan Siersdorfer" },
		title = "Ranking and Classifying Attractiveness of Photos in Folksonomies",
		year = "2009",
		booktitle = "18th International World Wide Web Conference",
		pages = { "771", "", "771" },
		url = "http://www2009.eprints.org/78/",
		month = "April")
public class Saturation extends AbstractMaskedObject<FImage>
		implements
		ImageAnalyser<MBFImage>,
		FeatureVectorProvider<DoubleFV>
{
	double saturation;

	@Override
	public DoubleFV getFeatureVector() {
		return new DoubleFV(new double[] { saturation });
	}

	@Override
	public void analyseImage(MBFImage image) {
		final FImage R = image.getBand(0);
		final FImage G = image.getBand(1);
		final FImage B = image.getBand(2);

		int N = 0;
		double sat = 0;

		if (mask != null) {
			for (int y = 0; y < R.height; y++) {
				for (int x = 0; x < R.width; x++) {
					if (mask.pixels[y][x] == 1) {
						final float min = Math.min(Math.min(R.pixels[y][x], G.pixels[y][x]), B.pixels[y][x]);
						final float max = Math.max(Math.max(R.pixels[y][x], G.pixels[y][x]), B.pixels[y][x]);
						sat += (max - min);
						N++;
					}
				}
			}
		} else {
			for (int y = 0; y < R.height; y++) {
				for (int x = 0; x < R.width; x++) {
					final float min = Math.min(Math.min(R.pixels[y][x], G.pixels[y][x]), B.pixels[y][x]);
					final float max = Math.max(Math.max(R.pixels[y][x], G.pixels[y][x]), B.pixels[y][x]);
					sat += (max - min);
					N++;
				}
			}
		}

		saturation = sat / N;
	}

	/**
	 * Get the average saturation of the last image processed with
	 * {@link #analyseImage(MBFImage)}.
	 * 
	 * @return the saturation
	 */
	public double getSaturation() {
		return saturation;
	}
}