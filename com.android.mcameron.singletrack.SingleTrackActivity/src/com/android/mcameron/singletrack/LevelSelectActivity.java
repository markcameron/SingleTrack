package com.android.mcameron.singletrack;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class LevelSelectActivity extends Activity {
	GridView gridView;
	
	static final String[] numbers = new String[] { 
			"1",  "2",  "3",  "4",  "5",
			"6",  "7",  "8",  "9",  "10",
			"11", "12", "13", "14", "15",
			"16", "17", "18", "19", "20"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.levelselect);
	
		gridView = (GridView) findViewById(R.id.grid);
	
		gridView.setAdapter(new ButtonAdapter(this));	
	}
	
	/**
	 * An adapter for my button so that I can place it inside the custom GridView
	 * 
	 * @author markcameron
	 */
	public class ButtonAdapter extends BaseAdapter {
		private Context mContext; 
		
		// Gets the context so it can be used later  
		public ButtonAdapter(Context c) {  
			mContext = c;  
		}  
		 
		@Override
		public int getCount() {
			Levels levels = new Levels();
	        ArrayList<float[]> levelList = levels.getLevels();
	        
			return levelList.size() - 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Button btn;  
			if (convertView == null) {  
				// if it's not recycled, initialize some attributes  
				btn = new Button(mContext);  
				// Set the font face because I cannot pass it to the Button constructor
				btn.setTextSize(30f);
				btn.setGravity(0x11);
				btn.setTextColor(Color.WHITE);
				btn.setTypeface(null, Typeface.BOLD);
			}  
			else {  
				btn = (Button) convertView;  
			}  

			btn.setText(numbers[position]);
			// Set the button to use my custom black background
			btn.setBackgroundResource(R.drawable.btn_black);  
			btn.setId(position);
			
		    // Set the onclicklistener so that pressing the button fires an event  
		    // We will need to implement this onclicklistner.  
		    btn.setOnClickListener(new MyOnClickListener(position));  
	  
			return btn;
		}
		
		/**
		 * OnClickListener for the button clicks inside the Custom GridView
		 * 
		 * @author markcameron
		 */
	    class MyOnClickListener implements OnClickListener {  
	    	private final int position;  
  
	    	public MyOnClickListener(int position) {  
	    		this.position = position;  
	    	}
  
	    	@Override
			public void onClick(View v) {  
	    		// Open the level corresponding to the level selected in the GridView
	    		Intent StartGameIntent = new Intent(LevelSelectActivity.this, Options.class);
				StartGameIntent.putExtra("LEVEL_ID", numbers[position]);
        		startActivity(StartGameIntent);
	    	}  
	    }  
	}

}// class
