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
package org.openimaj.math.geometry.point;

/**
 * Interface representing a point in 2d space with x and y coordinates 
 * @author jon
 *
 */
public interface Point2d extends Coordinate, Cloneable {
	/**
	 * @return x coordinate of point 
	 */
	public float getX();
	
	/**
	 * Set x coordinate of point 
	 * @param x x-coordinate
	 */
	public void setX(float x);
	
	/**
	 * @return y coordinate of point
	 */
	public float getY();
	
	/**
	 * Set y coordinate of point
	 * @param y y-coordinate
	 */
	public void setY(float y);
	
	/**
	 * 	Copy the values of the given point
	 * 	into this point.
	 *  @param p The point to copy values from.
	 */
	public void copyFrom( Point2d p );
	
	/**
	 * Clone the point
	 * @return a copy of the point
	 */
	public Point2d clone();

	/**
	 * Translate the position of the point by the given amounts
	 * @param x x-amount
	 * @param y y-amount
	 */
	public void translate(float x, float y);
}

