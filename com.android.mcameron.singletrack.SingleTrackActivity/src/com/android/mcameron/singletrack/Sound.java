/**
 * 
 */
package com.android.mcameron.singletrack;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Mark Cameron
 *
 */
public class Sound {

	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	
	private Boolean playSounds;

	/**
	 * 
	 */
	public Sound(Context context) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();

		// Thip sound on allowed line
		soundPoolMap.put(Globals.SOUND_TOUCH_DRAW, soundPool.load(context, R.raw.thip, 1));
		
		// Boop sound on error line
		soundPoolMap.put(Globals.SOUND_TOUCH_UNDRAW, soundPool.load(context, R.raw.thipa, 1));
		
		// Boop sound on error line
		soundPoolMap.put(Globals.SOUND_TOUCH_WRONG, soundPool.load(context, R.raw.bloopa, 1));
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		playSounds = prefs.getBoolean("game_sounds", true);
		
		Log.d("TAG", playSounds.toString());
	}

	public void play(int sound, Context context) {
		if (!playSounds) {
			return;
		}
	    
		/* Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0 */		
	    AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
	    
	    /* Play the sound with the correct volume */
	    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);     
	}
}
