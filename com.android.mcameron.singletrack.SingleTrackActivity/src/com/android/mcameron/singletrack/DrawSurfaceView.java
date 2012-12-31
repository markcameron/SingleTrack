package com.android.mcameron.singletrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;

public class DrawSurfaceView extends SurfaceView implements Runnable {
	
	private static final int FADE_ALPHA = 0x06;
	private static final int MAX_FADE_STEPS = 256/FADE_ALPHA + 4;
	private static final int LEVEL_SCALE = 2;
	
	private int mFadeSteps = MAX_FADE_STEPS;
	
	private final Paint paint = new Paint();
//    private final Paint mFadePaint;

    GameGrid gameGrid;
    
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor;
    
    private Matrix matrix;
    
    private Bitmap iBitmap;
    private Canvas fCanvas;
    private Bitmap fBitmap;
    public  Canvas mCanvas;
    private Bitmap mBitmap;
    private String positions = "Vals:";
    
    private int mX, mY, mLastTouchX, mLastTouchY;
    private float drawW, drawH, parentW, parentH;
    private float offsetX = 0;
    private float offsetY = 0;
    
    private float[] levelConfig;
    
	private SurfaceHolder surfaceHolder;
	private Thread gameThread = null;
	
	private boolean gameIsRunning = false;
    
    public DrawSurfaceView(Context context) {
        super(context);
        
        // Setup thread
        surfaceHolder = getHolder();
        
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6*LEVEL_SCALE);
        
//        mFadePaint = new Paint();
//        mFadePaint.setDither(true);
//        mFadePaint.setARGB(FADE_ALPHA, 0, 0, 0);
        
