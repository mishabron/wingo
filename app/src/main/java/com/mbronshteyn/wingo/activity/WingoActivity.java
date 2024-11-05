package com.mbronshteyn.wingo.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mbronshteyn.wingo.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class WingoActivity extends AppCompatActivity {

    public MediaPlayer mediaPlayer1 = new MediaPlayer();;
    public MediaPlayer mediaPlayer2 = new MediaPlayer();
    protected static SoundPool soundPool;
    public static Map<Integer,Integer> soundMap = new HashMap<>();
    public static Map<Integer,Integer> soundsInPlayMap = new HashMap<>();
    public ImageView progressCounter;
    public AnimationDrawable dotsProgress;
    private boolean playingBackground;
    public static float volume =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        soundPool = new SoundPool.Builder().setMaxStreams(5).build();

        //load all sounds
        int soundId = soundPool.load(this, R.raw.background_loop, 1);
        soundMap.put(R.raw.background_loop,soundId);
        soundId = soundPool.load(this, R.raw.avatari_intro, 1);
        soundMap.put(R.raw.avatari_intro,soundId);
        soundId = soundPool.load(this, R.raw.spin_sequence, 1);
        soundMap.put(R.raw.spin_sequence,soundId);
        soundId = soundPool.load(this, R.raw.select_bet_spanish, 1);
        soundMap.put(R.raw.select_bet_spanish,soundId);
        soundId = soundPool.load(this, R.raw.select_bet_game_click, 1);
        soundMap.put(R.raw.select_bet_game_click,soundId);
        soundId = soundPool.load(this, R.raw.good_luck_spanish, 1);
        soundMap.put(R.raw.good_luck_spanish,soundId);
        soundId = soundPool.load(this, R.raw.wingo_callout, 1);
        soundMap.put(R.raw.wingo_callout,soundId);
        soundId = soundPool.load(this, R.raw.win_count, 1);
        soundMap.put(R.raw.win_count,soundId);
        soundId = soundPool.load(this, R.raw.new_winner_ping, 1);
        soundMap.put(R.raw.new_winner_ping,soundId);

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPplayInBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer1 != null) {
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }
        if(mediaPlayer2 != null) {
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopPplayInBackground();
        moveTaskToBack(true);
        exitApp();
        System.exit(1);
    }

    protected void exitApp(){
        soundPool.release();
        soundPool = null;
    }

    protected void stopPlaySound(int sound) {

        Integer soundId = soundsInPlayMap.get(sound);

        if (soundId != null){
            soundPool.stop(soundId);
        }
    }

    protected void playSound(int sound) {

        Integer soundId = soundMap.get(sound);
        int soundPlaying = soundPool.play(soundId, volume, volume, 0, 0, 1);
        soundsInPlayMap.put(sound,soundPlaying);
    }

    protected void playInBackgroundIfNotPlaying(int sound){
        if(!playingBackground){
            playInBackground(sound);
        }
    }

    protected void playInBackground(int sound){

        final AssetFileDescriptor afd = getResources().openRawResourceFd(sound);
        mediaPlayer1 = MediaPlayer.create(this,sound);
        mediaPlayer2 = MediaPlayer.create(this,sound);
        mediaPlayer1.setVolume(volume,volume);
        mediaPlayer2.setVolume(volume,volume);

        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playingBackground = true;

        mediaPlayer1.start();
        mediaPlayer1.setNextMediaPlayer(mediaPlayer2);

        mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaPlayer2.setNextMediaPlayer(mediaPlayer1);
            }
        });

        mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaPlayer1.setNextMediaPlayer(mediaPlayer2);
            }
        });
    }

    protected void stopPplayInBackground(){

        playingBackground = false;

        if(mediaPlayer1 != null && mediaPlayer1.isPlaying()){
            mediaPlayer1.stop();
        }
        if(mediaPlayer2 != null && mediaPlayer2.isPlaying()){
            mediaPlayer2.stop();
        }
    }

    public void muteAudio(){
        volume  = 0;
        Collection<Integer> soundsInPlay = soundsInPlayMap.values();
        for(Integer sound: soundsInPlay){
            soundPool.stop(sound);
        }
        Collection<Integer> sounds = soundMap.values();
        for(Integer sound: sounds){
            soundPool.setVolume(sound,volume,volume);
        }
        mediaPlayer1.stop();
        mediaPlayer2.stop();
        mediaPlayer1.setVolume(volume,volume);
        mediaPlayer2.setVolume(volume,volume);
    }

    public void unMuteAudio(){
        volume = 1;
        Collection<Integer> sounds = soundMap.values();
        for(Integer sound: sounds) {
            soundPool.setVolume(sound, volume, volume);
        }
        mediaPlayer1.setVolume(volume,volume);
        mediaPlayer2.setVolume(volume,volume);
    }

}
