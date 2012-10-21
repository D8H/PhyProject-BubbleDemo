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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class RestartIconeDrawable extends Drawable
{
	
	@Override
	public void draw(Canvas canvas)
	{
		canvas.drawColor(Color.TRANSPARENT);
		
		Rect clip = canvas.getClipBounds();
		int size = Math.min(clip.width(), clip.height());
		canvas.translate(clip.exactCenterX(), clip.exactCenterY());
		
		float radius = size * 0.25f;
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(size / 8);
		paint.setAntiAlias(true);
		
		canvas.drawArc(new RectF(- radius, - radius, radius, radius), 180, 315, false, paint);
		
		Path triangle = new Path();
		triangle.moveTo(-1   ,  0     );
		triangle.lineTo( 0.5f, -0.866f);
		triangle.lineTo( 0.5f,  0.866f);
		triangle.close();
		
		Matrix matrix = new Matrix();
		float scale = size * 0.1875f;
		matrix.postRotate(-15);
		matrix.postScale(scale, scale);
		matrix.postTranslate(0, radius);
		matrix.postRotate(45);
		triangle.transform(matrix);
		
		paint.setStyle(Paint.Style.FILL);
		
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
