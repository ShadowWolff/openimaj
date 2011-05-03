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
package org.openimaj.feature.local.matcher;


import java.util.ArrayList;
import java.util.List;

import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.feature.local.LocalFeature;
import org.openimaj.util.pair.Pair;

/**
 * Basic local feature matcher. Matches interest points by finding closest
 * two interest points to target and checking whether the distance
 * between the two matches is sufficiently large.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 */
public class BasicMatcher<T extends LocalFeature> implements LocalFeatureMatcher<T> {
	protected List<T> modelKeypoints;
	protected List <Pair<T>> matches;
	protected int thresh = 8;
	
	/**
	 * Initialise the matcher setting the threshold which the difference between the scores of
	 * the top two best matches must differ in order to count the first as a good match.
	 * 
	 * @param threshold
	 */
	public BasicMatcher(int threshold)
	{
		matches = new ArrayList<Pair<T>>();
		thresh = threshold;
	}
	
	/**
	 * @return List of pairs of matching keypoints
	 */
	@Override
	public List<Pair<T>> getMatches() {
		return matches;
	}
	
	/**
	 * Given a pair of images and their keypoints, pick the first keypoint
	 * from one image and find its closest match in the second set of
	 * keypoints.  Then write the result to a file.
	 */
	@Override
	public boolean findMatches(List<T> keys1)
	{
		matches = new ArrayList<Pair<T>>();
		
	    /* Match the keys in list keys1 to their best matches in keys2.
	     */
	    for (T k : keys1) {
	        T match = checkForMatch(k, modelKeypoints);  
	        
	        if (match != null) {
	        		matches.add(new Pair<T>(k, match));
	        }
	    }
	    System.out.println("Found matches: " + matches.size());
	    
	    return true;
	}


	/**
	 * This searches through the keypoints in klist for the two closest
	 * matches to key.  If the closest is less than 0.6 times distance to
	 * second closest, then return the closest match.  Otherwise, return
	 * NULL.
	 */
	protected T checkForMatch(T query, List<T> features)
	{
	    double distsq1 = Double.MAX_VALUE, distsq2 = Double.MAX_VALUE;
	    T minkey = null;
	    
	    //find two closest matches
	    for (T target : features) {
	    	double dsq = target.getFeatureVector().asDoubleFV().compare(query.getFeatureVector().asDoubleFV(), DoubleFVComparison.EUCLIDEAN);
	        
	        if (dsq < distsq1) {
	            distsq2 = distsq1;
	            distsq1 = dsq;
	            minkey = target;
	        } else if (dsq < distsq2) {
	            distsq2 = dsq;
	        }
	    }
	    
	    // check the distance against the threshold
	    if (10 * 10 * distsq1 < thresh * thresh * distsq2) {
	        return minkey;
	    }
	    else return null;
	}

	@Override
	public void setModelKeypoints(List<T> modelkeys) {
		modelKeypoints = modelkeys;
	}
}
