package edu.warbot.maps;

import edu.warbot.tools.geometry.CoordCartesian;

public class OpenWorldWarMap extends AbstractWarMap {

    private static final double WIDTH = 1000;
    private static final double HEIGHT = 600;

    public OpenWorldWarMap() {
        super("Monde ouvert", WIDTH, HEIGHT);

        double widthQuarter = WIDTH / 4.;
        double heightThird = HEIGHT / 3.;
        addTeamPositions(
                new CoordCartesian(widthQuarter, heightThird),
                new CoordCartesian(widthQuarter, HEIGHT - heightThird)
        );
        addTeamPositions(
                new CoordCartesian(WIDTH - widthQuarter, heightThird),
                new CoordCartesian(WIDTH - widthQuarter, HEIGHT - heightThird)
        );

        addFoodPosition(getCenterX(), getCenterY() - (getCenterY() / 2.));
        addFoodPosition(getCenterX(), getCenterY());
        addFoodPosition(getCenterX(), getCenterY());
        addFoodPosition(getCenterX(), getCenterY() + (getCenterY() / 2.));
    }

}
