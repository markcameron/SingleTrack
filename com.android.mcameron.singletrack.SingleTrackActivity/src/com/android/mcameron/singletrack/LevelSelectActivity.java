package com.android.mcameron.singletrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, numbers);
	
		gridView.setAdapter(adapter);
	
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
				Intent StartGameIntent = new Intent(LevelSelectActivity.this, SingleTrackActivity.class);
				StartGameIntent.putExtra("LEVEL_ID", numbers[position]);
        		startActivity(StartGameIntent);
			}
		});
	
	}

}// class
