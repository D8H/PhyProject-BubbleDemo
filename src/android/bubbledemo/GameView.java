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

import android.bubbledemo.phy.GameBall;
import android.bubbledemo.render.BallRender;
import android.bubbledemo.render.CachedSimpleBallRender;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

public class GameView extends View
{
	private GameModel gameModel;
	private BallRender toCatchBallRender;
	private BallRender notToCatchBallRender;
	private BallRender gostBallRender;
	private CoordonateConverter coordonateConverter;
	
	public GameView(Context context, GameModel gameModel)
	{
		super(context);
		this.gameModel = gameModel;
		
		coordonateConverter = new CoordonateConverter(gameModel.getBounds());
		
		toCatchBallRender    = new CachedSimpleBallRender(coordonateConverter, Color.RED);
		notToCatchBallRender = new CachedSimpleBallRender(coordonateConverter, Color.GREEN);
		gostBallRender       = new CachedSimpleBallRender(coordonateConverter, Color.GRAY);
	}	
	
	@Override
	public void onDraw(Canvas canvas)
	{
		int width  = getWidth();
		int height = getHeight();
		
		if (coordonateConverter.getScreenDimension() == null)
		{
			coordonateConverter.setScreenDimension(new Rect(0, 0, width, height));
		}
		canvas.clipRect(coordonateConverter.getScreenRect());
		canvas.concat(coordonateConverter.getScreenTransformation());
		
		for (GameBall gostBall : gameModel.getGhostBalls())
		{
			gostBallRender.draw(canvas, gostBall);
		}
		for (GameBall notToCatchBall : gameModel.getNotToCatchBalls())
		{
			if (notToCatchBall.isHidden())
			{
				gostBallRender.draw(canvas, notToCatchBall);
			}
			else
			{
				notToCatchBallRender.draw(canvas, notToCatchBall);
			}
		}
		for (GameBall toCatchBall : gameModel.getToCatchBalls())
		{
			if (toCatchBall.isHidden())
			{
				gostBallRender.draw(canvas, toCatchBall);
			}
			else
			{
				toCatchBallRender.draw(canvas, toCatchBall);
			}
		}
		
		synchronized(this)
		{
			notify();
		}
	}
	
	public CoordonateConverter getCoordonateConverter()
	{
		return coordonateConverter;
	}
	
	public synchronized void repaintAndWait()
	{
		postInvalidate();
		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
