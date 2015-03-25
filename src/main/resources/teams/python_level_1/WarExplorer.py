class WarExplorer(PyWarExplorer):

	#__Tool = WarTools()

	def __init__(self):
		pass

	def action(self):

		__percepts = self.getPercepts();

		for i in range(0,(__percepts.size())): 
			# Search Food
			if (__percepts.get(i).getType().equals(WarAgentType.WarFood)):
				if((__percepts.get(i).getDistance() < self.getMaxDistanceTakeFood()) and (not self.isBagFull())):
					self.setHeading(__percepts.get(i).getAngle())
					return self.take();
				elif (not self.isBagFull()) :
					self.setHeading(__percepts.get(i).getAngle())

		if (self.isBagFull()) :
			self.setDebugString("Bag full return base")

			__percepts = self.getPerceptsAlliesByType(WarAgentType.WarBase);

			if((__percepts is None) or (__percepts.size() == 0)): # si pas de base amie
			
				__message = self.getMessages()
				__mes = None

				if(not(__message is None)):
					for i in range (0, __message.size()):
						__mes = __message.get(i)
						if(not (__mes.getSenderType() == WarAgentType.WarBase)):
							__mes = None

				if(not(__mes is None)):
					self.setHeading(__mes.getAngle());
		
				self.broadcastMessageToAgentType(WarAgentType.WarBase, "whereAreYou", "");


			else : # si je vois une base amie
				__base = __percepts.get(0)
				
				if(__base.getDistance() > self.maxDistanceGive()):
					self.setHeading(__base.getAngle());
					return self.move();
				else:
					self.setIdNextAgentToGive(__base.getID());
					return self.give();

		else : 
			self.setDebugString("Chercher food")

		if self.isBlocked():
			self.RandomHeading()

		return self.move();
