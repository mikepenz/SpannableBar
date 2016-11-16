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
	public static final int DEFAULT_START = 0;
	public static final int DEFAULT_SPAN = 7;
	public static final int DEFAULT_COLUMN_COUNT = 7; // week view, 7 days
	public static final int DEFAULT_PADDING = 10;
	public static final float DEFAULT_RADIUS = 8f;
	public static final int DEFAULT_BAR_COLOR = Color.LTGRAY;
	public static final int DEFAULT_TEXT_SIZE_SP = 12;
	public static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	
	private String text;
	private int start = DEFAULT_START,
			span = DEFAULT_SPAN,
			columnCount = DEFAULT_COLUMN_COUNT,
			padding = DEFAULT_PADDING,
			textColor = DEFAULT_TEXT_COLOR,
			color = DEFAULT_BAR_COLOR;
	private float scaledDensity, radius;
	private Paint textPaint;
	private ShapeDrawable drawable;
	
	/**
	 * An array of 8 radius values, for the outer roundrect.
	 * The first two floats are for the top-left corner (remaining pairs correspond clockwise).
	 * For no rounded corners on the outer rectangle, pass null.
	 *
	 * @see <a href="https://developer.android.com/reference/android/graphics/drawable/shapes/RoundRectShape.html">RoundRectShape</a>
	 */
	private float[] radii = {
			radius, radius,
			radius, radius,
			radius, radius,
			radius, radius};
	
	
	//region CONSTRUCTORS
	
	public SpannableBar(Context context)
	{
		super(context);
		init(context, null);
	}
	
	public SpannableBar(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}
	
	public SpannableBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SpannableBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}
	
	//endregion
	
	
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
				color = typedArray.getColor(R.styleable.SpannableBar_barColor, DEFAULT_BAR_COLOR);
				padding = typedArray.getLayoutDimension(R.styleable.SpannableBar_barPadding, DEFAULT_PADDING);
				textColor = typedArray.getColor(R.styleable.SpannableBar_barTextColor, Color.WHITE);
			} finally
			{
				typedArray.recycle();
			}
		}
		drawable = new ShapeDrawable(new RoundRectShape(radii, null, null));
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
		
		textPaint = new Paint();
		textPaint.setColor(textColor);
		textPaint.setTextSize(scaledDensity * DEFAULT_TEXT_SIZE_SP);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setTypeface(typeface);
		requestLayout();
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
		final int colWidth = canvas.getWidth() / columnCount;
		final int barPadding = Math.round(padding * scaledDensity);
		
		if (span > 0)
		{
			drawable.getPaint().setColor(color);
			
			final int coordLeft = barPadding + (start * colWidth);
			final int coordTop = barPadding;
			final int coordRight = coordLeft + (span * colWidth) - barPadding;
			final int coordBottom = canvas.getHeight() - barPadding;
			
			drawable.setBounds(coordLeft, coordTop, coordRight, coordBottom);
			drawable.draw(canvas);
			
			final int textCoordX = coordLeft + (coordRight / 2);
			final int textBaselineToCenter = Math.abs(Math.round(((textPaint.descent() + textPaint.ascent()) / 2)));
			final int textBaselineCoordY = (canvas.getHeight() / 2) + textBaselineToCenter;
			if (text != null && !text.isEmpty())
			{
				canvas.drawText(text, textCoordX, textBaselineCoordY, textPaint);
			}
		}
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
		
		// Measure height
		if (heightMode == MeasureSpec.EXACTLY)
			height = heightSize;
		else if (heightMode == MeasureSpec.AT_MOST)
			height = Math.min(desiredHeight, heightSize);
		else
			height = desiredHeight;
		
		setMeasuredDimension(width, height);
	}
	
	
	//region GETTERS & SETTERS
	
	/**
	 * Set the amount of columnCount
	 *
	 * @param numColumns the amount of columnCount to set
	 */
	public void setColumnCount(int numColumns)
	{
		columnCount = numColumns > 0 ? numColumns : 1;
		requestLayout();
	}
	
	/**
	 * Set the displayed text. The view will automatically be invalidated.
	 *
	 * @param text the text to be displayed
	 */
	public void setText(String text)
	{
		this.text = text == null ? "" : text;
		invalidate();
	}
	
	/**
	 * Set the desired starting column of the bar. Any amount that is higher than the span
	 * will automatically limit itself to the value of columnCount.
	 *
	 * @param start which column to start the bar
	 */
	public void setStart(int start)
	{
		if (start <= columnCount)
			this.start = start;
		else
			this.start = columnCount;
		
		invalidate();
	}
	
	/**
	 * Set the bar's span.
	 *
	 * @param span the span to set the bar to.
	 */
	public void setSpan(int span)
	{
		if (span <= (columnCount - start))
			this.span = span;
		else
		{
			this.span = columnCount - start;
		}
	}
	
	/**
	 * Set the bar's corner radius
	 *
	 * @param radius the radius to set
	 */
	public void setRadius(float radius)
	{
		this.radius = radius;
		this.radii = new float[]{
				radius, radius,
				radius, radius,
				radius, radius,
				radius, radius
		};
	}
	
	/**
	 * Set the amount of padding around the bar
	 *
	 * @param dp the amount of dp to set the padding to.
	 */
	public void setPadding(int dp)
	{
		padding = dp;
	}
	
	//endregion
}
