package com.android.mcameron.singletrack;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

public class MenuButton extends Button {

	public MenuButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setEnabledFromLevelState(int levelState) {
		if (levelState == -1) {
			this.setEnabled(false);				
		}
	}
	
	public void setBackgroundResourceFromLevelState(int levelState) {
		switch (levelState) {
			case 2:
				this.setBackgroundResource(R.drawable.btn_blue);
				break;
		    default:
		        this.setBackgroundResource(R.drawable.btn_black);
		}
	}
}