        mX = mY = mLastTouchX = mLastTouchY = 0;
        
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScaleFactor = 1f;
    }
    
	public void pause() {
		gameIsRunning = false;
		while (true) {
			try {
				gameThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		
		gameThread = null;
	}
    
    public void resume() {
    	gameIsRunning = true;
    	gameThread = new Thread(this);
    	gameThread.start();
//        gameThread = new Thread(this);
//        startGame();
    }
    
    @Override
	public void run() {
    	while (gameIsRunning) {
			if (!surfaceHolder.getSurface().isValid()) {
				continue;
			}  
			

			
			// TODO Auto-generated method stub
	    	Canvas canvas = surfaceHolder.lockCanvas();
	    	
//	    	canvas.save();
	        //canvas.translate(mX, mY);
	        
//	    	canvas.setMatrix(matrix);
	    	canvas.scale(mScaleFactor, mScaleFactor); //, this.mScaleDetector.getFocusX(), this.mScaleDetector.getFocusY());
	    	canvas.drawColor(Color.WHITE);
	    	// Fade
	    	canvas.drawBitmap(fBitmap, 0, 0, null);
	    	// Lines
	    	canvas.drawBitmap(mBitmap, 0, 0, null);
	    	// Dots
	    	canvas.drawBitmap(iBitmap, 0, 0, null);
	
	    	float[] tempArr = new float[9];
	        Matrix tMatrix = canvas.getMatrix();
	        tMatrix.getValues(tempArr);
	        offsetX = tempArr[2];
	        offsetY = tempArr[5];
//	    	canvas.restore();
//			Log.d("Counting", "onDraw Scalefactor Before"+ Float.toString(mScaleFactor));
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Log.d("Counting", "onDraw Scalefactor After 5"+ Float.toString(mScaleFactor));
			
			surfaceHolder.unlockCanvasAndPost(canvas);

    	}
	}
    
    public void startGame() {
    	if (!gameIsRunning) {
    		gameThread.start();
    		gameIsRunning = true;
    	}
    	else {
    		gameThread.resume();
    	}
    }

    @Override
    public void onDraw(Canvas canvas) {
    	Log.d("Counting", "onDraw");
/*
    	Log.d("Counting", "onDraw Scalefactor"+ Float.toString(mScaleFactor));
    	canvas.save();
        //canvas.translate(mX, mY);
        
    	canvas.setMatrix(matrix);
    	canvas.scale(mScaleFactor, mScaleFactor); //, this.mScaleDetector.getFocusX(), this.mScaleDetector.getFocusY());
    	canvas.drawColor(Color.WHITE);
    	// Fade
    	canvas.drawBitmap(fBitmap, 0, 0, null);
    	// Lines
    	canvas.drawBitmap(mBitmap, 0, 0, null);
    	// Dots
    	canvas.drawBitmap(iBitmap, 0, 0, null);

    	float[] tempArr = new float[9];
        Matrix tMatrix = canvas.getMatrix();
        tMatrix.getValues(tempArr);
        offsetX = tempArr[2];
        offsetY = tempArr[5];
    	canvas.restore();
    	*/
    	
//    	if (gameGrid.isFadingLine()) {
//			try {
//				Thread.sleep(500);
//				changeOpacity();
//				canvas.drawColor(Color.WHITE);
//				canvas.drawBitmap(fBitmap, 0, 0, null);
//		    	// Lines
//		    	canvas.drawBitmap(mBitmap, 0, 0, null);
//		    	// Dots
//		    	canvas.drawBitmap(iBitmap, 0, 0, null);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= (detector.getScaleFactor() - ((detector.getScaleFactor()-1)/1.5));
            Log.d("Counting", "SL SF "+ String.valueOf(mScaleFactor));
            Log.d("Counting", "Detector factor "+ detector.getScaleFactor());

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 1.0f));
//            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 2.0f));
//            rebound();
            
//            invalidate();
            return true;
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

        if (mCanvas == null) {
        	gameGrid = new GameGrid(2, w, h, 320, levelConfig);
        	
	        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        mCanvas = new Canvas(mBitmap);
	
	        fBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        fCanvas = new Canvas(fBitmap);
	        
	        paint.setColor(Color.BLACK);
	      	
	        iBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        Canvas initCanvas = new Canvas(iBitmap);
	       
	        matrix = initCanvas.getMatrix();
	        
	        drawDottedGrid(fCanvas, gameGrid.getGuideLinesArray());
	        drawCircles(gameGrid.getmPts(), paint.getStrokeWidth(), initCanvas, paint);
	        drawInitialLines(initCanvas, gameGrid.getFixedLinesArray());
	        drawStartFinish(initCanvas, gameGrid.getStartPoint(), gameGrid.getEndPoint());
	        
//	        resume();
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

//	    Log.d("Counting", "Area: "+ areaBounds.toString());
//	    Log.d("Counting", "Current: "+ currentBounds.toString());
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

	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(0);
        float y = event.getY(0);

//        mScaleDetector.onTouchEvent(event);
        //Log.d("Counting", "x: "+ x + " | y: "+ y +" | mX: "+ mX + " | mY: "+ mY +" | mLastTouchX: "+ mLastTouchX +" | mLastTouchY: "+ mLastTouchY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
        		final float tX = (x / mScaleFactor) - (mX / mScaleFactor) - (offsetX / mScaleFactor);
        		final float tY = (y / mScaleFactor) - (mY / mScaleFactor) - (offsetY / mScaleFactor) + 19;
        		Log.d("Counting", "Down: "+ offsetX + " | "+ offsetY +" | "+ mScaleFactor);

        		touchDraw(tX, tY);
        	
        		//invalidate();

                mLastTouchX = (int) x;
                mLastTouchY = (int) y;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            	break;
            case MotionEvent.ACTION_MOVE:
                // Only move if the ScaleGestureDetector isn't processing a gesture.
//                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;
                    
                    mX += dx;
                    mY += dy;
                    matrix.postTranslate(mX, mY);
                    Log.d("Counting", "Move:"+ mX + " | "+ mY +" | "+ mScaleFactor);
                    mX = mY = 1;
                    
//                    rebound();
                    //invalidate();
//                }

                mLastTouchX = (int) x;
                mLastTouchY = (int) y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                break;
        }	

        return true;
    }
    
    public void changeOpacity() {
//    	fBitmap.eraseColor(Color.TRANSPARENT);
//    	gameGrid.setFadingLine(false);
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
				canvas.drawCircle(pts[i], pts[i+1], radius, paint);
			}
		}
    }
	
	public void drawStartFinish(Canvas canvas, float[] start, float[] finish) {
		Paint paintPoint = new Paint();
		
		paintPoint.setColor(Color.rgb(0,238,118));
		canvas.drawCircle(start[0], start[1], 9*LEVEL_SCALE, paintPoint);
		
		paintPoint.setColor(Color.rgb(255,99,71));
		canvas.drawCircle(finish[0], finish[1], 9*LEVEL_SCALE, paintPoint);
	}
	
	public void drawInitialLines(Canvas canvas, float[] lines) {
		Paint paintPoint = new Paint();
		paintPoint.setColor(Color.BLACK);
		paintPoint.setStrokeWidth(6*LEVEL_SCALE);
		canvas.drawLines(lines, paintPoint);
	}
	
    public void touchDraw(float x, float y) {
        gameGrid.findClosestPoints(x, y);
        float[] line = gameGrid.getClosestPoints();
        
        ArrayList<float[]> temp = gameGrid.getDrawnLines();
        paint.setTextSize(24);
        String result;
        
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
	        result = Boolean.toString(temp.contains(line));
	        paint.setXfermode(originalXfermode);
        }
        else {
        	if (gameGrid.canBeDrawn(line)) {
        		gameGrid.addLine(line);
            	paint.setColor(Color.rgb(92,172,238));	        
    	        mCanvas.drawLines(line, paint);
    	        result = Boolean.toString(temp.contains(line));
			}
        	else {
        		paint.setAlpha(100);
        		paint.setColor(Color.RED);	        
    	        //fCanvas.drawLines(line, paint);
    	        gameGrid.setFadingLine(true);
        		result = "|";
        	}
        }
        
        if (gameGrid.levelIsSolved()) {
        	paint.setColor(Color.RED);
            positions = "Level Complete!";
            mCanvas.drawText(positions, 100, 500, paint);
            Log.d("Counting", "Level Complete");
		}        
    }
    
    public void setLevel(float[] level) {
    	levelConfig = level;
    }
	
}
