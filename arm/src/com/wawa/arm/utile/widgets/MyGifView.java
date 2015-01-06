package com.wawa.arm.utile.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.view.View;

import com.wawa.arm.R;

public class MyGifView extends View { 
    private Movie mMovie; 
    private long mMovieStart;
    public MyGifView(Context context) { 
        super(context); 
        mMovie = Movie.decodeStream(getResources().openRawResource( 
                R.drawable.scan_bg));
    } 
    
    public void onDraw(Canvas canvas) {
        long now = android.os.SystemClock.uptimeMillis(); 
        
        if (mMovieStart == 0) { // first time 
            mMovieStart = now; 
        } 
        if (mMovie != null) { 
            
            int dur = mMovie.duration(); 
            if (dur == 0) { 
                dur = 1000; 
            } 
            int relTime = (int) ((now - mMovieStart) % dur);                
            mMovie.setTime(relTime); 
            mMovie.draw(canvas, 0, 0); 
            invalidate(); 
        } 
    }
}
