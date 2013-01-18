package com.android.mcameron.singletrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class SingleTrackActivity extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(new MainGamePanel(this));
        
//        // Get the level passed from the grid
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {	        
//        	// Load the level information
//	        int index = Integer.parseInt(extras.getString("LEVEL_ID"));
//	        Levels levels = new Levels();
//	        float[] level = levels.getLevel(index);
//	        
//	        // Draw and setup level
//	        drawSurfaceView = new DrawSurfaceView(this);
//	        drawSurfaceView.setLevel(level);
////	        drawSurfaceView.setBackgroundColor(Color.WHITE);
//	        setContentView(drawSurfaceView);
//        }
    }
    
    public void restartActivity() {
    	finish();
    	startActivity(new Intent(this.getBaseContext(), SingleTrackActivity.class));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
   
    @Override
    public void onResume() {
    	super.onResume();
    }
    
	@Override
	protected void onDestroy() {
		Log.d("Counting", "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d("Counting", "Stopping...");
		super.onStop();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Free up memory by killing activity
    	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}