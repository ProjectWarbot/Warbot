var JsWarEngineer = JsWarAgent.extend({

	getExginneer : function () {
		var explo = edu.warbot.agents.agents.WarExplorer;
		return explo;
	},

	angleOfView : function () {
		return this.getExginneer().ANGLE_OF_VIEW;
	},

	bagSize : function () {
		return this.getExginneer().BAG_SIZE;
	},

	cost : function () {
		return this.getExginneer().COST;
	},

	distanceOfView : function () {
		return this.getExginneer().DISTANCE_OF_VIEW;
	},

	maxHealth : function () {
		return this.getExginneer().MAX_HEALTH;
	},

	speed : function () {
		return this.getExginneer().SPEED;
	},

	create : function () {
		return this.getExginneer().ACTION_CREATE;
	},

	eat : function () {
		return this.getExginneer().ACTION_EAT;
	},

	give : function () {
		return this.getExginneer().ACTION_GIVE;
	},

	idle : function () {
		return this.getExginneer().ACTION_IDLE;
	},

	move : function () {
		return this.getExginneer().ACTION_MOVE;
	},

	take : function () {
		return this.getExginneer().ACTION_TAKE;
	},

	maxDistanceGive : function () {
		return this.getExginneer().MAX_DISTANCE_GIVE;
	},

	setNextAgentToCreate : function (agent) {
		this.getAgent().setNextAgentToCreate(agent);
	},

	getNextAgentToCreate : function () {
		return this.getAgent().getNextAgentToCreate();
	},

	isAbleToCreate : function (agent) {
		return this.getAgent().isAbleToCreate(agent);
	}
	

	
});