
def actionWarTurret():
	for percept in getPerceptsEnemies():
		setHeading(percept.getAngle())
		return shootTarget()
	return idle();