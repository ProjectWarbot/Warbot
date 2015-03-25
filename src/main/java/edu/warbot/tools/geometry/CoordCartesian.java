package edu.warbot.tools.geometry;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class CoordCartesian extends Point2D.Double {

	public CoordCartesian(double x, double y) {
		super(x, y);
	}
	
	public CoordCartesian(Point point) {
        super(point.getX(), point.getY());
	}

    public CoordCartesian(Point2D.Double point) {
        super(point.getX(), point.getY());
    }

    public CoordPolar toPolar() {
		return new CoordPolar(Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2)),
				Math.toDegrees(Math.atan2(getY(), getX())));
	}

	public void add(Point2D.Double point) {
		setLocation(getX() + point.getX(), getY() + point.getY());
	}
	
	public void normalize(double minX, double maxX, double minY, double maxY) {
        double newX = getX();
        double newY = getY();
		if (getX() < minX)
			newX = minX;
		else if (getX() > maxX)
            newX = maxX;
		if (getY() < minY)
            newY = minY;
		else if (getY() > maxY)
            newY = maxY;
        setLocation(newX, newY);
	}
	
	public Point toPoint() {
		return new Point((int) getX(), (int) getY());
	}
	
	public static CoordCartesian getRandomInBounds (int x, int y, int width, int height) {
		Random random = new Random();
		return new CoordCartesian((random.nextDouble() * (width - x)) + x, (random.nextDouble() * (height - y)) + y);
	}
	
	public double getAngleToPoint(Point2D.Double p) {
		CoordCartesian pFromOrigin = new CoordCartesian(p.getX() - getX(), p.getY() - getY());
		return pFromOrigin.toPolar().getAngle();
	}
	
	@Override
	public String toString() {
		return "(" + getX() + "; " + getY() + ")";
	}

    public static double getAngleBetween(Point2D pSrc, Point2D pDest) {
        CoordCartesian pFromOrigin = new CoordCartesian(pDest.getX() - pSrc.getX(), pDest.getY() - pSrc.getY());
        return pFromOrigin.toPolar().getAngle();
    }
}
