from java.util.logging import Logger
logger = Logger.getLogger("WarBase")


st = {}
st['n'] =0

def actionWarBase():
	actionWarBase.n+=getNbElementsInBag()
	setDebugString(`st['n']`+`actionWarBase.n`)
	for m in getMessages():
		if m.getMessage() == "whereAreYouBase":
			reply(m,"Here","")
	nbEnv = getNbElementsInBag()
	if nbEnv > 0:
		return createRocketLauncher()

	st['n'] += 1
	return BaseAction.ACTION_IDLE

actionWarBase.n = 0