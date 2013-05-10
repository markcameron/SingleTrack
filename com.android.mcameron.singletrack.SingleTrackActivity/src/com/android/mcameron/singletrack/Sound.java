/**
 * 
 */
package com.android.mcameron.singletrack;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author Mark Cameron
 *
 */
public class Sound {

	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;

	/**
	 * 
	 */
	public Sound(Context context) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(Globals.SOUND_TOUCH_DRAW, soundPool.load(context, R.raw.thip, 1));
	}

	public void play(int sound, Context context) {
	    /* Updated: The next 4 lines calculate the current volume in a scale of 0.0 to 1.0 */
	    AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
	    
//	    Log.d("TAG", "Should be playing sound!!! :P | "+ Float.toString(volume) +" "+ Float.toString(streamVolumeCurrent) +" "+ Float.toString(streamVolumeMax));
	    
	    /* Play the sound with the correct volume */
	    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);     
	}
}
