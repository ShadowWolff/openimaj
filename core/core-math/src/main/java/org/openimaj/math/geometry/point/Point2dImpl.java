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
 * Simple concrete implementation of a two dimensional point 
 * @author Jonathon Hare
 *
 */
public class Point2dImpl implements Point2d {
	/**
	 * The x-coordinate
	 */
	public float x;
	
	/**
	 * The y-coordinate 
	 */
	public float y;
	
	/**
	 * Construct a Point2dImpl with the given (x, y) coordinates
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public Point2dImpl(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 	Construct a Point2dImpl with the (x,y) coordinates
	 * 	given via another point.
	 *  @param p The point to copy from.
	 */
	public Point2dImpl( Point2d p )
	{
		this.copyFrom( p );
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public void copyFrom( Point2d p )
	{
		this.x = p.getX();
		this.y = p.getY();
	}
	
	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	
	@Override
	public Point2dImpl clone() {
		return new Point2dImpl(x, y);
	}

	@Override
	public Float getOrdinate(int dimension) {
		if (dimension == 0) return x;
		return y;
	}

	@Override
	public int getDimensions() {
		return 2;
	}

	@Override
	public void translate(float x, float y) {
		this.x += x;
		this.y += y;
	}
}
