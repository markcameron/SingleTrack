package com.android.mcameron.singletrack;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Help extends Activity implements OnTouchListener {
    /** Called when the activity is first created. */
	
	TestSurfaceView testSurfaceView;
	
    private int mX, mY, mLastTouchX, mLastTouchY;
	private Matrix matrix;
    private float drawW, drawH, parentW, parentH;
    private float offsetX = 0;
    private float offsetY = 0;
    
    private static final int INVALID_POINTER_ID = -1;

    // The Ôactive pointerÕ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testSurfaceView = new TestSurfaceView(this);
        testSurfaceView.setOnTouchListener(this);
        setContentView(testSurfaceView);      
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	testSurfaceView.pause();
    }
   
    @Override
    public void onResume() {
    	super.onResume();
    	testSurfaceView.resume();
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	testSurfaceView.mScaleDetector.onTouchEvent(event);
    	//Log.d("Counting", "x: "+ x + " | y: "+ y +" | mX: "+ mX + " | mY: "+ mY +" | mLastTouchX: "+ mLastTouchX +" | mLastTouchY: "+ mLastTouchY);
    	final int action = event.getAction();
    	switch (action & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN: {
	    		final float x = event.getX();
	    		final float y = event.getY();
	
	    		final float tX = (x / testSurfaceView.mScaleFactor) - (mX / testSurfaceView.mScaleFactor) - (offsetX / testSurfaceView.mScaleFactor);
	    		final float tY = (y / testSurfaceView.mScaleFactor) - (mY / testSurfaceView.mScaleFactor) - (offsetY / testSurfaceView.mScaleFactor) + 19;
	    		Log.d("Counting", "Down: "+ offsetX + " | "+ offsetY +" | "+ testSurfaceView.mScaleFactor);
	
	    		testSurfaceView.touchDraw(tX, tY);
	
	    		mLastTouchX = (int) x;
	    		mLastTouchY = (int) y;
	
	    		// Save the ID of this pointer
	    		mActivePointerId = event.getPointerId(0);
	
	    		break;
	    	}
	    	case MotionEvent.ACTION_POINTER_DOWN: {
	    		final float x = event.getX();
	    		final float y = event.getY();
	    		Log.d("Counting", "Pointer_Down: "+ x + " | "+ y);
	    		break;
	    	}
	    	case MotionEvent.ACTION_MOVE: {
	    		if (event.getPointerCount() > 1) {
	    			break;
	    		}
	    		
	    		Log.d("Counting", "Move:"+ mX + " | "+ mY);
	    		// Find the index of the active pointer and fetch its position
	    		final int pointerIndex = event.findPointerIndex(mActivePointerId);
	    		final float x1 = event.getX(pointerIndex);
	    		final float y1 = event.getY(pointerIndex);
	
	    		// Only move if the ScaleGestureDetector isn't processing a gesture.
	    		if (!testSurfaceView.mScaleDetector.isInProgress()) {
	    			final float dx = x1 - mLastTouchX;
	    			final float dy = y1 - mLastTouchY;
	
	    			mX += dx;
	    			mY += dy;
	    			matrix.postTranslate(mX, mY);
	    			//                  Log.d("Counting", "Move:"+ mX + " | "+ mY +" | "+ testSurfaceView.mScaleFactor);
	    			mX = mY = 1;
	
	    			testSurfaceView.rebound();
	    		}
	
	    		mLastTouchX = (int) x1;
	    		mLastTouchY = (int) y1;            
	
	    		break;
	    	}
	    	case MotionEvent.ACTION_UP: {
	    		mActivePointerId = INVALID_POINTER_ID;
	    		break;
	    	}	
	    	case MotionEvent.ACTION_CANCEL: {
	    		mActivePointerId = INVALID_POINTER_ID;
	    		break;
	    	}	
	    	case MotionEvent.ACTION_POINTER_UP: {
	    		// Extract the index of the pointer that left the touch sensor
	    		final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	    		>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	    		final int pointerId = event.getPointerId(pointerIndex);
	    		if (pointerId == mActivePointerId) {
	    			// This was our active pointer going up. Choose a new
	    			// active pointer and adjust accordingly.
	    			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	    			mLastTouchX = (int) event.getX(newPointerIndex);
	    			mLastTouchY = (int) event.getY(newPointerIndex);
	    			mActivePointerId = event.getPointerId(newPointerIndex);
	    			Log.d("Counting", "We were there: "+ mLastTouchX + " | "+ mLastTouchY);
	    		}
	    		break;
	    	}
    	}	

    	return true;
    }
    
    public class TestSurfaceView extends SurfaceView implements Runnable{
    	private static final int LEVEL_SCALE = 2;
		GameGrid gameGrid;
    	private SurfaceHolder surfaceHolder;
    	private Thread thread = null;
    	private boolean isRunning = true;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Paint paint;
		private ScaleGestureDetector mScaleDetector;
		private float mScaleFactor;
		private Bitmap fBitmap;
		private Canvas fCanvas;
		private Bitmap iBitmap;
    	
		public TestSurfaceView(Context context) {
			super(context);
			surfaceHolder = getHolder();
			mScaleFactor = 0.5f;
			mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		}
		
	    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
	        public boolean onScale(ScaleGestureDetector detector) {
	            mScaleFactor *= (detector.getScaleFactor() - ((detector.getScaleFactor()-1)/1.5));
//	            Log.d("Counting", "SL SF "+ String.valueOf(mScaleFactor));
//	            Log.d("Counting", "Detector factor "+ detector.getScaleFactor());

	            // Don't let the object get too small or too large.
	            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 1.0f));
