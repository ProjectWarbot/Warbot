
def actionWarRocketLauncher():

	broadcastAll("broadcastAll","content")
	broadcastMessageToAll("broadcastMessageToAll",["content1","content2"])
	broadcastMessageToAgentType(WarAgentType.WarRocketLauncher,"broadcastMessageToAgentType",["content3","content4"])
	if(isBlocked()):
		RandomHeading()
	return move();
