package edu.warbot.gui.viewer;

import edu.warbot.launcher.AbstractWarViewer;
import edu.warbot.tools.geometry.CoordCartesian;

import java.awt.event.*;

public class MapExplorationListener implements MouseMotionListener, MouseListener, MouseWheelListener {

    private static final int MAX_CELL_SIZE = 10;
    private static final int MIN_CELL_SIZE = 1;

	private AbstractWarViewer viewer;

    private CoordCartesian oldMouseDragPosition;
    private CoordCartesian oldViewerPosition;
    private boolean onlyRightClick;

	public MapExplorationListener(AbstractWarViewer viewer) {
		this.viewer = viewer;
        this.onlyRightClick = false;


	}

	@Override
	public void mouseDragged(MouseEvent e) {
        if((!onlyRightClick) || (onlyRightClick && e.getButton() == MouseEvent.BUTTON2) ) {
            CoordCartesian movement = new CoordCartesian(
                    e.getX() - oldMouseDragPosition.getX(),
                    e.getY() - oldMouseDragPosition.getY());
            viewer.moveMapOffsetTo(movement.getX() + oldViewerPosition.getX(), movement.getY() + oldViewerPosition.getY());
        }
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
        if((!onlyRightClick) || (onlyRightClick && e.getButton() == MouseEvent.BUTTON2) ) {
            oldMouseDragPosition = new CoordCartesian(e.getX(), e.getY());
            oldViewerPosition = new CoordCartesian(viewer.getMapOffsetX(), viewer.getMapOffsetY());
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int newCellSize = viewer.getCellSize();
        newCellSize -= e.getWheelRotation();
        newCellSize = Math.max(MIN_CELL_SIZE, newCellSize);
        newCellSize = Math.min(MAX_CELL_SIZE, newCellSize);

        CoordCartesian inMapClickPosition = viewer.convertClickPositionToMapPosition(e.getX(), e.getY());

        viewer.setCellSize(newCellSize);

        CoordCartesian inMapClickNewPosition = viewer.convertClickPositionToMapPosition(e.getX(), e.getY());
        viewer.moveMapOffsetTo(viewer.getMapOffsetX() - ((inMapClickPosition.getX() - inMapClickNewPosition.getX()) * newCellSize),
                viewer.getMapOffsetY() - ((inMapClickPosition.getY() - inMapClickNewPosition.getY()) * newCellSize));
    }

    public void setOnlyRightClick(boolean onlyRightClick) {
        this.onlyRightClick = onlyRightClick;
    }
}
