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
		switch (levelState) {
			case Globals.LEVEL_DISABLED:
			case Globals.LEVEL_UNSET:
				this.setEnabled(false);				
				break;
			default:
				this.setEnabled(true);				
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
