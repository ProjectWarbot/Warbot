package edu.warbot.maps;

import edu.warbot.tools.geometry.CoordCartesian;

public class DefaultWarMap extends AbstractWarMap {

    private static final double WIDTH = 1000;
    private static final double HEIGHT = 600;

    public DefaultWarMap() {
        super("Arène", WIDTH, HEIGHT);

        forbidAllBorders();

        addTeamPositions(
                new CoordCartesian(TEAM_POSITION_RADIUS, TEAM_POSITION_RADIUS),
                new CoordCartesian(TEAM_POSITION_RADIUS, getHeight() / 2),
                new CoordCartesian(TEAM_POSITION_RADIUS, getHeight() - TEAM_POSITION_RADIUS)
        );
        addTeamPositions(
                new CoordCartesian(getWidth() - TEAM_POSITION_RADIUS, TEAM_POSITION_RADIUS),
                new CoordCartesian(getWidth() - TEAM_POSITION_RADIUS, getHeight() / 2),
                new CoordCartesian(getWidth() - TEAM_POSITION_RADIUS, getHeight() - TEAM_POSITION_RADIUS)
        );

        addFoodPosition(getCenterX(), getCenterY() - (getCenterY() / 2.));
        addFoodPosition(getCenterX(), getCenterY());
        addFoodPosition(getCenterX(), getCenterY());
        addFoodPosition(getCenterX(), getCenterY() + (getCenterY() / 2.));
    }

}
