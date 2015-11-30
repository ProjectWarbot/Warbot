
def actionWarRocketLauncher():
	for m in getMessages():
		print m.getMessage()

	broadcastAll("broadcastAll","content")
	broadcastMessageToAll("broadcastMessageToAll",["content1","content2"])
	broadcastMessageToAgentType(WarAgentType.WarRocketLauncher,"broadcastMessageToAgentType",["content1","content2"])

	#for g in getGroups():
	#	broadcastMessageToGroup(g,"broadcastGroup","content")
	#	for r in getRolesIn(g):
	#		broadcastMessage(g,r,"broadcastToRole","content")

	return move();
