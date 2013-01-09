package com.android.mcameron.singletrack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class Options extends Activity implements OnTouchListener {
    /** Called when the activity is first created. */
	
	TestSurfaceView testSurfaceView;
	
    private float mX, mY, mLastTouchX, mLastTouchY;
	private Matrix matrix;
    private float drawW, drawH, parentW, parentH, distanceMoved;
    private float offsetX = 0;
    private float offsetY = 0;
    
    private boolean isDrag = false;
    
    private int numberOfMoves;
    
    private static final int INVALID_POINTER_ID = -1;

    // The Ôactive pointerÕ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the selected level from the Global settings
        Globals globals = (Globals) getApplicationContext();
        Levels levels = new Levels();
        float[] level = levels.getLevel(Integer.parseInt(globals.getCurrentLevel()));
        
        // Draw and setup level
        testSurfaceView = new TestSurfaceView(this);
        testSurfaceView.setLevel(level);
//	    testSurfaceView.setBackgroundColor(Color.WHITE);
        testSurfaceView.setOnTouchListener(this);
        setContentView(testSurfaceView);
        
        numberOfMoves = 0;
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
//    	testSurfaceView.mScaleDetector.onTouchEvent(event);
    	//Log.d("Counting", "x: "+ x + " | y: "+ y +" | mX: "+ mX + " | mY: "+ mY +" | mLastTouchX: "+ mLastTouchX +" | mLastTouchY: "+ mLastTouchY);
    	final int action = event.getAction();
    	switch (action & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN: {
	    		final float x = event.getX();
	    		final float y = event.getY();
	
//	    		Log.d("Counting", "DOWN: "+ offsetX + " | "+ offsetY +" | "+ testSurfaceView.mScaleFactor);
	    		
	    		mLastTouchX = (int) x;
	    		mLastTouchY = (int) y;
	    		
	    		isDrag = false;
	    		distanceMoved = 0;
	
	    		// Save the ID of this pointer
	    		mActivePointerId = event.getPointerId(0);
	
	    		break;
	    	}
	    	case MotionEvent.ACTION_POINTER_DOWN: {
	    		final float x = event.getX();
	    		final float y = event.getY();
//	    		Log.d("Counting", "Pointer_Down: "+ x + " | "+ y);
	    		break;
	    	}
	    	case MotionEvent.ACTION_MOVE: {
	    		if (event.getPointerCount() > 1) {
	    			break;
	    		}
	    		
//	    		Log.d("Counting", "Move:"+ mX + " | "+ mY);
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
//	    			Log.d("Counting", "Move:"+ mX + " | "+ mY +" | "+ testSurfaceView.mScaleFactor);
//	    			Log.d("Counting", "Move Down: "+ offsetX + " | "+ offsetY +" | "+ testSurfaceView.mScaleFactor);
	    			offsetX += mX; 
			        offsetY += mY;
			        
			        distanceMoved += Math.abs(mX) + Math.abs(mY);

	    			mX = mY = 0;
	    			
	    			testSurfaceView.rebound();
	    		}
	    		
	    		// Only say we're dragging if we've moved more then X number
	    		// of pixels combined to avoid small jerks on touch
	    		if (distanceMoved > 8) {
	    			isDrag = true;
	    		}
	    		
	    		mLastTouchX = x1;
	    		mLastTouchY = y1;            
	
	    		break;
	    	}
	    	case MotionEvent.ACTION_UP: {
//	    		Log.d("Counting", "UP: "+ isDrag);

	    		if (!isDrag) {
		    		final float x = event.getX();
		    		final float y = event.getY();
		
		    		final float tX = (x / testSurfaceView.mScaleFactor) - (mX / testSurfaceView.mScaleFactor) - (offsetX / testSurfaceView.mScaleFactor);
		    		final float tY = (y / testSurfaceView.mScaleFactor) - (mY / testSurfaceView.mScaleFactor) - (offsetY / testSurfaceView.mScaleFactor);
		
		    		testSurfaceView.touchDraw(tX, tY);
	    		}
	    		
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
//	    			Log.d("Counting", "We were there: "+ mLastTouchX + " | "+ mLastTouchY);
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
		private int width;
		private int height;
		private float bmWidth;
		private float bmHeight;
		private float saveScale;
		private float scaleMappingRatio;
		private float redundantYSpace;
		private float redundantXSpace;
		private float origWidth;
		private float origHeight;
		private float right;
		private float bottom;
		private int screenDensity;
		private Queue<int[]> invalidLines = new LinkedList<int[]>();
		
		private float[] levelConfig = new float[]{6,6,0,3,1,3,0,3,0,2,1,3,1,2,1,1,1,0,1,1,2,1,2,1,2,0,3,2,3,1};
		
		public TestSurfaceView(Context context) {
			super(context);
			surfaceHolder = getHolder();
			mScaleFactor = 1.0f;
			mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	        DisplayMetrics dm = context.getResources().getDisplayMetrics();
	        screenDensity = dm.densityDpi;
		}
		
	    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    	private float maxScale = 1f;
			private float minScale = 0.5f;
			
			float[] m = new float[9];

			@Override
	    	public boolean onScale(ScaleGestureDetector detector) {
	    	    float mScaleFactor = (float) Math.min(
	    	        Math.max(.8f, detector.getScaleFactor()), 1.2);
	    	    float origScale = saveScale;
	    	    saveScale *= mScaleFactor;
	    	    if (saveScale > maxScale) {
	    	        saveScale = maxScale;
	    	        mScaleFactor = maxScale / origScale;
	    	    } else if (saveScale < minScale) {
	    	        saveScale = minScale;
	    	        mScaleFactor = minScale / origScale;
	    	    }
	    	    right = width * saveScale - width
	    	            - (2 * redundantXSpace * saveScale);
	    	    bottom = height * saveScale - height
	    	            - (2 * redundantYSpace * saveScale);
	    	    if (origWidth * saveScale <= width
	    	            || origHeight * saveScale <= height) {
	    	        matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
	    	        if (mScaleFactor < 1) {
	    	            matrix.getValues(m);
	    	            float x = m[Matrix.MTRANS_X];
	    	            float y = m[Matrix.MTRANS_Y];
	    	            if (mScaleFactor < 1) {
	    	                if (Math.round(origWidth * saveScale) < width) {
	    	                    if (y < -bottom)
	    	                        matrix.postTranslate(0, -(y + bottom));
	    	                    else if (y > 0)
	    	                        matrix.postTranslate(0, -y);
	    	                } else {
	    	                    if (x < -right)
	    	                        matrix.postTranslate(-(x + right), 0);
	    	                    else if (x > 0)
	    	                        matrix.postTranslate(-x, 0);
	    	                }
	    	            }
	    	        }
	    	    } else {
	    	        matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
	    	        matrix.getValues(m);
	    	        float x = m[Matrix.MTRANS_X];
	    	        float y = m[Matrix.MTRANS_Y];
	    	        if (mScaleFactor < 1) {
	    	            if (x < -right)
	    	                matrix.postTranslate(-(x + right), 0);
	    	            else if (x > 0)
	    	                matrix.postTranslate(-x, 0);
	    	            if (y < -bottom)
	    	                matrix.postTranslate(0, -(y + bottom));
	    	            else if (y > 0)
	    	                matrix.postTranslate(0, -y);
	    	        }
	    	    }
	    	    return true;
	    	}

	    }
	    
		public void rebound() {
		    // make a rectangle representing what our current canvas looks like
		    RectF currentBounds = new RectF(offsetX, offsetY, (drawW * mScaleFactor) + offsetX, (drawH * mScaleFactor) + offsetY);
		    
		    // make a rectangle representing the scroll bounds
		    RectF areaBounds = new RectF(getLeft(), getTop(), parentW + getLeft(), parentH + getTop());

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
		        // if the bitmap is smaller then the width of the screen
		    	if (currentBounds.left < areaBounds.left) {
		    		// Stop from going past left edge
		    		diff.x = (areaBounds.left - currentBounds.left);
		    	}
		    	else if (currentBounds.right > areaBounds.right) {
		    		// Stop from going past right edge
		    		diff.x = (areaBounds.right - currentBounds.right);
		    	}
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
		    	// if the bitmap is smaller then the width of the screen
		    	if (currentBounds.top < areaBounds.top) {
		    		// Stop from going past top edge
		    		diff.y = (areaBounds.top - currentBounds.top);
		    	}
		    	else if (currentBounds.bottom > areaBounds.bottom) {
		    		// Stop from going past bottom edge
		    		diff.y = (areaBounds.bottom - currentBounds.bottom);
		    	}
		    }
		    
		    // Translate
		    matrix.postTranslate(diff.x, diff.y);
		    offsetX += diff.x;
		    offsetY += diff.y;
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
		
//		@Override
//		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//			width = MeasureSpec.getSize(widthMeasureSpec); // Change this according to your screen size
//			height = MeasureSpec.getSize(heightMeasureSpec); // Change this according to your screen size
//
//			if (fBitmap == null) {
//				setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
//				return;
//			}
//			
//			bmWidth = fBitmap.getWidth();
//			bmHeight = fBitmap.getHeight();
//			
//			// Fit to screen.
//			float scale;
//			float scaleX = (float) width / (float) (bmWidth/2);
//			float scaleY = (float) height / (float) (bmHeight/2);
//			Log.d("Counting", "Scale factor "+ scaleX +" "+ scaleY);
//			Log.d("Counting", "Scale factor "+ width +" "+ bmWidth);
//			Log.d("Counting", "Scale factor "+ height +" "+ bmHeight);
//			scale = Math.min(scaleX, scaleY);
//			matrix.setScale(scale, scale);
////			setImageMatrix(matrix);
//			saveScale = 1f;
//			scaleMappingRatio = saveScale / scale;
//
//			// Center the image
//			redundantYSpace = (float) height - (scale * (float) bmHeight);
//			redundantXSpace = (float) width - (scale * (float) bmWidth);
//			redundantYSpace /= (float) 2;
//			redundantXSpace /= (float) 2;
//
//			matrix.postTranslate(redundantXSpace, redundantYSpace);
//
//			origWidth = width - 2 * redundantXSpace;
//			origHeight = height - 2 * redundantYSpace;
//			right = width * saveScale - width - (2 * redundantXSpace * saveScale);
//			bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
////			setImageMatrix(matrix);
//			setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
//		}
		
		@Override
		public void run() {
			while (isRunning) {
				if (!surfaceHolder.getSurface().isValid()) {
					continue;
				}
		    	decreaseLineOpacity();
        		drawLinesInvalid();

				Canvas canvas = surfaceHolder.lockCanvas();
//				Log.d("Counting", "Scale factor "+ parentW +" "+ parentH);
				canvas.setMatrix(matrix);
				canvas.scale(mScaleFactor, mScaleFactor); //, mScaleDetector.getFocusX(), mScaleDetector.getFocusY()); 

				float[] tempArr = new float[9];
				Matrix tMatrix = canvas.getMatrix();
				tMatrix.getValues(tempArr);

//		        Log.d("Counting", "Canvas w "+ canvas.getWidth() +" h "+ canvas.getHeight() +" density "+ canvas.getDensity() );
				canvas.drawRGB(255, 255, 255);
				
				float centerPointWidth = parentW / 2;
				float centerPointHeight = parentH / 2;
				centerPointWidth = centerPointWidth - ((fBitmap.getWidth()*mScaleFactor)/2);
				centerPointHeight = centerPointHeight- ((fBitmap.getHeight()*mScaleFactor)/2);
//				Log.d("Counting", "Image "+ fBitmap.getWidth() +" "+ fBitmap.getHeight() +" "+ centerPointWidth +" "+ centerPointHeight +" "+ ((fBitmap.getWidth()*mScaleFactor)/2) +" "+ ((fBitmap.getHeight()*mScaleFactor)/2) +" "+ mScaleFactor);
//				Log.d("Counting", "cpWidth "+ centerPointWidth +" cpHeight "+ centerPointHeight +" xOffset "+ offsetX +" yOffset "+ offsetY +" bmpWidth "+ fBitmap.getWidth());
				canvas.drawBitmap(fBitmap, centerPointWidth, centerPointHeight, null);
				canvas.drawBitmap(mBitmap, centerPointWidth, centerPointHeight, null);
				canvas.drawBitmap(iBitmap, centerPointWidth, centerPointHeight, null);

//				canvas.drawLine(77, 353, 660, 936, paint);
//				canvas.drawLine(0, 25, 1440, 25, paint);
//				canvas.drawLine(0, 50, 720, 50, paint);
				
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
		
	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
		       
	        parentW = w;
	        parentH = h;
//	        Log.d("Counting", "pW "+ parentW +" pH "+ parentH);
	        gameGrid = new GameGrid(2, w, h, screenDensity, levelConfig);
	        int[] bitmapDimensions = gameGrid.getBitmapDimensions();
	        
	        w = bitmapDimensions[0];
	        h = bitmapDimensions[1];
	        drawW = w;
	        drawH = h;

//	        Log.d("Counting", "w "+ w +" h "+ h);
//	        Log.d("Counting", "dW "+ drawW +" dH "+ drawH);
	        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        mCanvas = new Canvas(mBitmap);
	
	        fBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        fCanvas = new Canvas(fBitmap);
	        
	        paint = new Paint();
	        paint.setAntiAlias(true);
	        paint.setFilterBitmap(true);
	        paint.setDither(true);
	        paint.setColor(Color.rgb(0,0,0));
	      	
	        iBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
	        Canvas initCanvas = new Canvas(iBitmap);
	       
	        matrix = initCanvas.getMatrix();
	        
	        offsetX = (parentW/2) - ((fBitmap.getWidth()*mScaleFactor)/2);
			offsetY = (parentH/2) - ((fBitmap.getHeight()*mScaleFactor)/2);
//			Log.d("Counting", "Initial offset "+ offsetX +" "+ offsetY);
	        paint.setStrokeWidth(6*LEVEL_SCALE);
	        
	        drawDottedGrid(fCanvas, gameGrid.getGuideLinesArray());
	        drawCircles(gameGrid.getmPts(), paint.getStrokeWidth(), initCanvas, paint);
	        drawInitialLines(initCanvas, gameGrid.getFixedLinesArray());
	        drawStartFinish(initCanvas, gameGrid.getStartPoint(), gameGrid.getEndPoint());        
	    }
	    
	    public void touchDraw(float x, float y) {
	    	gameGrid.findClosestPoints(x, y);
	        float[] line = gameGrid.getClosestPoints();
	        
//	        ArrayList<float[]> temp = gameGrid.getDrawnLines();
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
	        	deleteLineValid(line);
		        numberOfMoves++;
	        }
	        else {
	        	if (gameGrid.canBeDrawn(line)) {
	        		drawLineValid(line);
	    	        numberOfMoves++;
	        		checkIfLevelIsSolved();
				}
	        	else if (!gameGrid.isOutsideGrid(line)) {
	        		addLineToInvalidList(line);
	        	}
	        }
	    }
	    
	    private void drawLineValid(float[] line) {
	    	gameGrid.addLine(line);
        	paint.setColor(Color.rgb(92,172,238));	        
	        mCanvas.drawLines(line, paint);
	    }
	    
	    private void drawLinesInvalid() {
    		Iterator<int[]> iterator = invalidLines.iterator();
	    	while (iterator.hasNext()) {
	    		int[] invalidLine = iterator.next();
	    		float[] line = {invalidLine[1], invalidLine[2], invalidLine[3], invalidLine[4]};
	        	Xfermode originalXfermode = paint.getXfermode();
	        	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	    		mCanvas.drawLines(line, paint);
	    		paint.setXfermode(originalXfermode);
	    		
	    		paint.setColor(Color.RED);
	    		paint.setAlpha(invalidLine[0]);
		    	
		    	mCanvas.drawLines(line, paint);
		    	
	    	}
	    }
	    
	    private void addLineToInvalidList(float[] line) {
	    	int[] lineWithOpacity = new int[5];
	    	lineWithOpacity[0] = 250;
	    	lineWithOpacity[1] = (int) line[0];
	    	lineWithOpacity[2] = (int) line[1];
	    	lineWithOpacity[3] = (int) line[2];
	    	lineWithOpacity[4] = (int) line[3];
	    	invalidLines.add(lineWithOpacity);
	    }
	    
	    private void decreaseLineOpacity() {
	    	Iterator<int[]> iterator = invalidLines.iterator();
	    	while (iterator.hasNext()) {
	    		int[] line = iterator.next();
	    		if (line[0] == 0) {
	    			iterator.remove();
	    		}
			}
	    	
	    	iterator = invalidLines.iterator();
	    	while (iterator.hasNext()) {
	    		int[] line = iterator.next();
	    		line[0] -= 10;
	    		Log.d("Counting", "Opacity: "+ line[0]);
			}
	    }
	    
	    
	    
	    private void deleteLineValid(float[] line) {
        	gameGrid.removeLine(line);
        	Xfermode originalXfermode = paint.getXfermode();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	        //paint.setColor(Color.WHITE);
	        mCanvas.drawLines(line, paint);
	        paint.setXfermode(originalXfermode);
	    }
	    
	    private void checkIfLevelIsSolved() {
	        if (gameGrid.levelIsSolved()) {
	        	setCurrentLevelState(2);
	        	unlockNextLevel();
	        	showLevelCompletePopup();
			} 
	    }
	    
	    private void setCurrentLevelState(int levelState) {
	    	AppPreferences appPrefs = new AppPreferences(getContext());
	    	Globals globals = (Globals) getApplicationContext();
	    	
	    	String currentLevel = globals.getCurrentLevel();
	    	appPrefs.setLevelState(globals.getCurrentPack(), String.format("%02d", Integer.parseInt(currentLevel)), levelState);
	    }
	    
	    private void unlockNextLevel() {
	    	AppPreferences appPrefs = new AppPreferences(getContext());
	    	Globals globals = (Globals) getApplicationContext();
	    	
	    	int unlockLevel = appPrefs.getHighestUnlockedLevel();
	    	appPrefs.setLevelState(globals.getCurrentPack(), String.format("%02d", unlockLevel), Globals.LEVEL_ENABLED);
	    }
	    
	    public void showLevelCompletePopup() {
	    	Dialog dialog;
	    	
	    	// Set the Theme depending on the API version so we have Holo for 4.X.X
	    	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    	if (currentapiVersion < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
	    		dialog = new Dialog(this.getContext(), R.style.Theme_CustomDialog);
	    	} else{
	    		dialog = new Dialog(this.getContext(), R.style.Theme_CustomDialogHolo);
	    	}
        	
	    	// Setup dialog properties
    		dialog.setContentView(R.layout.levelcompleteoverlay);
    		dialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;
    		dialog.setTitle(R.string.levelcomplete);
    		dialog.setCanceledOnTouchOutside(false);

    		// Set the number of moves
    		TextView textViewTableNumberOfMovesValue = (TextView) dialog.findViewById(R.id.textViewTableNumberOfMovesValue);
    		textViewTableNumberOfMovesValue.setText(Integer.toString(numberOfMoves));
    		
    		// Make the Next Level button clickable
    		setupButtonNextLevel(dialog);
    		setupButtonBackToLevelSelect(dialog);
    		
    		dialog.show();
	    }
	    
	    private void setupButtonNextLevel(final Dialog dialog) {
	        Button buttonNextLevel = (Button) dialog.findViewById(R.id.btnNextLevel);
	        buttonNextLevel.setOnClickListener(new OnClickListener() {
	        	
	        	@Override
				public void onClick(View v) {
	        		Globals globals = (Globals) getApplicationContext();
					globals.setNextLevel();

					Intent currentIntent = (Intent) getIntent();
					finish();
	        		startActivity(currentIntent);
	        		dialog.dismiss();
	        	}
	        });
	    }
	    
	    private void setupButtonBackToLevelSelect(final Dialog dialog) {
	        Button btnBackToMainMenu = (Button) dialog.findViewById(R.id.btnBackToLevelSelect);
	        btnBackToMainMenu.setOnClickListener(new OnClickListener() {
	        	
	        	@Override
				public void onClick(View v) {
	        		finish();
	        		dialog.dismiss();
	        	}
	        });
	    }
	    
	    public void drawDottedGrid(Canvas canvas, float[] lines) {
	    	Paint paintLine = new Paint();
	    	paintLine.setColor(Color.rgb(200,200,200));
	    	paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
	    	paintLine.setStrokeWidth(4*LEVEL_SCALE);
	    	paintLine.setPathEffect(new DashPathEffect(new float[] {7,14}, 0));
	    	canvas.drawColor(Color.WHITE);
	    	canvas.drawLines(lines, paintLine);
	    }
	    
		public void drawCircles(float[] pts, float radius, Canvas canvas, Paint paint) {
	    	for (int i = 0; i < pts.length-1; i++) {
				if (i % 2 == 0) {
					//Log.d("Counting", "circles!!!");
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
		
		public void setLevel(float[] level) {
	    	levelConfig = level;
	    }
    }
}