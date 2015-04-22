package edu.warbot.agents.percepts;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.game.WarGame;

import java.awt.*;
import java.awt.geom.Arc2D;

public class InConePerceptsGetter extends PerceptsGetter {

    public InConePerceptsGetter(ControllableWarAgent agent, WarGame game) {
        super(agent, game);
    }

//	@Override
//	public ArrayList<WarPercept> getAgentPercepts() {
//        ArrayList<WarPercept> percepts = new ArrayList<WarPercept>();
//
//        Area visibleArea = new Area(getPerceptionArea());
//        for (WarAgent agentToTestVisible : getGame().getAllAgentsInRadiusOf(getAgent(), getAgent().getDistanceOfView())) {
//			if (agentToTestVisible.getID() != getAgent().getID()) {
////				double a, b, c;
////				double halfAngleOfView = (getAgent().getAngleOfView() / 2.) + agentToTestVisible.getHitbox();
////
////				// test if angle b is between a and c
////				a = getAgent().getHeading() - halfAngleOfView;
////				b = getAgent().getPosition().getAngleToPoint(agentToTestVisible.getPosition());
////				c = getAgent().getHeading() + halfAngleOfView;
////
////				if (b < a) {
////					a -= 360;
////					c -= 360;
////				} else if (b > c) {
////					a += 360;
////					c += 360;
////				}
////
////				if (c >= b && b >= a)
////                    percepts.add(new WarPercept(getAgent(), agentToTestVisible));
//
//                Area agentArea = new Area(agentToTestVisible.getActualForm());
//                agentArea.intersect(visibleArea);
//                if (! agentArea.isEmpty())
//                    percepts.add(new WarPercept(getAgent(), agentToTestVisible));
//			}
//		}
//
//		return percepts;
//	}

    @Override
    public Shape getPerceptionAreaShape() {
        double halfAngleOfView = getAgent().getAngleOfView() / 2.;
        return new Arc2D.Double(getAgent().getX() - getAgent().getDistanceOfView(),
                getAgent().getY() - getAgent().getDistanceOfView(),
                2. * getAgent().getDistanceOfView(),
                2. * getAgent().getDistanceOfView(),
                360. - getAgent().getHeading() - halfAngleOfView,
                getAgent().getAngleOfView(),
                Arc2D.PIE);
    }
}
