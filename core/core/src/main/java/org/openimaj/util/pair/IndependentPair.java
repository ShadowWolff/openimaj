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
package org.openimaj.util.pair;

/**
 * IPair represents a generic pair of objects of different (independent) types.
 * 
 * @author Jonathon Hare
 * 
 * @param <A>	the class of the first object in the pair
 * @param <B>	the class of the second object in the pair
 */
public class IndependentPair <A, B> {
	A o1;
	B o2;
	
	/**
	 * Constructs a Pair object with two objects obj1 and obj2
	 * @param obj1	first object in pair
	 * @param obj2	second objec in pair
	 */
	public IndependentPair(A obj1, B obj2)
	{
		o1 = obj1;
		o2 = obj2;
	}
	
	/**
	 * @return first object in pair
	 */
	public A firstObject()
	{
		return o1;
	}
	
	/**
	 * @return second object in pair
	 */
	public B secondObject()
	{
		return o2;
	}
	
	/**
	 * Set first object in pair to obj
	 * @param obj the object
	 */
	public void setFirstObject(A obj)
	{
		o1 = obj;
	}
	
	/**
	 * Set second object in pair to obj
	 * @param obj the object
	 */
	public void setSecondObject(B obj)
	{
		o2 = obj;
	}
}
