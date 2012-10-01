package com.android.mcameron.singletrack;

import java.util.ArrayList;

public class Levels {
	ArrayList <float[]>levelList = new ArrayList<float[]>();
	
	Levels() {
		levelList.add(null); //Level 0 - Doesn't exist - saves on silly - 1 operations
		levelList.add(new float[]{9,9,1,3,7,5,0,3,1,3,1,2,2,2,2,2,3,2,3,2,4,2,4,1,5,1,5,2,6,2,8,2,7,2,8,3,8,2,8,4,8,3,7,4,6,4,4,4,5,4,5,3,4,3,3,4,4,4,3,5,2,5,1,5,2,5,0,6,1,6,1,7,2,7,3,8,3,7,4,7,4,6,5,8,5,7,6,7,5,7});
		levelList.add(new float[]{6,6,0,5,2,4,0,4,0,5,2,4,1,4,0,1,0,0,0,2,1,2,3,0,4,0,3,1,4,1,2,2,3,2,4,3,4,2,5,3,5,2,3,5,3,4,4,5,4,4});
		levelList.add(new float[]{7,7,0,6,4,4,1,6,0,6,1,4,1,3,2,2,1,2,3,4,3,3,4,1,4,0,6,2,6,1,6,4,6,3,5,6,6,6,4,4,4,3});
	}
	
	public float[] getLevel(int index) {
		return levelList.get(index);
	}
}
