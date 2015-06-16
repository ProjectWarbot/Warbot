package edu.warbot.tools.geometry;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class CartesianCoordinates extends Point2D.Double {

    public CartesianCoordinates(double x, double y) {
        super(x, y);
    }

    public CartesianCoordinates(Point point) {
        super(point.getX(), point.getY());
    }

    public CartesianCoordinates(Point2D.Double point) {
        super(point.getX(), point.getY());
    }

    public static CartesianCoordinates getRandomInBounds(int x, int y, int width, int height) {
        Random random = new Random();
        return new CartesianCoordinates((random.nextDouble() * (width - x)) + x, (random.nextDouble() * (height - y)) + y);
    }

    public static double getAngleBetween(Point2D pSrc, Point2D pDest) {
        CartesianCoordinates pFromOrigin = new CartesianCoordinates(pDest.getX() - pSrc.getX(), pDest.getY() - pSrc.getY());
        return pFromOrigin.toPolar().getAngle();
    }

    public PolarCoordinates toPolar() {
        return new PolarCoordinates(Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2)),
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

    public double getAngleToPoint(Point2D.Double p) {
        CartesianCoordinates pFromOrigin = new CartesianCoordinates(p.getX() - getX(), p.getY() - getY());
        return pFromOrigin.toPolar().getAngle();
    }

    @Override
    public String toString() {
        return "(" + getX() + "; " + getY() + ")";
    }
}
