/**
 * 
 */
package com.android.mcameron.singletrack;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author impaler
 * This is the main surface that handles the ontouch events and draws
 * the image to the screen.
 */
/**
 * @author markcameron
 *
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	// Invalid line options
	private static final int RED_LINE_DECREASE_OPACITY_BY = 10;
	private static final int RED_LINE_INITIAL_OPACITY = 250;

	// Scale all parts of the level (lines, dots, etc);
	private static final int LEVEL_SCALE = 2;
	
	// How much to allow pinch to zoom
    private static float MIN_ZOOM = 0.5f;
    private static float MAX_ZOOM = 1f;
	
    // Log.d text for this app
	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private MainThread thread;
	
	private Paint paintFPS = new Paint();

	private Sound sound;
	
	GameGrid gameGrid;
	private Matrix matrix;
	private SurfaceHolder surfaceHolder;
	// The various bitmaps to draw on.
	private Bitmap backgroundBitmap;
	private Bitmap middlegroundBitmap;
	private Bitmap foregroundBitmap;
	private Canvas backgroundCanvas;
	private Canvas middlegroundCanvas;
	private Canvas foregroundCanvas;
	private Paint paint;
	private Paint paintDebug;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor;
	private int screenDensity;
	private CopyOnWriteArrayList<int[]> invalidLines = new CopyOnWriteArrayList<int[]>();
	private float mX, mY, mLastTouchX, mLastTouchY;
    private float distanceMoved;
    private int displayWidth;
    private int displayHeight;
	private int bitmapWidth;
	private int bitmapHeight;
    private float offsetX = 0;
    private float offsetY = 0;
    private float translateX = 0;
    private float translateY = 0;
    private Context activityContext;
	private String avgFps;
	
    private boolean isDrag = false;
    
    private int numberOfMoves;
    
    private static final int INVALID_POINTER_ID = -1;

	protected static final Globals globals = null;

    // The Ôactive pointerÕ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
	
	private float[] levelConfig = new float[]{6,6,0,3,1,3,0,3,0,2,1,3,1,2,1,1,1,0,1,1,2,1,2,1,2,0,3,2,3,1};

	/**
	 * Set the average FPS
	 * 
	 * @param avgFps The string value to set
	 */
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainGamePanel(Context context) {
		super(context);
		
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		surfaceHolder = getHolder();
		
		// Create the game loop thread
		thread = new MainThread(getHolder(), this);

		// Initialize sound
		sound = new Sound(context);
		
		// Initialize stuff
		mScaleFactor = 1.0f;
//		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		activityContext = context;
		
		// Initialize brush to display FPS
		paintFPS.setColor(Color.RED);
		paintFPS.setTextSize(30);
		
        // Get selected level, and set levelConfig
        Levels levels = new Levels();
        float[] level = levels.getLevel(Integer.parseInt(Globals.getCurrentLevel()));
        setLevel(level);
		
		// Get the screen properties
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenDensity = displayMetrics.densityDpi;
        
        // Get the size of the display for our SurfaceView
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        // initialize game grid
        gameGrid = new GameGrid(2, displayWidth, displayHeight, screenDensity, levelConfig);
        int[] bitmapDimensions = gameGrid.getBitmapDimensions();
        
        // Get the size of the grid bitmap to draw
        bitmapWidth = bitmapDimensions[0];
        bitmapHeight = bitmapDimensions[1];

        Log.d(TAG, "bmW: "+ bitmapWidth +" bmH: "+ bitmapHeight +" ggP: "+ gameGrid.getGridPadding() +" ggS: "+ gameGrid.getGridSpacing());
        
        // Create the canvas for the background (dotted grid to show possible lines to draw)
        backgroundBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
        backgroundCanvas = new Canvas(backgroundBitmap);
        
        // Create canvas where the lines the user makes are drawn
        middlegroundBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
        middlegroundCanvas = new Canvas(middlegroundBitmap);

        // Create the foreground canvas where we store the fixed lines, points and start/finish points
        foregroundBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
        foregroundCanvas = new Canvas(foregroundBitmap);

        // The transformation matrix for panning and zooming
        matrix = foregroundCanvas.getMatrix();

        // Get the initial offset of the middle of the bitmap from the top and left edges
        // since we center the game grid on the screen
        offsetX = (displayWidth/2) - ((bitmapWidth*mScaleFactor)/2);
		offsetY = (displayHeight/2) - ((bitmapHeight*mScaleFactor)/2);

		// Setup the brush for the drawing
		paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.rgb(0,0,0));
        paint.setStrokeWidth(6*LEVEL_SCALE);
        
        // Setup debug brush
        paintDebug = new Paint();
        paintDebug.setColor(Color.MAGENTA);
        paintDebug.setStrokeWidth(2);
        
        // Draw all initial lines and points to the respective canvases
        drawDashedGrid(backgroundCanvas, gameGrid.getGuideLinesArray());
        drawPoints(gameGrid.getmPts(), paint.getStrokeWidth(), foregroundCanvas, paint);
        drawInitialLines(foregroundCanvas, gameGrid.getFixedLinesArray());
        drawStartFinish(foregroundCanvas, gameGrid.getStartPoint(), gameGrid.getEndPoint());
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
	}
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		thread.setRunning(false);
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		mScaleDetector.onTouchEvent(event);
    	
