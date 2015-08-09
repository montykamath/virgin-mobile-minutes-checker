package com.goodstart.minutescheckerlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;

public class GsPieChart extends ShapeDrawable
{
	static final int DRAWABLE_PADDING = 4;
	static final int TIME_STROKE_WIDTH = 4;
	static final int DEGREES = 360;
	Context context;
	int minDeg;
	int dateDeg;
	boolean showTimeChart = true; 

	public Bitmap getPieChartBitmap(Context c, GsAccount account, boolean showTime)
	{
		showTimeChart = showTime;
		context = c;
		minDeg = Math.round((((float) account.getMinutesUsedAsProgress() / (float) 100) * (float) DEGREES));
		dateDeg = Math.round((((float) account.getNewMonthAsProgress() / (float) 100) * (float) DEGREES));
		return this.asBitmap();
	}

	public Bitmap asBitmap()
	{
		Canvas canvas = new Canvas();
		Bitmap bitmap = 
			Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
		canvas.setBitmap(bitmap);
		this.drawOnCanvas(canvas, new Rect(0, 0, 50, 50));
		return bitmap;
	}

	public void draw(final Canvas c)
	{
		Rect clip = squareIt(c.getClipBounds());
		drawOnCanvas(c, clip);
	}

	public void drawOnCanvas(final Canvas c, final Rect clip)
	{
		RectF oval = 
			new RectF(
				clip.left + DRAWABLE_PADDING, 
				clip.top + DRAWABLE_PADDING, 
				clip.right - DRAWABLE_PADDING, 
				clip.bottom - DRAWABLE_PADDING);
		drawBackground(c, oval);
		drawMinutesChart(c, oval);
		drawTimeChart(c, oval);
	}

	private void drawBackground(final Canvas c, final RectF clip)
	{
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		p.setStyle(Paint.Style.FILL);
		p.setAntiAlias(true);
		c.drawOval(clip, p);
	}

	private void drawMinutesChart(final Canvas c, final RectF clip)
	{
		Paint minPaint = new Paint();
		minPaint.setAlpha(getOpacity());
		minPaint.setStyle(Paint.Style.FILL);
		minPaint.setAntiAlias(true);
		minPaint.setColor(context.getResources().getColor(R.color.red));
		c.drawArc(clip, 0, minDeg, true, minPaint);
	}

	private void drawTimeChart(final Canvas c, final RectF clip)
	{
		if(!showTimeChart) { return; }
		RectF degOval = 
			new RectF(
				clip.left + 10, clip.top + 10,
				clip.right - 10, clip.bottom - 10);
		Paint degPaint = new Paint();
		degPaint.setColor(Color.BLACK);
		degPaint.setStyle(Paint.Style.STROKE);
		degPaint.setStrokeWidth(TIME_STROKE_WIDTH);
		degPaint.setAntiAlias(true);
		c.drawArc(degOval, 0, dateDeg, false, degPaint);
	}

	private Rect squareIt(final Rect clipBounds)
	{
		final Rect r = new Rect(clipBounds);
		final int w = r.right - r.left;
		final int h = r.bottom - r.top;
		final int size = Math.min(w, h);

		// Center align
		r.left += (w - size);
		r.right -= (w - size);

		// Align vertically
		r.top += (h - size);
		r.bottom -= (h - size);

		return r;
	}
}
