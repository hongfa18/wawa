package com.wawa.arm.utile.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.wawa.arm.R;

public class OMEditText extends EditText {
	private final static String TAG = "OMEditText";
	private Drawable imgAble;
	private Context mContext;

	public OMEditText(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public OMEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public OMEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init() {
		imgAble = mContext.getResources().getDrawable(R.drawable.inputfield_icon_clear);
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
			}
		});
		setDrawable();
	}
	
	//设置删除图片
	private void setDrawable() {
		if(length() < 1)
			setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0], null, null, null);
		else
			setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0], null, imgAble, null);
	}
	
	 // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();//相对于屏幕左上角的x坐标值
            int eventY = (int) event.getRawY();
            //Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);//相对于全局的view的区域
            rect.left = rect.right - 50;
            if(rect.contains(eventX, eventY)) 
            	setText("");
        }
        return super.onTouchEvent(event);
    }

}
