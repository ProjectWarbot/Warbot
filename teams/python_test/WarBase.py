from java.util.logging import Logger
logger = Logger.getLogger("WarBase")
def actionWarBase():
	actionWarBase.n+=getNbElementsInBag()
	setDebugString(`actionWarBase.n`)
	for m in getMessages():
		if m.getMessage() == "whereAreYouBase":
			reply(m,"Here","")
	nbEnv = getNbElementsInBag()
	if nbEnv > 0:
		return createRocketLauncher()

	return BaseAction.ACTION_IDLE

actionWarBase.n = 0