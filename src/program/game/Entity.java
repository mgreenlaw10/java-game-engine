package src.program.game;

import java.awt.image.BufferedImage;
import src.math.*;
import src.obj.TileSet;

public abstract class Entity extends Box2d {

	Game gameHandle;

	Vec2d velocity;
	int mass;
	double speed;
	AnimationPlayer animationPlayer;

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
		this.velocity = new Vec2d();
		this.mass = 100;
		this.animationPlayer = new AnimationPlayer();
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

	public abstract void update(double delta);
	public abstract void whileDying();

	// should be overridden to return the current state of any entity as a string
	public String getStateString() {
		return "DEFAULT";
	}
}