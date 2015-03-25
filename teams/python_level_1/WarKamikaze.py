class WarKamikaze(PyWarKamikaze):

	def __init__(self):
		pass

	def action(self):
		if self.isBlocked():
			self.RandomHeading()

		return self.move();