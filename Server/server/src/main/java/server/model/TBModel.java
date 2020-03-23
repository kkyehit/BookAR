package server.model;

public class TBModel {
	String name;
	String x,y,z;

	public void setName(String name) {
		this.name = name;
	}

	public void setX(String x) {
		this.x = x;
	}

	public void setY(String y) {
		this.y = y;
	}

	public void setZ(String z) {
		this.z = z;
	}

	public String getName() {
		return name;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public String getZ() {
		return z; }

	@Override
	public String toString() {
		return "name : ["+name+"]" + "["+x+", "+y+", "+z+"]";
	}
}
