class WarRocketLauncher(PyWarRocketLauncher):

	def __init__(self):
		pass

	def action(self):

		__percepts = self.getPercepts();

		for i in range(0,(__percepts.size())): 
			if (__percepts.get(i).getType().equals(WarAgentType.WarRocketLauncher)):
				if (self.isEnemy(__percepts.get(i))):
					self.setDebugString("Mode hunter")
					self.setHeading(__percepts.get(i).getAngle())

					if (self.isReloaded()):
						return self.fire()
					else :
						return self.reloadWeapon()
				else:
					self.setDebugString("No cible")
			elif (__percepts.get(i).getType().equals(WarAgentType.WarBase)):
				if (self.isEnemy(__percepts.get(i))):
					self.setDebugString("Mode hunter")
					self.setHeading(__percepts.get(i).getAngle())

					if (self.isReloaded()):
						return self.fire()
					else :
						return self.reloadWeapon()
				else:
					self.setDebugString("No cible")
			else:
				self.setDebugString("No cible")
		
		if (__percepts.size() == 0):
			self.setDebugString("No cible")

		if self.isBlocked():
			self.RandomHeading()

		return self.move();