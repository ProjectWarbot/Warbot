
def actionRocketLauncher(	)	:

	for percept in getPerceptsEnemiesWarRocketLauncher():

		setDebugString("Mode hunter rocket launcher")

		followTarget(percept)

		return shootTarget();

	for percept in getPerceptsEnemiesWarBase():

		setDebugString("Mode hunter base")

		followTarget(percept)

		return shootTarget()	;

	if (haveNoTarget()):
		setDebugString("No cible				vggcg")

	if (haveTarget()):
		setDebugString("Cible")

	if (isBlocked()):
		RandomHeading()

	return move();
