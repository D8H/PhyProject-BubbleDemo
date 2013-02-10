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
package android.bubbledemo.render;

import android.bubbledemo.CoordonateConverter;
import android.bubbledemo.phy.GameBall;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class SimpleBallRender implements BallRender
{
	private int color;
	private CoordonateConverter coordonateConverter;
	
	public SimpleBallRender(CoordonateConverter coordonateConverter, int color)
	{
		this.coordonateConverter = coordonateConverter;
		this.color = color;
	}
	
	@Override
	public void draw(Canvas canvas, GameBall ball)
	{
		PointF center = ball.getLocation();
		float radius = ball.getRadius();
		
		RectF ballRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
		Matrix toScreenTransform = coordonateConverter.getToScreenTransform();
		toScreenTransform.mapRect(ballRect);
		
		draw(canvas, ballRect.centerX(), ballRect.centerY(), ballRect.width() / 2);
	}
	
	public void draw(Canvas canvas, float cx, float cy, float radius)
	{
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		
		paint.setStyle(Paint.Style.FILL);
		paint.setAlpha(32);
		canvas.drawCircle(cx, cy, radius - 2, paint);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setAlpha(255);
		canvas.drawCircle(cx, cy, radius - 2, paint);
	}
}
