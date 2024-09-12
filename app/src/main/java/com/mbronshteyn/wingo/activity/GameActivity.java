package com.mbronshteyn.wingo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mbronshteyn.wingo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class GameActivity extends AppCompatActivity {

    private Button chipButton25;
    private Button chipButton50;
    private Button chipButton100;
    private ChipBlinkedEvent chipBlinkedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        chipButton25 = (Button) findViewById(R.id.btn_chip25);
        chipButton50 = (Button) findViewById(R.id.btn_chip50);
        chipButton100 = (Button) findViewById(R.id.btn_chip100);
        
        scaleUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onChipStpedBlinking(ChipBlinkedEvent event){
        if(event.chip.equals(chipButton100)){
            chipOnOff(chipButton100,event.delay, event.intrval);
        }
        else if(event.chip.equals(chipButton50)){
            chipOnOff(chipButton50,event.delay, event.intrval);
        }
        else if(event.chip.equals(chipButton25)){
            chipOnOff(chipButton25,event.delay, event.intrval);
        }
    }

    @Subscribe
    public void onChipStpedBlinkingEnds(String event){
        if(event.equals(END_OD_BLINKIG)){
            allChipsOnOff();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chipBlinkedEvent = new ChipBlinkedEvent(chipButton100,200,600);
        EventBus.getDefault().post(chipBlinkedEvent);
    }

    private void chipOnOff(Button chipButton, int onTime, int offTime) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            chipButton.setPressed(true);
        }, onTime);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            chipButton.setPressed(false);
            if(chipBlinkedEvent.chip.equals(chipButton100)) {
                chipBlinkedEvent.chip = chipButton50;
                EventBus.getDefault().post(chipBlinkedEvent);
            }
            else if(chipBlinkedEvent.chip.equals(chipButton50)) {
                chipBlinkedEvent.chip = chipButton25;
                EventBus.getDefault().post(chipBlinkedEvent);
            }
            else if(chipBlinkedEvent.chip.equals(chipButton25) && chipBlinkedEvent.counter <1) {
                chipBlinkedEvent.chip = chipButton100;
                chipBlinkedEvent.counter ++;
                EventBus.getDefault().post(chipBlinkedEvent);
            }
            else{
                EventBus.getDefault().post(END_OD_BLINKIG);
            }
        }, onTime+offTime);

    }

    private void allChipsOnOff(){
        chipButton100.setPressed(true);
        chipButton50.setPressed(true);
        chipButton25.setPressed(true);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            chipButton100.setPressed(false);
            chipButton50.setPressed(false);
            chipButton25.setPressed(false);
        }, 500);
    }

    public void scaleUi() {

        // scale the screen
        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.game, options);
        float bmapHeight = options.outHeight;
        float bmapWidth = options.outWidth;

        float wRatio = width / bmapWidth;
        float hRatio = height / bmapHeight;

        float ratioMultiplier = wRatio;
        // Untested conditional though I expect this might work for landscape
        // mode
        if (hRatio < wRatio) {
            ratioMultiplier = hRatio;
        }

        int newBmapWidth = (int) (bmapWidth * ratioMultiplier);
        int newBmapHeight = (int) (bmapHeight * ratioMultiplier);

        //scale background
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.game);
        ImageView iView = (ImageView) findViewById(R.id.gameBacgroundimageView);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.constrainHeight(iView.getId(), newBmapHeight);
        set.constrainWidth(iView.getId(), newBmapWidth);
        set.applyTo(layout);

        //scale chips  button
        Button buttonChip25 = (Button) findViewById(R.id.btn_chip25);
        ViewGroup.LayoutParams buttonParamsChip25 = buttonChip25.getLayoutParams();
        buttonParamsChip25.height = (int) (newBmapHeight * 0.1381F);;
        buttonParamsChip25.width = (int) (newBmapWidth * 0.0704F);;

        Button buttonChip50 = (Button) findViewById(R.id.btn_chip50);
        ViewGroup.LayoutParams buttonParamsChip50 = buttonChip50.getLayoutParams();
        buttonParamsChip50.height = buttonParamsChip25.height;
        buttonParamsChip50.width = buttonParamsChip25.width;

        Button buttonChip100 = (Button) findViewById(R.id.btn_chip100);
        ViewGroup.LayoutParams buttonParamsChip100 = buttonChip100.getLayoutParams();
        buttonParamsChip100.height = buttonParamsChip25.height;;
        buttonParamsChip100.width = buttonParamsChip25.width;;
    }

    private class ChipBlinkedEvent {
        public Button chip;
        public int intrval;
        public int delay;
        public int counter =0;
        public ChipBlinkedEvent(Button chip, int delay, int intrval) {
            this.chip = chip;
            this.intrval = intrval;
            this.delay = delay;
        }
    }

    private static final String END_OD_BLINKIG = "the end";
}