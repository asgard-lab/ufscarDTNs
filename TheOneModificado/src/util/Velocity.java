package util;

import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;

public class Velocity {
	private double speed;
	private double angle;
	private double x;
	private double y;

	public Velocity(double sp, double ang) {
		this.setSpeed(sp);
		this.setAngle(ang);
		this.setX(this.getSpeed() * Math.cos(this.getAngle()));
		this.setY(this.getSpeed() * Math.sin(this.getAngle()));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "V="+this.speed+" <="+this.getAngle();
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

}