//		Log.d("Counting", "mX: "+ mX + " | mY: "+ mY +" | mLastTouchX: "+ mLastTouchX +" | mLastTouchY: "+ mLastTouchY +" OffsetX: "+ offsetX +" offsetY: "+ offsetY);
    	final int action = event.getAction();
    	switch (action & MotionEvent.ACTION_MASK) {
	    	case MotionEvent.ACTION_DOWN: {
	    		final float x = event.getX();
	    		final float y = event.getY();
	
//	    		Log.d("Counting", "DOWN: "+ offsetX + " | "+ offsetY +" | "+ mScaleFactor +" mxl: "+ mLastTouchX +" myl: "+ mLastTouchY);
	    		
	    		mLastTouchX = x;
	    		mLastTouchY = y;
	    		
	    		isDrag = false;
	    		distanceMoved = 0;
	
	    		// Save the ID of this pointer
//	    		mActivePointerId = event.getPointerId(0);
	
	    		break;
	    	}
//	    	case MotionEvent.ACTION_POINTER_DOWN: {
//	    		final float x = event.getX();
//	    		final float y = event.getY();
//	    		Log.d("Counting", "Pointer_Down: "+ x + " | "+ y);
//	    		break;
//	    	}
	    	case MotionEvent.ACTION_MOVE: {
	    		if (event.getPointerCount() > 1) {
	    			break;
	    		}
	    		
//	    		Log.d("Counting", "Move:"+ mX + " | "+ mY);
	    		// Find the index of the active pointer and fetch its position
//	    		final int pointerIndex = event.findPointerIndex(mActivePointerId);
	    		final float x1 = event.getX();
	    		final float y1 = event.getY();
	
	    		// Only move if the ScaleGestureDetector isn't processing a gesture.
//	    		if (!mScaleDetector.isInProgress()) {
	    			final float dx = x1 - mLastTouchX;
	    			final float dy = y1 - mLastTouchY;
	
	    			mX += dx;
	    			mY += dy;
	    			translateX = mX;
	    			translateY = mY;
	    			matrix.postTranslate(mX * mScaleFactor, mY * mScaleFactor);
//	    			Log.d("Counting", "Move:"+ mX + " | "+ mY +" | "+ testSurfaceView.mScaleFactor);
//	    			Log.d("Counting", "Move Down: "+ offsetX + " | "+ offsetY +" | "+ testSurfaceView.mScaleFactor);
//	    			Log.d("Counting", "XXXXXXXXXXXXXXXXXXXXX We are moving!!! Move:"+ mX + " | "+ mY);
	    			offsetX += mX; 
			        offsetY += mY;
			        
			        distanceMoved += Math.abs(mX) + Math.abs(mY);

	    			mX = mY = 0;
	    			
	    			rebound();
//	    		}
	    		
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
		    		final float x = mLastTouchX; //event.getX();
		    		final float y = mLastTouchY; //event.getY();
		
		    		final float tX = (x / mScaleFactor) - (mX / mScaleFactor) - (offsetX / mScaleFactor);
		    		final float tY = (y / mScaleFactor) - (mY / mScaleFactor) - (offsetY / mScaleFactor);
		
		    		touchDraw(tX, tY);
	    		}
	    		
//	    		mActivePointerId = INVALID_POINTER_ID;
	    		
	    		break;
	    	}	
