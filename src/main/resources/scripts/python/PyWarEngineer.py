from java.lang import String
from edu.warbot.agents.agents import WarEngineer as EngineerAction

class PyWarEngineer(PyWarAgent):

	def __init__(self):
		pass

	def angleOfView(self):
		return EngineerAction.ANGLE_OF_VIEW;

	def bagSize(self):
		return EngineerAction.BAG_SIZE;

	def cost(self):
		return EngineerAction.COST;

	def distanceOfView(self):
		return EngineerAction.DISTANCE_OF_VIEW;

	def maxHealth(self):
		return EngineerAction.MAX_HEALTH;

	def speed(self):
		return EngineerAction.SPEED;

	def create(self):
		return EngineerAction.ACTION_CREATE;

	def eat(self):
		return EngineerAction.ACTION_EAT;

	def give(self):
		return EngineerAction.ACTION_GIVE;

	def idle(self):	
		return EngineerAction.ACTION_IDLE;

	def move(self):
		return EngineerAction.ACTION_MOVE;

	def take(self):
		return EngineerAction.ACTION_TAKE;

	def maxDistanceGive(self):
		return EngineerAction.MAX_DISTANCE_GIVE;

	# WarEngineerAdapter

	def setNextAgentToCreate(self, nextAgentToCreate):
		self.getRetAgent().setNextAgentToCreate(nextAgentToCreate)
	
	def getNextAgentToCreate(self):
		return self.getRetAgent().getNextAgentToCreate()

	def isAbleToCreate(self, agent):
		return self.getRetAgent().isAbleToCreate(agent)
