package com.wawa.arm.common;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.speech.tts.TextToSpeech;

import com.wawa.arm.R;
import com.wawa.arm.utile.log.LogUtil;

public class SoundPoolPlay {
	private boolean isOpen;
	// ������Ч
    private SoundPool mSoundPool;
    private HashMap<String, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    
    private int volMax,volLow,currentVol;
    private int alarmId,nomerId;
    
    public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public SoundPoolPlay(Context context,boolean isopen){
    	this.isOpen = isopen;
    	/*mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
    	mSoundPoolMap = new HashMap<String, Integer>();
    	mSoundPoolMap.put(CommonConsts.SOUND_NOMOR, mSoundPool.load(context, R.raw.nomer_du_once, 1));
    	mSoundPoolMap.put(CommonConsts.SOUND_ALARM, mSoundPool.load(context, R.raw.alarm_du_once, 1));
    	
    	//�Ȼ�ȡAudioManagerʵ����
    	mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	//��ʾ��������
    	volMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING );//ȡ�õ�ǰ�ֻ������������ֵΪ7����СֵΪ0����Ϊ0ʱ���ֻ��Զ���ģʽ����Ϊ����ģʽ����
    	currentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_RING ); //��ǰ�ֻ�����ֵ
    	LogUtil.i("---vlo max--"+volMax);*/
    	
    	/*volMax = Integer.valueOf(OMApplication.getInstance().getVal(CommonConsts.APP_VOL_VAL, "4"));
    	if("1".equals(OMApplication.getInstance().getVal(CommonConsts.APP_VOL_OPEN_STATU, "0")))
    		changeSystemAlarmVol(volMax);*/
    	/*
    	 * <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
    	 * //��������ģʽ  
    	mAudioManager.setMode(AudioManager.STREAM_MUSIC);  
    	//�ر���˷�  
    	mAudioManager.setMicrophoneMute(false);  
    	// ��������  
    	mAudioManager.setSpeakerphoneOn(true);*/ 
    }
	
    public void changeSystemAlarmVol(int tempVolume){
    	mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,tempVolume, 0);//tempVolume:�������� 
    	/*//��һ����������������������������ϵͳĬ��������������
    	//��������������ϵͳ��������
    	mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,
    	//��������������ϵͳ��������
    	mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
    	                            AudioManager.FX_FOCUS_NAVIGATION_UP);*/
    }
    private int getCurrentVol(){
    	return mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
    }
    private int lastLevl = -1;
    private long lastTime = -1;
    private Timer timer;
	public void play(int level){
		if(!isOpen || level == -1){
			lastLevl = -1;
			if(timer != null){
				timer.cancel();
				timer = null;
			}
			return;
		}
		lastLevl = level;
		if(timer == null){
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(myTTS != null && lastLevl != -1 && isOpen)
						myTTS.speak(lastLevl+"", TextToSpeech.QUEUE_FLUSH, null);
				}
			}, 500, 2000);
		}
		/*if("0".equals(OMApplication.getInstance().getVal(CommonConsts.APP_VOL_OPEN_STATU, "0"))){
			changeSystemAlarmVol(currentVol);
			 return;
		}else{
			volMax = Integer.valueOf(OMApplication.getInstance().getVal(CommonConsts.APP_VOL_VAL, "4"));
			changeSystemAlarmVol(volMax);
		}*/
		//mSoundPool.play(mSoundPoolMap.get(CommonConsts.SOUND_ALARM), volMax,volMax, 0, 2, 2);//leftVolume��rightVolume��ʾ����������priority��ʾ���ȼ�,loop��ʾѭ������,rate��ʾ���ʣ��磺�������0.5���Ϊ2��1���������ٶ�
		//float vol = 0.00f;
		if(lastLevl == level || (System.currentTimeMillis() - lastTime) < 1500) return;
		lastLevl = level;
		lastTime = System.currentTimeMillis();
		
		float rate = 1;
		int loop = 100;
		String type = CommonConsts.SOUND_NOMOR;
		if(level == 0){
			stopAll();
			return;
		}else if(level == 91){//���
			//vol = volMax;
			type = CommonConsts.SOUND_ALARM;
			rate = 1.3f;
		}else if(0 < level && level < 91){
			//vol = (float)((volMax/90f)*level);
			//LogUtil.e("---------------"+vol);
			rate= (float) (0.5+(float)((1.5/90)*level));
		}
		int vol = getCurrentVol();
		if(type.equals(CommonConsts.SOUND_NOMOR)){
			alarmId = mSoundPool.play(mSoundPoolMap.get(type), vol,vol, 0, loop, rate);
			mSoundPool.stop(nomerId);
		}else{
			nomerId = mSoundPool.play(mSoundPoolMap.get(type), vol,vol, 0, loop, rate);
			mSoundPool.stop(alarmId);
		}
	}
	public void stopAll(){
		lastLevl = -1;
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		if(mSoundPool != null){
			mSoundPool.stop(alarmId);
			mSoundPool.stop(nomerId);
		}
	}
	public void destory(){
		changeSystemAlarmVol(currentVol);
		mSoundPool.unload(mSoundPoolMap.get(CommonConsts.SOUND_NOMOR));
		mSoundPool.unload(mSoundPoolMap.get(CommonConsts.SOUND_ALARM));
		mSoundPoolMap.clear();
		mSoundPoolMap = null;
        mSoundPool.release();
        mSoundPool = null;
	}

	private TextToSpeech myTTS;
	public void setTTS(TextToSpeech myTTS) {
		this.myTTS = myTTS;
	}

}
