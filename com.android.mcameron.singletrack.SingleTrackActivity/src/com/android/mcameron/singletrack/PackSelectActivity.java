package com.android.mcameron.singletrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PackSelectActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.packselect);
		
		this.initButtons();
	}
	
	private void initButtons() {
		final Globals globals = (Globals) getApplicationContext();

		Levels levels = new Levels(getApplicationContext());
		levels.setLevelPack(Globals.PACK_SQUARES);
		
        Button btnPackSquares = (Button) findViewById(R.id.PackSquares);
        btnPackSquares.setText(getString(R.string.pack_select_squares_completed, levels.completedCount(), levels.size()));
        btnPackSquares.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
        		Intent LevelSelectIntent = new Intent(PackSelectActivity.this, LevelSelectActivity.class);
        		LevelSelectIntent.putExtra("PACK_ID", Globals.PACK_SQUARES);
        		globals.setCurrentPack(Globals.PACK_SQUARES);
        		startActivity(LevelSelectIntent);
        	}
        });
        
//        levels = new Levels();
		levels.setLevelPack(Globals.PACK_RECTANGLES);
		
        Button btnPackRectangles = (Button) findViewById(R.id.PackRectangles);
        btnPackRectangles.setText(getString(R.string.pack_select_rectangles_completed, levels.completedCount(), levels.size()));
        btnPackRectangles.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
        		Intent LevelSelectIntent = new Intent(PackSelectActivity.this, LevelSelectActivity.class);
        		LevelSelectIntent.putExtra("PACK_ID", Globals.PACK_RECTANGLES);
        		globals.setCurrentPack(Globals.PACK_RECTANGLES);
        		startActivity(LevelSelectIntent);
        	}
        });
	}
	
	public void onResume() {
		super.onResume();
		this.initButtons();
	}
}