//	    	case MotionEvent.ACTION_CANCEL: {
//	    		mActivePointerId = INVALID_POINTER_ID;
//	    		break;
//	    	}	
//	    	case MotionEvent.ACTION_POINTER_UP: {
//	    		// Extract the index of the pointer that left the touch sensor
//	    		final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
//	    		>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//	    		final int pointerId = event.getPointerId(pointerIndex);
//	    		if (pointerId == mActivePointerId) {
//	    			// This was our active pointer going up. Choose a new
//	    			// active pointer and adjust accordingly.
//	    			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//	    			mLastTouchX = (int) event.getX(newPointerIndex);
//	    			mLastTouchY = (int) event.getY(newPointerIndex);
//	    			mActivePointerId = event.getPointerId(newPointerIndex);
////	    			Log.d("Counting", "We were there: "+ mLastTouchX + " | "+ mLastTouchY);
//	    		}
//	    		break;
//	    	}
    	}	
		return true;
	}

	public void render(Canvas canvas) {
//        Log.d("Counting", "Canvas w "+ canvas.getWidth() +" h "+ canvas.getHeight() +" density "+ canvas.getDensity() );
		if (canvas != null) {
			canvas.drawRGB(255, 255, 255);
			canvas.setMatrix(matrix);
//			canvas.translate(translateY, translateY);
//			Log.d("Counting", "Le Matrix: "+ matrix.toString());
//			canvas.scale(mScaleFactor, mScaleFactor);
//			canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
			
			float centerPointWidth = displayWidth / 2;
			float centerPointHeight = displayHeight / 2;
			centerPointWidth = centerPointWidth - ((backgroundBitmap.getWidth())/2);
			centerPointHeight = centerPointHeight- ((backgroundBitmap.getHeight())/2);
//			Log.d("Counting", "Image "+ fBitmap.getWidth() +" "+ fBitmap.getHeight() +" "+ centerPointWidth +" "+ centerPointHeight +" "+ ((fBitmap.getWidth()*mScaleFactor)/2) +" "+ ((fBitmap.getHeight()*mScaleFactor)/2) +" "+ mScaleFactor);
//			Log.d("Counting", "cpWidth "+ centerPointWidth +" cpHeight "+ centerPointHeight +" xOffset "+ offsetX +" yOffset "+ offsetY +" bmpWidth "+ fBitmap.getWidth());
			canvas.drawBitmap(backgroundBitmap, centerPointWidth, centerPointHeight, null);
			canvas.drawBitmap(middlegroundBitmap, centerPointWidth, centerPointHeight, null);
			canvas.drawBitmap(foregroundBitmap, centerPointWidth, centerPointHeight, null);
			
//			canvas.drawRect(offsetX, offsetY, (bitmapWidth + offsetX) * mScaleFactor, (bitmapHeight + offsetY) * mScaleFactor, paintDebug);
//		    Log.d(TAG, "gridB - L: "+ offsetX +" T: "+ offsetY +" R: "+ ((bitmapWidth + offsetX) * mScaleFactor) +" B: "+ ((bitmapHeight + offsetY) * mScaleFactor));
		    
			// display fps
			displayFps(canvas, avgFps);
		}
	}

	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
    	decreaseLineOpacity();
    	drawLinesInvalid();

