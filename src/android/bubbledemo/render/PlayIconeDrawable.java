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
package android.bubbledemo.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class PlayIconeDrawable extends Drawable
{
	
	@Override
	public void draw(Canvas canvas)
	{
		canvas.drawColor(Color.TRANSPARENT);
		
		Rect clip = canvas.getClipBounds();
		int size = Math.min(clip.width(), clip.height());
		canvas.translate(clip.exactCenterX(), clip.exactCenterY());
		
		float radius = size * 0.375f;
		float radiuso2 = radius / 2;
		float sqrt3o2 = radius * 0.866f;
		
		
		Path triangle = new Path();
		triangle.moveTo(  radius,       0);
		triangle.lineTo(- radiuso2, - sqrt3o2);
		triangle.lineTo(- radiuso2,   sqrt3o2);
		triangle.close();
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		
		canvas.drawPath(triangle, paint);
	}
	
	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public void setAlpha(int alpha)
	{
	}
	
	@Override
	public void setColorFilter(ColorFilter colorFilter)
	{
	}
}