//	            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 2.0f));
	            rebound();
	            
//	            invalidate();
	            return true;
	        }
	    }
	    
		public void rebound() {
		    // make a rectangle representing what our current canvas looks like
		    RectF currentBounds = new RectF(0, 0, drawW * mScaleFactor, drawH * mScaleFactor);
		    matrix.mapRect(currentBounds);
		    // make a rectangle representing the scroll bounds
		    RectF areaBounds = new RectF(getLeft(),
		                                   getTop(),
		                                   parentW + getLeft(),
		                                   parentH + getTop());

//		    Log.d("Counting", "Area: "+ areaBounds.toString());
//		    Log.d("Counting", "Current: "+ currentBounds.toString());
		    // the difference between the current rectangle and the rectangle we want
		    PointF diff = new PointF(0f, 0f);

		    // x-direction
		    if (currentBounds.width() > areaBounds.width()) {
		        // allow scrolling only if the amount of content is too wide at this scale
		        if (currentBounds.left > areaBounds.left) {
		            // stop from scrolling too far left
		            diff.x = (areaBounds.left - currentBounds.left);
		        }
		        if (currentBounds.right < areaBounds.right) {
		            // stop from scrolling too far right
		            diff.x = (areaBounds.right - currentBounds.right);
		        }
		    } else {
		        // negate any scrolling
		        diff.x = (areaBounds.left - currentBounds.left);
		    }

		    // y-direction
		    if (currentBounds.height() > areaBounds.height()) {
		        // allow scrolling only if the amount of content is too tall at this scale
		        if (currentBounds.top > areaBounds.top) {
		            // stop from scrolling too far above
		            diff.y = (areaBounds.top - currentBounds.top);
		        }
		        if (currentBounds.bottom < areaBounds.bottom) {
		            // stop from scrolling too far below
		            diff.y = (areaBounds.bottom - currentBounds.bottom);
		        }
		    } else {
		        // negate any scrolling
		        diff.y = (areaBounds.top - currentBounds.top);
		    }
		    
		    

		    // translate
		    matrix.postTranslate(diff.x, diff.y);
		}
		
		public void pause() {
			isRunning = false;
			while (true) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			
			thread = null;
		}
		
		public void resume() {
			isRunning = true;
			thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			while (isRunning) {
				if (!surfaceHolder.getSurface().isValid()) {
					continue;
				}
				
				Canvas canvas = surfaceHolder.lockCanvas();
//				Log.d("Counting", "Scale factor "+ mScaleFactor);
				canvas.setMatrix(matrix);
				canvas.scale(mScaleFactor, mScaleFactor); //, mScaleDetector.getFocusX(), mScaleDetector.getFocusY()); 
				

				float[] tempArr = new float[9];
				Matrix tMatrix = canvas.getMatrix();
				tMatrix.getValues(tempArr);
		        offsetX = tempArr[2];
		        offsetY = tempArr[5];
				
				canvas.drawRGB(255, 255, 255);

				canvas.drawBitmap(fBitmap, 0, 0, null);
				canvas.drawBitmap(mBitmap, 0, 0, null);
				canvas.drawBitmap(iBitmap, 0, 0, null);

				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
		
	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
		       
	        parentW = w;
	        parentH = h;
	        w = w*LEVEL_SCALE;
	        h = h*LEVEL_SCALE;
	        drawW = w;
	        drawH = h;
	    	
	        gameGrid = new GameGrid(2, w, h, 320, new float[]{4,4,0,3,1,3,0,3,0,2,1,3,1,2,1,1,1,0,1,1,2,1,2,1,2,0,3,2,3,1});
	        
	        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        mCanvas = new Canvas(mBitmap);
	
	        fBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        fCanvas = new Canvas(fBitmap);
	        
	        paint = new Paint();
	        paint.setColor(Color.rgb(0,0,0));
	      	
	        iBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        Canvas initCanvas = new Canvas(iBitmap);
	       
	        matrix = initCanvas.getMatrix();
	        
	        paint.setStrokeWidth(6*LEVEL_SCALE);
	        
	        drawDottedGrid(fCanvas, gameGrid.getGuideLinesArray());
	        drawCircles(gameGrid.getmPts(), paint.getStrokeWidth(), initCanvas, paint);
	        drawInitialLines(initCanvas, gameGrid.getFixedLinesArray());
	        drawStartFinish(initCanvas, gameGrid.getStartPoint(), gameGrid.getEndPoint());        
	    }
	    
	    public void touchDraw(float x, float y) {
	    	gameGrid.findClosestPoints(x, y);
	        float[] line = gameGrid.getClosestPoints();
	        
	        ArrayList<float[]> temp = gameGrid.getDrawnLines();
	        paint.setTextSize(24);
	        
	        // Do nothing if it's a fixed line
	        if (gameGrid.isFixedLine(line)) {
				return;
			}
	        // Do nothing if one of the points is the start or the finish point
	        if (gameGrid.lineVisitsStartFinish(line)) {
				return;
			}
	        
	        if (gameGrid.isAlreadyDrawn(line)) {
	        	gameGrid.removeLine(line);
	        	Xfermode originalXfermode = paint.getXfermode();
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		        //paint.setColor(Color.WHITE);
		        mCanvas.drawLines(line, paint);
		        paint.setXfermode(originalXfermode);
	        }
	        else {
	        	if (gameGrid.canBeDrawn(line)) {
	        		gameGrid.addLine(line);
	            	paint.setColor(Color.rgb(92,172,238));	        
	    	        mCanvas.drawLines(line, paint);
				}
	        	else {
	        		paint.setAlpha(100);
	        		paint.setColor(Color.RED);	        
	    	        //fCanvas.drawLines(line, paint);
	    	        gameGrid.setFadingLine(true);
	        	}
	        }
	        
	        if (gameGrid.levelIsSolved()) {
	        	paint.setColor(Color.RED);
	            String positions = "Level Complete!";
	            mCanvas.drawText(positions, 100, 500, paint);
	            Log.d("Counting", "Level Complete");
			} 
	    }
	    
	    public void drawDottedGrid(Canvas canvas, float[] lines) {
	    	Paint paintLine = new Paint();
	    	paintLine.setColor(Color.rgb(200,200,200));
	    	paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
	    	paintLine.setStrokeWidth(4*LEVEL_SCALE);
	    	paintLine.setPathEffect(new DashPathEffect(new float[] {7,14}, 0));
	    	canvas.drawLines(lines, paintLine);
	    }
	    
		public void drawCircles(float[] pts, float radius, Canvas canvas, Paint paint) {
	    	for (int i = 0; i < pts.length-1; i++) {
				if (i % 2 == 0) {
					Log.d("Counting", "circles!!!");
					canvas.drawCircle(pts[i], pts[i+1], radius, paint);
				}
			}
	    }
		
		public void drawStartFinish(Canvas canvas, float[] start, float[] finish) {
			Paint paintPoint = new Paint();
			
			paintPoint.setColor(Color.rgb(163,47,163));
			canvas.drawCircle(start[0], start[1], 9*LEVEL_SCALE, paintPoint);
			
			paintPoint.setColor(Color.rgb(162,47,163));
			canvas.drawCircle(finish[0], finish[1], 9*LEVEL_SCALE, paintPoint);
		}
		
		public void drawInitialLines(Canvas canvas, float[] lines) {
			Paint paintPoint = new Paint();
			paintPoint.setColor(Color.BLACK);
			paintPoint.setStrokeWidth(6*LEVEL_SCALE);
			canvas.drawLines(lines, paintPoint);
		}
    }
}