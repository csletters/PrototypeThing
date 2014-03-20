package com.example.prototypething;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
	
	
	MyRenderer renderer;
	private GestureDetector mGestureDetector;
    public MyGLSurfaceView(Context context){
        super(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setEGLContextClientVersion(2);
        renderer = new MyRenderer(context);
        setRenderer(renderer);
        
        //gesture listener
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }
    
    private class GestureListener extends  GestureDetector.SimpleOnGestureListener
	{
    	private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        
    	  @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
              boolean result = false;
              try {
                  float diffY = e2.getY() - e1.getY();
                  float diffX = e2.getX() - e1.getX();
                  if (Math.abs(diffX) > Math.abs(diffY)) {
                      if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                          if (diffX > 0) {
                              onSwipeRight();
                          } else {
                              onSwipeLeft();
                          }
                      }
                  }
              } catch (Exception exception) {
                  exception.printStackTrace();
              }
              return result;
          }
    	  
    	// event when double tap occurs
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {

        	if(e.getAction() == e.ACTION_UP)
        	{
        		//renderer.squareSelected();
        	}

              return true;
          }
    	
    	
    	
    	@Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    	public void onSwipeRight() {
    		renderer.updateFocus(-1);
        }

        public void onSwipeLeft() {
        	renderer.updateFocus(1);
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
	}
    
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
    	/*float x = e.getX();
    	float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	
        	Log.w("adsd",Integer.toString( renderer.widthView));
        	if (x < renderer.widthView/2)
        	{
        		Log.w("sadsa","sadsadsad");
        		renderer.updateFocus(1);
        	}
        	else
        	{
        		Log.w("sadsa","jhgjgh");
        		renderer.updateFocus(-1);
        	}
            requestRender();
    }
        return true;*/
    	return mGestureDetector.onTouchEvent(e);
    }
}
