package com.android.mcameron.singletrack;

import com.android.mcameron.singletrack.DrawSurfaceView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class SingleTrackActivity extends Activity {
    /** Called when the activity is first created. */
	DrawSurfaceView drawSurfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the level passed from the grid
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	Log.d("Counting", "Extras: "+ extras.getString("LEVEL_ID"));
	        
        	// Load the level information
	        int index = Integer.parseInt(extras.getString("LEVEL_ID"));
	        Levels levels = new Levels();
	        float[] level = levels.getLevel(index);
	        
	        // Draw and setup level
	        drawSurfaceView = new DrawSurfaceView(this);
	        drawSurfaceView.setLevel(level);
	        drawSurfaceView.setBackgroundColor(Color.WHITE);
	        setContentView(drawSurfaceView);
        }
    }
}