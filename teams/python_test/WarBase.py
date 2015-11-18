from java.util.logging import Logger
logger = Logger.getLogger("WarBase")
def actionWarBase():

	for m in getMessages():
		if m.getMessage() == "whereAreYouBase":
			reply(m,"Here","")
	nbEnv = getNbElementsInBag()
	if nbEnv > 4:
		return createRocketLauncher()

	return BaseAction.ACTION_IDLE
