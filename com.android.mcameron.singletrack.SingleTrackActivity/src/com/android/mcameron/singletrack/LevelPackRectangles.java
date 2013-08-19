package com.android.mcameron.singletrack;

import java.util.ArrayList;

public class LevelPackRectangles extends Levels {
	public ArrayList<float[]> getLevelPackRectangles() {
		ArrayList <float[]> levelList = new ArrayList<float[]>();
		
		levelList.add(null); //Level 0 - Doesn't exist - saves on silly - 1 operations
		// 1-10
		levelList.add(new float[]{2,4,0,3,0,0,1,3,0,3,1,0,0,0,0,1,1,1,0,2,1,2});
		levelList.add(new float[]{3,4,1,2,1,1,1,2,0,2,1,1,0,1,2,3,1,3,2,0,1,0});
		levelList.add(new float[]{3,4,0,2,2,3,0,3,0,2,2,2,2,3,1,1,0,1});
		levelList.add(new float[]{3,4,2,1,0,2,0,3,0,2,2,1,2,0,1,2,1,1});
		levelList.add(new float[]{3,5,2,2,2,0,2,3,2,2,2,1,2,0,1,3,1,2,1,2,0,2});
		levelList.add(new float[]{3,5,2,4,1,3,1,4,2,4,1,3,2,3,1,2,1,1,1,0,0,0,2,2,2,1});
		levelList.add(new float[]{4,5,0,4,1,4,1,4,1,3,0,4,0,3,2,3,1,3,2,2,1,2,0,2,0,1,2,2,2,1,3,2,3,1});



		return levelList;
	}
}
