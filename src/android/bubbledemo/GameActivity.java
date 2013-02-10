/*******************************************************************************
 * Copyright 2012 Davy Hélard
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

import android.app.Activity;
import android.bubbledemo.GameModel.GameOver;
import android.bubbledemo.render.PlayIconeDrawable;
import android.bubbledemo.render.RestartIconeDrawable;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.phy.core.word.World;
import android.phy.core.word.WorldListener;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameActivity extends Activity implements  WorldListener, GameListener
{
	private GameModel model;
	private GameView gameView;
	private long previousMillis;
	private Thread gameThread;
	private boolean isFirstFocus;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		float sqrtOfSurface = FloatMath.sqrt(widthPixels * heightPixels);
		
		World world = new World(1000 / 60);
		// The surface of bounds is 1 whatever the ratio is.
		// It will make the game similar for any screens.
		RectF bounds = new RectF(0, 0, widthPixels / sqrtOfSurface, heightPixels / sqrtOfSurface);
		model = new GameModel(world, bounds);
		
		gameView = new GameView(this, model);
		setContentView(gameView);
		
		world.addWorldListener(this);
		model.addGameListener(this);
	}
	
	@Override
	protected void onPause()
	{
		model.pause();
		World world = model.getWorld();
		world.setOver(true);
		gameThread = null;
		
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		World world = model.getWorld();
		world.setOver(false);
		gameThread = new Thread(world);
		gameThread.start();
		
		isFirstFocus = true;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocusFlag)
	{
	    super.onWindowFocusChanged(hasFocusFlag);  
	    
	    if (hasFocusFlag && isFirstFocus)
	    {
	    	isFirstFocus = false;
	        openOptionsMenu();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game, menu);
		
		MenuItem restartMenuItem = menu.findItem(R.id.restart);
		restartMenuItem.setIcon(new RestartIconeDrawable());
		
		MenuItem resumeMenuItem = menu.findItem(R.id.resume);
		resumeMenuItem.setIcon(new PlayIconeDrawable());
		
		return true;
	}
	
	@Override
	public void timeShifted()
	{
		long currentMillis = System.currentTimeMillis();
		if (currentMillis - previousMillis >= 1000 / 60)
		{
			//Log.v("frame interval", "" + (currentMillis - previousMillis));
			previousMillis = currentMillis;
			gameView.repaintAndWait();
			//gameView.postInvalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (model != null && ! model.isPaused())
		{
			int action = event.getActionMasked();
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
			{
				int pointerIndex = event.getActionIndex();
				CoordonateConverter coordonateConverter = gameView.getCoordonateConverter();
				float[] point = {event.getX(pointerIndex), event.getY(pointerIndex)};
				Matrix toWorldTransform = coordonateConverter.getToWorldTransform();
				toWorldTransform.mapPoints(point);
				model.hit(point[0], point[1]); //TODO dans timeShift
			}
		}
		return false;
	}
	
	@Override
	public void gameIsFinished(GameEndEvent event)
	{
		model.pause();
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				String message;
				if (model.getGameOver() == GameOver.WON)
				{
					message = getString(R.string.game_won) + " (" + model.getWorld().getTimeMillis() / 1000.0f + " s)";
				}
				else
				{
					message = getString(R.string.game_lost);
				}
				Toast.makeText(GameActivity.this, message, Toast.LENGTH_LONG).show();
				openOptionsMenu();
			}
		});
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		model.pause();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.restart:
			model.restart();
			gameView.invalidate();
			model.resume();
			break;

		case R.id.resume:
			model.resume();
			break;

		default:
			break;
		}
		return true;
	}
}