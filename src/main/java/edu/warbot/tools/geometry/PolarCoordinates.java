package edu.warbot.tools.geometry;

import java.util.Random;

public class PolarCoordinates {

    private double _distance;
    private double _angleInDegrees;

    public PolarCoordinates(double distance, double angleInDegrees) {
        super();
        this._distance = distance;
        setAngle(angleInDegrees);
    }

    public static PolarCoordinates getRandomInBounds(double maxDistance) {
        Random random = new Random();
        return new PolarCoordinates(random.nextDouble() * maxDistance, random.nextDouble() * 359.);
    }

    public static double invertDirectionAngle(double angle) {
        return (360. - angle) % 360.;
    }

    public double getDistance() {
        return _distance;
    }

    public void setDistance(double distance) {
        this._distance = distance;
    }

    public double getAngle() {
        return invertDirectionAngle(_angleInDegrees);
    }

    public void setAngle(double angleInDegrees) {
        this._angleInDegrees = invertDirectionAngle(angleInDegrees);
    }

    public CartesianCoordinates toCartesian() {
        return new CartesianCoordinates(this._distance * Math.cos(Math.toRadians(getAngle())),
                this._distance * Math.sin(Math.toRadians(getAngle())));
    }
}