//		Canvas canvas = surfaceHolder.lockCanvas();
//		Log.d("Counting", "Scale factor "+ parentW +" "+ parentH);
		 //, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			canvas.drawText(fps, this.getWidth() - 250, 200, paintFPS);
		}
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
			return true;
		}
	}
    
	public void rebound() {
	    // make a rectangle representing what our current canvas looks like
	    RectF gamegridBounds = new RectF(offsetX, offsetY, (bitmapWidth + offsetX) * mScaleFactor, (bitmapHeight + offsetY) * mScaleFactor);
//	    Log.d(TAG, "gridB - L: "+ offsetX +" T: "+ offsetY +" R: "+ ((bitmapWidth + offsetX) * mScaleFactor) +" B: "+ ((bitmapHeight + offsetY) * mScaleFactor));
	    
	    // make a rectangle representing the scroll bounds
	    RectF areaBounds = new RectF(getLeft(), getTop(), displayWidth, displayHeight);
	    Log.d(TAG, "AreaB - L: "+ getLeft() +" T: "+ getTop() +" R: "+ displayWidth +" B: "+ displayHeight);
	    
	    PointF diff = new PointF(0f, 0f);

	    // x-direction
	    if (gamegridBounds.width() > areaBounds.width()) {
	        // allow scrolling only if the amount of content is too wide at this scale
	        if (gamegridBounds.left > areaBounds.left) {
	            // stop from scrolling too far left
	            diff.x = (areaBounds.left - gamegridBounds.left);
	        }
	        if (gamegridBounds.right < areaBounds.right) {
	            // stop from scrolling too far right
	            diff.x = (areaBounds.right - gamegridBounds.right);
	        }
	    } else {
	        // if the bitmap is smaller then the width of the screen
	    	if (gamegridBounds.left < areaBounds.left) {
	    		// Stop from going past left edge
	    		diff.x = (areaBounds.left - gamegridBounds.left);
	    	}
	    	else if (gamegridBounds.right > areaBounds.right) {
	    		// Stop from going past right edge
	    		diff.x = (areaBounds.right - gamegridBounds.right);
	    	}
	    }

	    // y-direction
	    if (gamegridBounds.height() > areaBounds.height()) {
	        // allow scrolling only if the amount of content is too tall at this scale
	        if (gamegridBounds.top > areaBounds.top) {
	            // stop from scrolling too far above
	            diff.y = (areaBounds.top - gamegridBounds.top);
	        }
	        if (gamegridBounds.bottom < areaBounds.bottom) {
	            // stop from scrolling too far below
	            diff.y = (areaBounds.bottom - gamegridBounds.bottom);
	        }
	    } else {
	    	// if the bitmap is smaller then the width of the screen
	    	if (gamegridBounds.top < areaBounds.top) {
	    		// Stop from going past top edge
	    		diff.y = (areaBounds.top - gamegridBounds.top);
	    	}
	    	else if (gamegridBounds.bottom > areaBounds.bottom) {
	    		// Stop from going past bottom edge
	    		diff.y = (areaBounds.bottom - gamegridBounds.bottom);
	    	}
	    }
	    
	    // Translate
	    matrix.postTranslate(diff.x * mScaleFactor, diff.y * mScaleFactor);
	    offsetX += diff.x;
	    offsetY += diff.y;
	}

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
	       
        
    }
    
    public void touchDraw(float x, float y) {
    	gameGrid.findClosestPoints(x, y);
        float[] line = gameGrid.getClosestPoints();
        
//        ArrayList<float[]> temp = gameGrid.getDrawnLines();
        paint.setTextSize(24);
        
        // Do nothing if it's a fixed line
        if (gameGrid.isFixedLine(line)) {
			return;
		}
        // Draw red fading line if one of the points is the start or the finish point
        if (gameGrid.lineVisitsStartFinish(line) && !gameGrid.isOutsideGrid(line)) {
        	addLineToInvalidList(line);
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
        middlegroundCanvas.drawLines(line, paint);
        
        sound.play(Globals.SOUND_TOUCH_DRAW, getContext());
    }
    
    private void drawLinesInvalid() {
		paint.setColor(Color.RED);
		
		for (Iterator<int[]> iterator = invalidLines.iterator(); iterator.hasNext();) {
    		int[] invalidLine = iterator.next();	
    		float[] line = {invalidLine[1], invalidLine[2], invalidLine[3], invalidLine[4]};
        
    		Xfermode originalXfermode = paint.getXfermode();
        	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    		middlegroundCanvas.drawLines(line, paint);
    		paint.setXfermode(originalXfermode);
    		paint.setAlpha(invalidLine[0]);
	    	middlegroundCanvas.drawLines(line, paint);
    	}
    }
    
    private void addLineToInvalidList(float[] line) {	
    	int[] lineWithOpacity = new int[5];
    	lineWithOpacity[0] = RED_LINE_INITIAL_OPACITY;
    	lineWithOpacity[1] = (int) line[0];
    	lineWithOpacity[2] = (int) line[1];
    	lineWithOpacity[3] = (int) line[2];
    	lineWithOpacity[4] = (int) line[3];
    	invalidLines.add(lineWithOpacity);
    	
    	sound.play(Globals.SOUND_TOUCH_WRONG, getContext());
    }
    
    private void decreaseLineOpacity() {
    	int position = 0;
    	for (Iterator<int[]> iterator = invalidLines.iterator(); iterator.hasNext();) {
    		int[] line = iterator.next();
    		if (line[0] <= 0) {
    			invalidLines.remove(position);
    		}
    		position++;
		}

		for (Iterator<int[]> iterator = invalidLines.iterator(); iterator.hasNext();) {
    		int[] line = iterator.next();
    		line[0] -= RED_LINE_DECREASE_OPACITY_BY;
		}
    }
    
    private void deleteLineValid(float[] line) {
    	gameGrid.removeLine(line);
    	Xfermode originalXfermode = paint.getXfermode();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        middlegroundCanvas.drawLines(line, paint);
        paint.setXfermode(originalXfermode);
        
        sound.play(Globals.SOUND_TOUCH_UNDRAW, getContext());
    }
    
    private void checkIfLevelIsSolved() {
        if (gameGrid.levelIsSolved()) {
        	setCurrentLevelState(Globals.LEVEL_SOLVED);
        	unlockNextLevel();
        	showLevelCompletePopup();
		} 
    }
    
    private void setCurrentLevelState(int levelState) {
    	AppPreferences appPrefs = new AppPreferences(getContext());
//    	Globals globals = (Globals) getApplicationContext();
    	
    	String currentLevel = Globals.getCurrentLevel();
    	appPrefs.setLevelState(Globals.getCurrentPack(), String.format("%02d", Integer.parseInt(currentLevel)), levelState);
    	levelState = appPrefs.getLevelState(Globals.getCurrentPack(), String.format("%02d", Integer.parseInt(currentLevel)));
    	Log.d("Counting", "currLevel: "+ currentLevel +" LevelState: "+ levelState);
    }
    
    private void unlockNextLevel() {
    	AppPreferences appPrefs = new AppPreferences(getContext());
//    	Globals globals = (Globals) getApplicationContext();
    	
    	String currentLevel = Globals.getCurrentLevel();
    	int unlockLevel = appPrefs.getHighestUnlockedLevel();
    	if (unlockLevel != Integer.parseInt(currentLevel)) {
    		appPrefs.setLevelState(Globals.getCurrentPack(), String.format("%02d", unlockLevel), Globals.LEVEL_ENABLED);
    	}
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
        buttonNextLevel.setEnabled(Globals.hasNextLevel());
        buttonNextLevel.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
				Globals.incrementLevel();
				
				((Activity) activityContext).finish();
				Context context = getContext(); // from MySurfaceView/Activity
				Intent intent = new Intent(context, SingleTrackActivity.class);
				context.startActivity(intent);

        		dialog.dismiss();
        	}
        });
    }
    
    private void setupButtonBackToLevelSelect(final Dialog dialog) {
        Button btnBackToMainMenu = (Button) dialog.findViewById(R.id.btnBackToLevelSelect);
        btnBackToMainMenu.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
        		((Activity) activityContext).finish();
        		dialog.dismiss();
        	}
        });
    }
    
    public void drawDashedGrid(Canvas canvas, float[] lines) {
    	Paint paintLine = new Paint();
    	paintLine.setColor(Color.rgb(200,200,200));
    	paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
    	paintLine.setStrokeWidth(4*LEVEL_SCALE);
    	paintLine.setPathEffect(new DashPathEffect(new float[] {7,14}, 0));
    	// Bitmap background color to check boundries when moving around screen
//    	canvas.drawColor(Color.CYAN);
    	canvas.drawLines(lines, paintLine);
    }
    
	public void drawPoints(float[] pts, float radius, Canvas canvas, Paint paint) {
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
