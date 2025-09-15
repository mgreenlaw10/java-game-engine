package src.program.game;

import java.awt.image.BufferedImage;
import src.math.*;
import src.obj.TileSet;

public abstract class Entity extends Box2d {

	Game gameHandle;

	Vec2d velocity;
	int mass;
	double speed;
	double health;
	boolean staggered;
	AnimationPlayer animationPlayer;
	boolean showHealthBar;
	

	public void stagger() {
		staggered = true;
	}
	public void unstagger() {
		staggered = false;
	}

	public boolean getShowHealthBar() {
		return showHealthBar;
	}

	public enum LifeState {
		ALIVE,
		DYING,
		DEAD
	}
	LifeState lifeState;
	public LifeState getLifeState() {
		return lifeState;
	}

	public Entity(Vec2d position, Vec2d size, Game gameHandle) {
		super(position.x, position.y, size.x, size.y);
		lifeState = LifeState.ALIVE;
		this.gameHandle = gameHandle;
		velocity = new Vec2d();
		staggered = false;
		mass = 100;
		speed = 100;
		health = 1;
		animationPlayer = new AnimationPlayer();
		showHealthBar = false;
	}

	public Vec2d getVelocity() {
		return velocity;
	}
	public void setVelocity(Vec2d velocity) {
		this.velocity = velocity;
	}

	public AnimationPlayer getAnimationPlayer() {
		return animationPlayer;
	}

	public Vec2d getPosition() {
		return xy();
	}

	public Vec2d getSize() {
		return wh();
	}

	public int getMass() {
		return mass;
	}
	public void setMass(int mass) {
		this.mass = mass;
	}

	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getHealth() {
		return health;
	}

	public void setPosition(Vec2d pos) {
		x = pos.x;
		y = pos.y;
	}

	public void setSize(Vec2d size) {
		width = size.x;
		height = size.y;
	}

	public void setGameHandle(Game gameHandle) {
		this.gameHandle = gameHandle;
	}

	public boolean isDead() {
		return lifeState == LifeState.DEAD;
	}

	public void kill() {
		lifeState = LifeState.DYING;
	}
  	
  	// always call super.update(delta) at the beginning of any override
	public void update(double delta) {
		if (lifeState == LifeState.DYING) { 
			whileDying();
			return;
		}
	}
	// these methods should be overridden
	protected void whileDying() {}
	protected void onDamaged(double damage) {}
	public void damage(double amount) {
		onDamaged(amount);
	}
	// should be overridden to return the current state of any entity as a string
	public String getStateString() {
		return "DEFAULT";
	}
}