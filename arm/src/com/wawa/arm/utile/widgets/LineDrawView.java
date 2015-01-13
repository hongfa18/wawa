package com.wawa.arm.utile.widgets;

import java.util.ArrayList;
import java.util.List;

import com.wawa.arm.utile.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

public class LineDrawView extends View {

	private List<String> mPoints = new ArrayList<String>();;
	private float lengh;
	private float width;
	private float height;
	private float high;
	private float pathStartX;
	private float pathStartY;
	private float one;
	private float two;
	private float three;
	private float four;
	private float five;
	private float six;
	private float seven;
	Path path1;
	Path path2;
	Paint paint1;
	Paint paint2;
	Paint paint3;
	Paint paint4;
	Paint paint5;
	PathEffect effect = new PathEffect();
	Bitmap bitmap;
	Canvas canvas;
    String TEXT;  
    int min;
    int max;
    private Context context;
	public LineDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		//画笔 1 画坐�? 
		paint1 = new Paint();
		paint1.setColor(Color.GRAY);
		paint1.setStrokeWidth(2);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setAntiAlias(true);
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		paint1.setPathEffect(effects);
		
		//画笔 2  文字画笔
 		paint2=new Paint();
		paint2.setColor(Color.BLACK);
		paint2.setStrokeWidth(1);
		paint2.setStyle(Paint.Style.FILL);
		paint2.setAntiAlias(true);
		paint2.setTextAlign(Align.LEFT);
		paint2.setTextSize(sp2px(context, 13));
		
		//画笔 3 画实心图形及线条
		paint3=new Paint();
		paint3.setColor(Color.WHITE);
		paint3.setStrokeWidth(5);
		paint3.setStyle(Paint.Style.FILL);
		paint3.setAntiAlias(true);
		
		//画笔 4 画坐标轴横坐�?
		paint4=new Paint();
		paint4.setColor(Color.WHITE);
		paint4.setStyle(Paint.Style.FILL);
		paint4.setAntiAlias(true);
		paint4.setTextSize(sp2px(context, 12));
		paint4.setTextAlign(Align.CENTER);
		
		//画笔5 画坐标轴纵坐�?
		paint5=new Paint();
		paint5.setColor(Color.WHITE);
		paint5.setStyle(Paint.Style.FILL);
		paint5.setAntiAlias(true);
		paint5.setTextSize(sp2px(context, 12));
		paint5.setTextAlign(Align.RIGHT);
		
		//获取窗口管理   并获取屏幕尺�?
		Display dis = ((Activity) context).getWindowManager().getDefaultDisplay();
		
		bitmap = Bitmap.createBitmap(dis.getWidth(), dis.getHeight(),Config.ARGB_8888);
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
	}
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		lengh=MeasureSpec.getSize(widthMeasureSpec);
		high=MeasureSpec.getSize(heightMeasureSpec);
		width=lengh/8;
		height=high-dp2px(context, 35/2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for (int i = 0; i < 31; i++) {
			canvas.drawLine(width * (i+1), 0, width * (i+1), height, paint1);
			canvas.drawText(StringUtils.GetDatas(i), width*(7-i), high, paint4);
		}
		canvas.drawLine(width, height, width * 7, height, paint1);	//画坐标线
		
		canvas.drawText(min+".00", width-dp2px(context, 10/2), height, paint5);
		canvas.drawText(max+".00", width-dp2px(context, 10/2), dp2px(context, 150/2), paint5);		//画坐标轴上刻度�?
		
		if(mPoints.size() <= 0) {
			return;
		}

		float h = max - min;
		float draw=height-dp2px(context, 135/2);
		one  = (draw / (h * 100) * ((Float.parseFloat(mPoints.get(0))) * 100 - (min * 100)));
		one = dp2px(context, one/2);
		two  = (draw / (h * 100) * ((Float.parseFloat(mPoints.get(1))) * 100 - (min * 100)));
		two = dp2px(context, two/2);
		three= (draw / (h * 100) * ((Float.parseFloat(mPoints.get(2))) * 100 - (min * 100)));
		three = dp2px(context, three/2);
		four = (draw / (h * 100) * ((Float.parseFloat(mPoints.get(3))) * 100 - (min * 100)));
		four = dp2px(context, four/2);
		five = (draw / (h * 100) * ((Float.parseFloat(mPoints.get(4))) * 100 - (min * 100)));
		five = dp2px(context, five/2);
		six  = (draw / (h * 100) * ((Float.parseFloat(mPoints.get(5))) * 100 - (min * 100)));
		six = dp2px(context, six/2);
		seven= (draw / (h * 100) * ((Float.parseFloat(mPoints.get(6))) * 100 - (min * 100)));
		seven = dp2px(context, seven/2);

		float dh=height;
		float[] pts = { width * 1, dh - one,  width * 2,dh - two, 
				        width * 2, dh - two,  width * 3,dh - three, 
				        width * 3, dh - three,width * 4,dh - four, 
				        width * 4, dh - four, width * 5,dh - five,
				        width * 5, dh - five, width * 6,dh - six, 
				        width * 6, dh - six,  width * 7,dh - seven };
		canvas.drawLines(pts, paint3);									//画折�?

		canvas.drawCircle(width * 7, height - seven, 20, paint3);
		pathStartX = (width * 7 - dp2px(context, 80/2));
		pathStartY = height - seven - dp2px(context, 130/2);
		path1 = new Path();
		path1.moveTo(pathStartX, pathStartY);
		path1.lineTo(pathStartX + dp2px(context, 160/2), pathStartY);
		path1.lineTo(pathStartX + dp2px(context, 160/2), pathStartY + dp2px(context, 80/2));
		path1.lineTo(pathStartX, pathStartY + dp2px(context, 80/2));
		path1.close();
		canvas.drawPath(path1, paint3);		//画矩形框
		
		TEXT = mPoints.get(mPoints.size() - 1) + "%";
		canvas.drawTextOnPath(TEXT, path1, dp2px(context, 22/2), dp2px(context, 56/2), paint2); 	//矩形框内写数�?
		
		path2 = new Path();
		path2.moveTo(pathStartX + dp2px(context, 60/2), pathStartY + dp2px(context, 80/2));
		path2.lineTo(pathStartX + dp2px(context, 100/2), pathStartY + dp2px(context, 80/2));
		path2.lineTo(pathStartX + dp2px(context, 80/2), pathStartY + dp2px(context, 100/2));
		path2.close();
		canvas.drawPath(path2, paint3);		//画矩形框下发三角�?
		
	}

	/**
	 * 描述�?
	 *将sp值转换为px值，保证文字大小不变
	 * @author W.Y
	 * @version 1.0
	 * @created 2014�?�?9�?上午9:06:45
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 描述�?将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @author W.Y
	 * @version 1.0
	 * @created 2014�?�?9�?上午9:08:36
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	public void setPoints(List<String> points) {
		float minf = 0f;
		float maxf = 0f;
		mPoints.clear();
		for (int i = 0; i < points.size(); i++) {
			mPoints.add(points.get(i));
			float f = Float.valueOf(points.get(i));
			if (minf == 0f && maxf == 0f) {
				minf = f;
				maxf = f;
			} else if (minf > f) {
				minf = f;
			} else if (maxf < f) {
				maxf = f;
			}
		}
		min = (int) Math.floor(minf);
		max = (int) Math.ceil(maxf);
	}
	
}
