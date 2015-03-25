package edu.warbot.agents;

import edu.warbot.agents.actions.IdlerActionsMethods;
import edu.warbot.brains.capacities.Alive;
import edu.warbot.game.Team;

import java.util.logging.Level;

public abstract class AliveWarAgent extends WarAgent implements Alive, IdlerActionsMethods {

    private int _cost;
    private int _health;
    private int _maxHealth;

    public AliveWarAgent(String firstActionToDo, Team team, Hitbox hitbox, int cost, int maxHealth) {
        super(firstActionToDo, team, hitbox);

        _cost = cost;
        _maxHealth = maxHealth;
        _health = _maxHealth;
    }

    public int getCost() {
        return _cost;
    }

    @Override
    public int getHealth() {
        return _health;
    }

    @Override
    public int getMaxHealth() {
        return _maxHealth;
    }

    public void heal(int quantity) {
        logger.log(Level.FINEST, this.toString() + "healed of " + quantity + "life points.");
        _health += quantity;
        if (_health > getMaxHealth()) {
            _health = getMaxHealth();
        }
    }

    public void damage(int quantity) {
        logger.finest(this.toString() + "damaged of " + quantity + "life points.");
        _health -= quantity;
        if (_health <= 0) {
            logger.finer(this.toString() + "killed.");
//            System.out.println(killAgent(this));
            kill();
        }
    }

    @Override
    public void kill() {
        getTeam().setWarAgentAsDying(this);
    }

    public void init(int health) {
        _health = health;
    }
}
