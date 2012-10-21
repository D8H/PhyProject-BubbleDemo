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

import android.bubbledemo.CoordonateConverter;
import android.bubbledemo.phy.GameBall;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.SparseArray;

public class CachedSimpleBallRender implements BallRender
{
	private SparseArray<Bitmap> images;
	private CoordonateConverter coordonateConverter;
	private SimpleBallRender simpleBallRender;
	
	public CachedSimpleBallRender(CoordonateConverter coordonateConverter, int color)
	{
		this.coordonateConverter = coordonateConverter;
		images = new SparseArray<Bitmap>();
		simpleBallRender = new SimpleBallRender(coordonateConverter, color);
	}
	
	@Override
	public void draw(Canvas canvas, GameBall ball)
	{
		PointF center = ball.getLocation();
		float radius = ball.getRadius();
		
		RectF ballRect = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);
		Matrix toScreenTransform = coordonateConverter.getToScreenTransform();
		toScreenTransform.mapRect(ballRect);
		
		int dimension = Math.round(ballRect.width());
		float x = ballRect.left;
		float y = ballRect.top;
		
		Bitmap image = images.get(dimension);
		if (image == null)
		{
			image = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
			image.setDensity(canvas.getDensity());
			Canvas bitmapCaneva = new Canvas(image);
			
			float radiusPixel = dimension / 2;
			simpleBallRender.draw(bitmapCaneva, radiusPixel, radiusPixel, radiusPixel);
			
			images.put(dimension, image);
		}
		
		canvas.drawBitmap(image, x, y, null);
	}
	
	public void clear()
	{
		images.clear();
	}
}
