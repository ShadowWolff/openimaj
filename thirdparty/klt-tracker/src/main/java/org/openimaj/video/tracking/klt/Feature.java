package org.openimaj.video.tracking.klt;

import java.io.DataOutputStream;
import java.io.IOException;

import org.openimaj.math.geometry.point.Point2d;



public class Feature implements Point2d, Cloneable {
	public float x;
	public float y;
	public int val;	
	/* for affine mapping */
//	public FImage aff_img; 
//	public FImage aff_img_gradx;
//	public FImage aff_img_grady;
//	public float aff_x;
//	public float aff_y;
//	public float aff_Axx;
//	public float aff_Ayx;
//	public float aff_Axy;
//	public float aff_Ayy;
	
	
	public String toString(String format, String type) {
		assert(type.equals("f") || type.equals("d"));
		String s = "";

		if (type.equals("f"))
			s += String.format(format, x, y, val);
		else if (type.equals("d"))  {
			/* Round x & y to nearest integer, unless negative */
			float _x = x;
			float _y = y;
			if (_x >= 0.0) _x += 0.5;
			if (_y >= 0.0) _y += 0.5;
			s += String.format(format, (int) x, (int) y, val);
		}
		
		return s;
	}
	
	public void writeFeatureBin(DataOutputStream os) throws IOException {
		os.writeFloat(x);
		os.writeFloat(y);
		os.writeInt(val);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public Feature clone() {
		Feature f = new Feature();

		f.x = x;
		f.y = y;
		f.val = val;	
//		f.aff_img = aff_img; 
//		f.aff_img_gradx = aff_img_gradx;
//		f.aff_img_grady = aff_img_grady;
//		f.aff_x = aff_x;
//		f.aff_y = aff_y;
//		f.aff_Axx = aff_Axx;
//		f.aff_Ayx = aff_Ayx;
//		f.aff_Axy = aff_Axy;
//		f.aff_Ayy = aff_Ayy;
		
		return f;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Feature)) return false;
		
		if (((Feature)o).x == x && ((Feature)o).y == y && ((Feature)o).val == val) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = (int) ((31 * hash) + x);
		hash = (int) ((31 * hash) + y);
		hash = ((31 * hash) + val);	
//		hash = (int) ((31 * hash) + aff_img; 
//		hash = (int) ((31 * hash) + aff_img_gradx;
//		hash = (int) ((31 * hash) + aff_img_grady;
//		hash = (int) ((31 * hash) + aff_x;
//		hash = (int) ((31 * hash) + aff_y;
//		hash = (int) ((31 * hash) + aff_Axx;
//		hash = (int) ((31 * hash) + aff_Ayx;
//		hash = (int) ((31 * hash) + aff_Axy;
//		hash = (int) ((31 * hash) + aff_Ayy;
		
		return hash;
	}
	
	@Override
	public String toString() {
		return "Feature(" + x + ", " + y + ", " + val + ")";
	}

	@Override
    public void copyFrom( Point2d p )
    {
		setX( p.getX() );
		setY( p.getY() );
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
