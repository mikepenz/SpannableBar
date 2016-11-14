package com.greasemonk.spannablebar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Wiebe Geertsma on 10-11-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class SpannableBar extends View
{
	private static final int DEFAULT_COLUMN_COUNT = 7;
	private static final int DEFAULT_PADDING = 10;
	private static final float DEFAULT_RADIUS = 8f;
	private static final int TEXT_SIZE_SP = 12;
	
	private Rect textRect;
	private float scaledDensity;
	private Paint textPaint;
	private int color = 0xff74AC23;
	private String text;
	private int start = 0, span = 7;
	private ShapeDrawable drawable;
	private int columns = DEFAULT_COLUMN_COUNT;
	private float definedRadius = DEFAULT_RADIUS;
	private float[] definedRadii = {definedRadius, definedRadius, definedRadius, definedRadius,
			definedRadius, definedRadius, definedRadius, definedRadius};
	
	public SpannableBar(Context context, int columns, int start, int span)
	{
		super(context);
		init(context, null);
		setColumnCount(columns);
		set(start, span);
	}
	
	public SpannableBar(Context context, @Nullable AttributeSet attrs, int columns, int start, int span)
	{
		super(context, attrs);
		init(context, attrs);
		setColumnCount(columns);
		set(start, span);
	}
	
	public SpannableBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int columns, int start, int span)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs);
		setColumnCount(columns);
		set(start, span);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SpannableBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int columns, int start, int span)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
		setColumnCount(columns);
		set(start, span);
	}
	
	private void init(Context context, AttributeSet attrs)
	{
		if (attrs != null)
		{
			TypedArray typedArray = context.getTheme().obtainStyledAttributes(
					attrs,
					R.styleable.SpannableBar,
					0, 0);
			try
			{
				text = typedArray.getString(R.styleable.SpannableBar_barText);
				color = typedArray.getColor(R.styleable.SpannableBar_barColor, Color.LTGRAY);
			} finally
			{
				typedArray.recycle();
			}
		}
		drawable = new ShapeDrawable(new RoundRectShape(definedRadii, null, null));
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		Typeface typeface = Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD_ITALIC);
		
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(scaledDensity * TEXT_SIZE_SP);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTypeface(typeface);
		requestLayout();
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
		final int colWidth = canvas.getWidth() / columns;
		final int padding = Math.round(DEFAULT_PADDING * scaledDensity);
		
		if (span > 0)
		{
			drawable.getPaint().setColor(color);
			
			final int coordLeft = padding + (start * colWidth);
			final int coordTop = padding;
			final int coordRight = coordLeft + (span * colWidth) - padding;
			final int coordBottom = canvas.getHeight() - padding;
			
			drawable.setBounds(coordLeft, coordTop, coordRight, coordBottom);
			drawable.draw(canvas);
			
			final int textCoordX = coordLeft + (coordRight / 2);
			final int textBaselineCoordY = (canvas.getHeight() / 2) + (padding / 2);
			if (text != null && !text.isEmpty())
			{
				canvas.drawText(text, textCoordX, textBaselineCoordY, textPaint);
			}
		}
	}
	
	
	public void setColumnCount(int numColumns)
	{
		columns = Math.min(numColumns, 0);
		requestLayout();
	}
	
	/**
	 * Set the displayed text. The view will automatically be invalidated.
	 * @param text the text to be displayed
	 */
	public void setText(String text)
	{
		this.text = text == null ? "" : text;
		invalidate();
	}
	
	public void set(int start, int span)
	{
		this.start = start;
		this.span = span;
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int desiredWidth = 100;
		int desiredHeight = 100;
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int width;
		int height;
		
		// Measure width
		if (widthMode == MeasureSpec.EXACTLY)
			width = widthSize;
		else if (widthMode == MeasureSpec.AT_MOST)
			width = Math.min(desiredWidth, widthSize);
		else
			width = desiredWidth;
		
		// Meausre height
		if (heightMode == MeasureSpec.EXACTLY)
			height = heightSize;
		else if (heightMode == MeasureSpec.AT_MOST)
			height = Math.min(desiredHeight, heightSize);
		else
			height = desiredHeight;

		setMeasuredDimension(width, height);
	}
}
