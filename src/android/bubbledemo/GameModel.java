/*******************************************************************************
 * Copyright 2012, 2013 Davy Hélard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package android.bubbledemo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.bubbledemo.phy.GameBall;
import android.graphics.RectF;
import android.phy.core.solid.MoveableSolid;
import android.phy.core.solid.Solid;
import android.phy.core.word.World;
import android.phy.library.BoundsSolid;
import android.phy.library.ball.Ball;
import android.phy.library.ball.ball.BallBallCollision;
import android.phy.library.ball.ball.BallBallCollisionEvent;
import android.phy.library.ball.ball.BallBallCollisionListener;
import android.phy.library.ball.ball.BounceBallBallCollisionEffect;
import android.phy.library.ball.bounds.BallBoundsCollision;
import android.phy.library.ball.bounds.BounceBallBoundsCollisionEffect;
import android.phy.library.force.FrictionForce;
import android.phy.util.InterIterablePairIterable;
import android.phy.util.IntraIterablePairIterable;
import android.phy.util.NTreeNode;


public class GameModel
{
	public enum GameOver
	{
		NOT_YET,
		WON,
		LOST
	}

	private static final float BALL_RADIUS_MIN = 0.03125f;
	private static final float BALL_RADIUS_MAX = 0.0625f;
	private static final int BALL_RADIUS_DIFFERENT_SIZE = 3;
	private static final int NUMBER_OF_BALLS_TO_CATCH = 8;
	private static final int NUMBER_OF_BALLS_NOT_TO_CATCH = 8;
	private static final int NUMBER_OF_GHOST_BALLS = 8;
	
	private final ArrayList<GameListener> gameListeners = new ArrayList<GameListener>();
	
	private World world;
	private GameOver gameOver;
	private ArrayList<GameBall> toCatchBalls;
	private ArrayList<GameBall> notToCatchBalls;
	private ArrayList<GameBall> ghostBalls;
	private RectF bounds;
	
	public GameModel(World world, RectF bounds)
	{
		this.world = world;
		this.bounds = bounds;
		
		ArrayList<BoundsSolid> boundsGroup;
		NTreeNode<GameBall> balls;
		NTreeNode<GameBall> notGhostBalls;
		
		NTreeNode<Solid> motionlessSolids = world.getMotionlessSolids();
		{
			boundsGroup = new ArrayList<BoundsSolid>();
			{
				BoundsSolid boundsSolid = new BoundsSolid(world, bounds);
				boundsGroup.add(boundsSolid);
			}
			motionlessSolids.getChildren().add(boundsGroup);
		}
		NTreeNode<MoveableSolid> mobileSolids = world.getMobileSolids();
		{
			balls = new NTreeNode<GameBall>();
			{
				ghostBalls    = new ArrayList<GameBall>();
				notGhostBalls = new NTreeNode<GameBall>();
				{
					toCatchBalls    = new ArrayList<GameBall>();
					notToCatchBalls = new ArrayList<GameBall>();
					
					notGhostBalls.getChildren().add(toCatchBalls);
					notGhostBalls.getChildren().add(notToCatchBalls);
				}
				balls.getChildren().add(ghostBalls);
				balls.getChildren().add(notGhostBalls);
			}
			mobileSolids.getChildren().add(balls);
		}
		
		world.addForceDefinition(new FrictionForce(0.0625f), balls);
		
		InterIterablePairIterable<Ball, BoundsSolid> ballsBoundsCollisionIterable = new InterIterablePairIterable<Ball, BoundsSolid>(balls, boundsGroup);
		BallBoundsCollision ballBoundsCollision = new BallBoundsCollision(ballsBoundsCollisionIterable, new BounceBallBoundsCollisionEffect(true));
		world.getCollisions().add(ballBoundsCollision);
		
		IntraIterablePairIterable<Ball> ballsBallsCollisionIterable = new IntraIterablePairIterable<Ball>(notGhostBalls);
		BallBallCollision ballBallCollision = new BallBallCollision(ballsBallsCollisionIterable, new BounceBallBallCollisionEffect());
		world.getCollisions().add(ballBallCollision);
		
		NotGhostBallsCollisionListener notGhostBallsCollisionListener = new NotGhostBallsCollisionListener();
		ballBallCollision.addCollisionListener(notGhostBallsCollisionListener);
		
		restart();
	}
	
	public void restart()
	{
		toCatchBalls.clear();
		notToCatchBalls.clear();
		ghostBalls.clear();
		
		for (int i = 0; i < NUMBER_OF_BALLS_TO_CATCH; i++)
		{
			toCatchBalls.add(generateBall(i));
		}
		
		for (int i = 0; i < NUMBER_OF_BALLS_NOT_TO_CATCH; i++)
		{
			notToCatchBalls.add(generateBall(i));
		}
		
		for (int i = 0; i < NUMBER_OF_GHOST_BALLS; i++)
		{
			ghostBalls.add(generateBall(i));
		}
		
		world.setTimeMillis(0);
		gameOver = GameOver.NOT_YET;
		
	}
	
	private GameBall generateBall(int i)
	{
		GameBall ball;
		float randomBoundsLocationLengt = 2 * (bounds.width() + bounds.height());
		float randomBoundsLocation = randomBoundsLocationLengt * (float) Math.random();
		float randomRadius = BALL_RADIUS_MIN + (BALL_RADIUS_MAX - BALL_RADIUS_MIN) * (i % BALL_RADIUS_DIFFERENT_SIZE) / BALL_RADIUS_DIFFERENT_SIZE;
		float x, y;
		float toTesteRandomBoundsLocationLengt = randomBoundsLocation;
		if (toTesteRandomBoundsLocationLengt < bounds.width())
		{
			x = bounds.left + toTesteRandomBoundsLocationLengt;
			y = bounds.top - randomRadius;
		}
		else
		{
			toTesteRandomBoundsLocationLengt -= bounds.width();
			if (toTesteRandomBoundsLocationLengt < bounds.height())
			{
				x = bounds.right + randomRadius;
				y = bounds.top + toTesteRandomBoundsLocationLengt;
			}
			else
			{
				toTesteRandomBoundsLocationLengt -= bounds.height();
				if (toTesteRandomBoundsLocationLengt < bounds.width())
				{
					x = bounds.right - toTesteRandomBoundsLocationLengt;
					y = bounds.bottom + randomRadius;
				}
				else
				{
					toTesteRandomBoundsLocationLengt -= bounds.width();
					//if (toTesteRandomBoundsLocationLengt < bounds.height())
					{
						x = bounds.left + randomRadius;
						y = bounds.bottom + toTesteRandomBoundsLocationLengt;
					}
				}
			}
		}
		ball = new GameBall(world, randomRadius * randomRadius, x, y, randomRadius);
		
		float randomInsideX = bounds.left + bounds.width()  * (float) Math.random();
		float randomInsideY = bounds.top  + bounds.height() * (float) Math.random();
		ball.setAngleTo(randomInsideX, randomInsideY);
		
		ball.setSpeed(0.5f);
		
		return ball;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public void addGameListener(GameListener listener)
	{
		gameListeners.add(listener);
	}
	
	public void removeGameListener(GameListener listener)
	{
		gameListeners.remove(listener);
	}
	
	public List<GameListener> getGameListeners()
	{
		return gameListeners;
	}
	
	protected void fireGameIsFinished()
	{
		GameEndEvent event = new GameEndEvent(this);
		for (GameListener listener : getGameListeners())
		{
			listener.gameIsFinished(event);
		}
	}
	
	public void pause()
	{
		world.pause();
	}

	public void resume()
	{
		world.resume();
	}
	
	public boolean isPaused()
	{
		return world.isPaused();
	}

	public GameOver getGameOver()
	{
		return gameOver;
	}

	public void hit(float x, float y)
	{
		{
			Iterator<GameBall> toCatchBallsItr = toCatchBalls.iterator();
			while (toCatchBallsItr.hasNext())
			{
				GameBall toCatchBall = toCatchBallsItr.next();
				
				if (toCatchBall.contains(x, y))
				{
					toCatchBallsItr.remove();
					
					if (toCatchBalls.isEmpty() && gameOver == GameOver.NOT_YET)
					{
						gameOver = GameOver.WON;
						fireGameIsFinished();
					}
				}
			}
		}
		{
			Iterator<GameBall> notToCatchBallsItr = notToCatchBalls.iterator();
			while (notToCatchBallsItr.hasNext())
			{
				GameBall notToCatchBall = notToCatchBallsItr.next();
				
				if (notToCatchBall.contains(x, y))
				{
					notToCatchBallsItr.remove();
					
					if (gameOver == GameOver.NOT_YET)
					{
						gameOver = GameOver.LOST;
						fireGameIsFinished();
					}
				}
			}
		}
	}

	public Collection<GameBall> getToCatchBalls()
	{
		return toCatchBalls;
	}

	public Collection<GameBall> getNotToCatchBalls()
	{
		return notToCatchBalls;
	}

	public Collection<GameBall> getGhostBalls()
	{
		return ghostBalls;
	}

	public RectF getBounds()
	{
		return bounds;
	}
	
	private class NotGhostBallsCollisionListener implements BallBallCollisionListener
	{

		@Override
		public void onBallBallCollision(BallBallCollisionEvent event)
		{
			((GameBall) event.getMoveableSolid()).setImpacted();
			((GameBall) event.getSolid()).setImpacted();
		}
	}
}
