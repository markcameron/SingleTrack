package com.android.mcameron.singletrack;

import java.util.ArrayList;

public class LevelPackSquares extends Levels {
	public ArrayList<float[]> getLevelPackSquares() {
		ArrayList <float[]> levelList = new ArrayList<float[]>();
		
		levelList.add(null); //Level 0 - Doesn't exist - saves on silly - 1 operations
		// 1-10
		levelList.add(new float[]{2,2,0,0,0,1,1,1,0,1,1,0,0,0});
		levelList.add(new float[]{3,3,0,2,1,1,0,2,0,1,1,2,1,1,1,0,2,0,2,1,2,0});
		levelList.add(new float[]{15,15,2,14,9,13,9,13,10,13,2,14,2,13,5,6,6,6,3,9,4,9,3,8,2,8,4,4,3,4,8,3,9,3,11,3,11,4,12,6,12,5,10,9,10,10,7,10,7,11,8,9,9,9,10,5,9,5,8,6,9,6,6,4,5,4,6,3,6,2,7,3,7,2,9,2,8,2,10,2,11,2,12,2,12,3,13,3,13,2,12,10,13,10,13,11,13,10,13,12,14,12,1,3,1,4,1,5,0,5,4,2,4,1});
		levelList.add(new float[]{4,4,1,1,1,2,1,2,2,2,2,1,1,1,0,3,0,2,0,1,0,0,3,0,2,0,3,3,2,3});
		levelList.add(new float[]{4,4,0,3,1,3,0,3,0,2,1,3,1,2,1,1,1,0,1,1,2,1,2,1,2,0,3,2,3,1});
		levelList.add(new float[]{6,6,0,5,2,4,0,4,0,5,2,4,1,4,0,1,0,0,0,2,1,2,3,0,4,0,3,1,4,1,2,2,3,2,4,3,4,2,5,3,5,2,3,5,3,4,4,5,4,4});
		levelList.add(new float[]{7,7,0,6,4,4,1,6,0,6,1,4,1,3,2,2,1,2,3,4,3,3,4,1,4,0,6,2,6,1,6,4,6,3,5,6,6,6,4,4,4,3});
		levelList.add(new float[]{7,7,2,4,5,1,2,5,2,4,5,1,4,1,0,4,1,4,0,5,1,5,2,2,2,3,2,3,3,3,4,4,5,4,5,5,5,4,6,5,6,4});
		levelList.add(new float[]{8,8,3,4,4,4,3,4,3,3,4,4,4,3,2,4,2,3,1,4,1,3,0,4,0,3,5,4,5,3,6,4,6,3,7,3,7,4,5,6,5,5,5,1,5,0,6,1,6,0,1,6,2,6,5,7,4,7,2,1,1,1,3,2,2,2,2,5,1,5,3,6,2,6,0,6,0,5,5,2,6,2,7,2,6,2,5,6,6,6});
		levelList.add(new float[]{9,9,1,3,7,5,0,3,1,3,1,2,2,2,2,2,3,2,3,2,4,2,4,1,5,1,5,2,6,2,8,2,7,2,8,3,8,2,8,4,8,3,7,4,6,4,4,4,5,4,5,3,4,3,3,4,4,4,3,5,2,5,1,5,2,5,0,6,1,6,1,7,2,7,3,8,3,7,4,7,4,6,5,8,5,7,6,7,5,7});
		// 11-20
		levelList.add(new float[]{7,7,1,5,3,5,2,5,3,5,1,5,1,4,4,5,5,5,5,3,5,2,3,2,4,2,3,2,3,1,1,2,1,1,2,3,1,3});
		levelList.add(new float[]{3,3,0,2,2,0,0,2,0,1,1,0,0,0,1,1,1,0,1,1,1,2,2,0,2,1,2,2,2,1});
		
		
		return levelList;
	}
}
