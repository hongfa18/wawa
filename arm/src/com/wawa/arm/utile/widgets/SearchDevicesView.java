package com.wawa.arm.utile.widgets;



import com.wawa.arm.ARMActivity;
import com.wawa.arm.BuildConfig;
import com.wawa.arm.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SearchDevicesView extends BaseView{
	
	public static final String TAG = "SearchDevicesView";
	public static final boolean D  = BuildConfig.DEBUG; 
	
	@SuppressWarnings("unused")
	private long TIME_DIFF = 1500;
	
	int[] lineColor = new int[]{0x7B, 0x7B, 0x7B};
	int[] innerCircle0 = new int[]{0xb9, 0xff, 0xFF};
	int[] innerCircle1 = new int[]{0xdf, 0xff, 0xFF};
	int[] innerCircle2 = new int[]{0xec, 0xff, 0xFF};
	
	int[] argColor = new int[]{0xF3, 0xf3, 0xfa};
	
	private float offsetArgs = 0;
	private boolean isSearching = false;
	private Bitmap bitmap;
	private Bitmap bitmap1,bitmap2;
	private Bitmap currentBitmap;
	private int LEVLE = 0;
	private Handler handler;
	
	public boolean isSearching() {
		return isSearching;
	}

	public void startSerch() {
		this.isSearching = true;
		offsetArgs = 0;
		LEVLE = 0;
		currentBitmap = bitmap1;
		invalidate();
	}
	public void onPause() {
		this.isSearching = false;
		offsetArgs = 0;
		LEVLE = 0;
		currentBitmap = bitmap2;
		invalidate();
	}
	public void stopSerch() {
		this.isSearching = false;
		offsetArgs = 0;
		LEVLE = 0;
		currentBitmap = null;
		invalidate();
	}
	public void changeLevle(int levle){
		this.LEVLE = levle;
		if(!isSearching){
			this.isSearching = true;
			invalidate();
		}
	}

	public SearchDevicesView(Context context) {
		super(context);
		initBitmap();
	}
	
	public SearchDevicesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBitmap();
	}

	public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initBitmap();
	}
	
	private void initBitmap(){//TODO
		if(bitmap == null){//背景
			bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_bg));
		}
		if(bitmap1 == null){//正在扫描
			bitmap1 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sp_bg_03));
		}
		if(bitmap2 == null){//暂停
			bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sp_bg));
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		boolean needgreen = false;
		canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, null);
		
		//canvas.drawBitmap(bitmap1,  getWidth() / 2 - bitmap1.getWidth() / 2, getHeight() / 2 - bitmap1.getHeight() / 2, null);
		if(isSearching){
			/*Rect rMoon = new Rect(getWidth()/2-bitmap2.getWidth(),getHeight()/2,getWidth()/2,getHeight()/2+bitmap2.getHeight()); 
			canvas.rotate(offsetArgs,getWidth()/2,getHeight()/2);
			canvas.drawBitmap(bitmap2,null,rMoon,null);
			offsetArgs = offsetArgs + 3;*/
			Paint p = new Paint();
			p.setColor(0x40fffff1);
			float width = 0;
			if(LEVLE == 0){
				width = 0;
			}else if(LEVLE == 91){//最近
				width = 0;
				needgreen = true;
			}else if(0 < LEVLE && LEVLE < 91){
				float r10 = getWidth()/2 - 13;
				width = (r10 - (r10/90)*LEVLE)+13;
			}
			
			canvas.drawCircle(getWidth()/2,getHeight()/2,width, p); 
			 Shader mShader = new SweepGradient(getWidth()/2, getHeight()/2, new int[] { 0x00ffffff,0x00000000,0x00000000,0x0f000000,0x30ffffff,0xd0ffffff}, null);
			//Shader mShader = new SweepGradient(getWidth()/2, getHeight()/2,0x00000000,0x90ffffff);
	        p.setShader(mShader);
	        int r = (int) Math.sqrt(Math.pow((double)getHeight()/2,2) + Math.pow((double)getWidth()/2,2));
	        //RectF oval2 = new RectF(getWidth()/2-r, 0, getWidth()/2+r,2*r);// 设置个新的长方形，扫描测量
	        canvas.rotate(offsetArgs,getWidth()/2,getHeight()/2);
	        //RectF oval2 = new RectF(getWidth()/2-getHeight(),0,getWidth()/2+getHeight(),2*getHeight());
	        //canvas.drawArc(oval2, 180, 180, true, p);//画扇形
	        canvas.drawCircle(getWidth()/2,getHeight()/2,r, p);
	        if(offsetArgs == 360){
	        	offsetArgs = 0;
	        }else{
	        	offsetArgs = offsetArgs + 4;
	        }
		}
		canvas.restore();
		Paint p = new Paint();
		p.setColor(needgreen?0xff00FF00:0xff8c0628);
        canvas.drawCircle(getWidth()/2,getHeight()/2,10, p);
		
        if(currentBitmap != null){
        	canvas.drawBitmap(currentBitmap,  getWidth() / 2  - currentBitmap.getWidth()/2 , getHeight()/2 - currentBitmap.getHeight()/2, null);
        }
        
		if(isSearching) invalidate();
		//canvas.restore();
		/*Paint p = new Paint();
		 canvas.drawText("画扇形和椭圆:", 10, 120, p);
	        // 设置渐变色 这个正方形的颜色是改变的 
	        Shader mShader = new LinearGradient(0, 0, 100, 100,
	                new int[] { Color.WHITE,
	                        Color.BLACK}, null, Shader.TileMode.CLAMP); // 一个材质,打造出一个线性梯度沿著一条线。
	        Shader mShader = new SweepGradient(getWidth()/2, getHeight()/2, new int[] { Color.WHITE,
                    Color.BLACK}, null);
	        p.setShader(mShader);
	        // p.setColor(Color.BLUE);
	        RectF oval2 = new RectF(60, 200, 200, 340);// 设置个新的长方形，扫描测量
	        canvas.drawArc(oval2, 180, 180, true, p);
	        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线
	        //画椭圆，把oval改一下
	        oval2.set(210,100,250,130);
	        canvas.drawOval(oval2, p);*/
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:	
			if(currentBitmap != null)
				handleActionDownEvenet(event);
			//return true;
		case MotionEvent.ACTION_MOVE: 
			//return true;
		case MotionEvent.ACTION_UP:
			//return true;
		}
		return super.onTouchEvent(event);
	}
	
	private void handleActionDownEvenet(MotionEvent event){
		RectF rectF = new RectF(getWidth() / 2 - currentBitmap.getWidth() / 2, 
								getHeight() / 2 - currentBitmap.getHeight() / 2, 
								getWidth() / 2 + currentBitmap.getWidth() / 2, 
								getHeight() / 2 + currentBitmap.getHeight() / 2);
		
		if(rectF.contains(event.getX(), event.getY())){
			if(D) Log.d(TAG, "click search device button");
			if(!isSearching()) {//暂停就搜索
				handler.sendEmptyMessage(ARMActivity.START_SCAN);
			}else{//搜索就暂停
				handler.sendEmptyMessage(ARMActivity.STOP_SCAN);
			}
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}