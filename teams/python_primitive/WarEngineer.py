
def actionWarEngineer():

	if(getHealth() > 1000):
		setDebugString("Creating tower")
		return createWall()

	return move();