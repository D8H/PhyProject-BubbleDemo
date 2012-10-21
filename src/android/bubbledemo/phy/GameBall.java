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
package android.bubbledemo.phy;

import android.phy.core.word.World;
import android.phy.library.ball.Ball;

public class GameBall extends Ball implements Cloneable
{
	private static final int unhiddenDuration = 1000;
	private int lastImpactMillis;
	
	
	public GameBall(World world, float mass, float x, float y, float radius)
	{
		super(world, mass, x, y, radius, (float) (Math.PI / 24));
		lastImpactMillis = - unhiddenDuration;
	}
	
	public boolean isHidden()
	{
		return getWorld().getTimeMillis() - lastImpactMillis > unhiddenDuration;
	}
	
	public void setImpacted()
	{
		lastImpactMillis = getWorld().getTimeMillis();
	}
	
	@Override
	public GameBall clone()
	{
		GameBall clone;
		clone = (GameBall) super.clone();
		return clone;
	}
}
