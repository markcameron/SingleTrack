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
		
		final Globals globals = (Globals) getApplicationContext();
		
        Button btnPackSquares = (Button) findViewById(R.id.PackSquares);
        btnPackSquares.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
        		Intent LevelSelectIntent = new Intent(PackSelectActivity.this, LevelSelectActivity.class);
        		LevelSelectIntent.putExtra("PACK_ID", Globals.PACK_SQUARES);
        		globals.setCurrentPack(Globals.PACK_SQUARES);
        		startActivity(LevelSelectIntent);
        	}
        });
        
        Button btnPackRectangles = (Button) findViewById(R.id.PackRectangles);
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
}
