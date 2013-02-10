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


import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class CoordonateConverter
{
	private RectF worldDimension;
	private Rect screenDimension;
	private Rect drawnDimension;
	
	private Matrix toScreenTransform;
	private Matrix toWorldTransform;
	private float[] toScreenTransformValues;
	private float[] toWorldTransformValues;
	
	private Rect screenRect;
	private Matrix screenTransformation;
	
	
	public CoordonateConverter(RectF worldDimension)
	{
		this.worldDimension = worldDimension;
		this.screenDimension = null;
		
		toScreenTransform = new Matrix();
		toWorldTransform = new Matrix();
	}
	
	public RectF getWorldDimension()
	{
		return worldDimension;
	}
	
	public Rect getScreenDimension()
	{
		return screenDimension;
	}
	
	public void setWorldDimension(RectF worldDimension)
	{
		this.worldDimension = worldDimension;
		if (screenDimension != null)
		{
			reclac();
		}
	}

	public void setScreenDimension(Rect screenDimension)
	{
		this.screenDimension = screenDimension;
		if (worldDimension != null && screenDimension != null)
		{
			reclac();
		}
	}
	
	private void reclac()
	{
		float worldWidth  = worldDimension.width();
		float worldHeight = worldDimension.height();
		
		int width  = screenDimension.width();
		int height = screenDimension.height();
		
		screenTransformation = new Matrix();
		if (width > height ^ worldWidth > worldDimension.height())
		{
			float[] values =
			{
				0, -1, width,
				1,  0,     0,
				0,  0,     1
			};
			screenTransformation.setValues(values);
			
			int swap = width;
			width = height;
			height = swap;
		}
		if (height * worldWidth > width * worldHeight)
		{
			int bandHeight = (int) (height - width  * worldHeight / worldWidth);
			
			screenRect = new Rect
			(
				0,
				bandHeight / 2,
				width,
				height - (bandHeight / 2 + bandHeight % 2)
			);
		}
		else
		{
			int bandWidth  = (int) (width  - height * worldWidth  / worldHeight);
			
			screenRect = new Rect
			(
				bandWidth  / 2,
				0,
				width  - (bandWidth / 2  + bandWidth  % 2),
				height
			);
		}
		screenTransformation.postTranslate(screenRect.left, screenRect.top);
		drawnDimension = new Rect(0, 0, screenRect.width(), screenRect.height());
		
		toScreenTransform.reset();
		toScreenTransform.postScale
		(
			(float) drawnDimension.width()  / worldDimension.width(),
			(float) drawnDimension.height() / worldDimension.height()
		);
		
		screenTransformation.invert(toWorldTransform);
		Matrix toScreenInvert = new Matrix();
		toScreenTransform.invert(toScreenInvert);
		toWorldTransform.postConcat(toScreenInvert);
		
		toScreenTransformValues = new float[9];
		toScreenTransform.getValues(toScreenTransformValues);
		toWorldTransformValues = new float[9];
		toWorldTransform.getValues(toWorldTransformValues);
	}
	
	public Matrix getToScreenTransform()
	{
		return toScreenTransform;
	}

	public Matrix getToWorldTransform()
	{
		return toWorldTransform;
	}
	
	public Matrix getScreenTransformation()
	{
		return screenTransformation;
	}
	
	public Rect getScreenRect()
	{
		return screenRect;
	}
}
