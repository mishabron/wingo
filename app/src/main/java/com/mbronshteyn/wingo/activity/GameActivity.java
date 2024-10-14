package com.mbronshteyn.wingo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.mbronshteyn.wingo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity {

    private Button chipButton25;
    private Button chipButton50;
    private Button chipButton100;
    private ChipBlinkedEvent chipBlinkedEvent;
    private Button spinButton;
    private int previous =6;
    private int randomNum = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        chipButton25 = (Button) findViewById(R.id.btn_chip25);
        chipButton50 = (Button) findViewById(R.id.btn_chip50);
        chipButton100 = (Button) findViewById(R.id.btn_chip100);
        spinButton = (Button) findViewById(R.id.spinButton);
        spinButton.setEnabled(false);

        ImageView numbers = (ImageView) findViewById(R.id.numbers);
        ImageView winner = (ImageView) findViewById(R.id.winnerBanner);
        AnimationDrawable winnerAnimation = (AnimationDrawable) winner.getBackground();
        spinButton.setOnClickListener(new View.OnClickListener() {
            Button buttonHome = (Button) findViewById(R.id.homeButton);
            @Override
            public void onClick(View v) {

                numbers.setBackground(getResources().getDrawable(R.drawable.numbers_animation,null));
                AnimationDrawable numbersAnimation = (AnimationDrawable) numbers.getBackground();
                Animation aniRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ring_animation);
                ImageView mainCircle = (ImageView) findViewById(R.id.mainCircle);
                Animation mainCircleRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.maincircle_animation);
                ImageView winBackground = (ImageView) findViewById(R.id.winBackground);
                winBackground.setVisibility(View.INVISIBLE);
                ImageView winCircle = (ImageView) findViewById(R.id.winCircle);
                winCircle.setVisibility(View.INVISIBLE);
                aniRotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        buttonHome.setEnabled(false);
                        spinButton.setEnabled(false);
                        chipButton25.setClickable(false);
                        chipButton100.setClickable(false);
                        chipButton50.setClickable(false);
                        numbersAnimation.start();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        numbersAnimation.stop();
                        while (randomNum == previous) {
                            randomNum = ThreadLocalRandom.current().nextInt(0, 5 + 1);
                        }
                        previous = randomNum;
                        numbersAnimation.selectDrawable(randomNum);
                        buttonHome.setEnabled(true);
                        ImageView spingring = (ImageView) findViewById(R.id.spingring);
                        spingring.setVisibility(View.INVISIBLE);
                        chipButton25.setClickable(true);
                        chipButton100.setClickable(true);
                        chipButton50.setClickable(true);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            mainCircleRotate.setDuration(15000);
                            mainCircle.startAnimation(mainCircleRotate);
                        }, 1000);
                        if(randomNum == 5){
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                winBackground.setVisibility(View.VISIBLE);
                                winCircle.setVisibility(View.VISIBLE);
                                winCircle.startAnimation(mainCircleRotate);
                                spinButton.setEnabled(true);
                                winner.setVisibility(View.VISIBLE);
                                winnerAnimation.start();
                            }, 1);
                        }
                        else{
                            spinButton.setEnabled(true);
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                ImageView spingring = (ImageView) findViewById(R.id.spingring);
                spingring.setVisibility(View.VISIBLE);
                spingring.startAnimation(aniRotate);

                winCircle.clearAnimation();
                mainCircleRotate.setDuration(2000);
                mainCircle.startAnimation(mainCircleRotate);
                winnerAnimation.stop();
                winner.setVisibility(View.INVISIBLE);
            }
        });

        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameSelectorActivity.class);
                intent.putExtras(new Bundle());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(GameActivity.this);
                startActivity(intent, options.toBundle());
            }
        });

        scaleUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        chipButton25.setClickable(false);
        chipButton100.setClickable(false);
        chipButton50.setClickable(false);
        EventBus.getDefault().register(this);

        Animation mainCircleRotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.maincircle_animation);
        ImageView mainCircle = (ImageView) findViewById(R.id.mainCircle);
        mainCircle.startAnimation(mainCircleRotate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageView mainCircle = (ImageView) findViewById(R.id.mainCircle);
        mainCircle.clearAnimation();
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

    @Subscribe
    public void onChipStpedAnimationEnds(String event){
        if(event.equals(END_OD_ANIMATION)){
            RadioGroup chips = (RadioGroup) findViewById(R.id.radioGroup1);
            chipButton25.setClickable(true);
            chipButton100.setClickable(true);
            chipButton50.setClickable(true);
            chips.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    spinButton.setEnabled(true);
                    spinButton.setPressed(false);
                    if (checkedId == R.id.btn_chip100){
                        ImageView win1000000 = (ImageView) findViewById(R.id.win1000000);
                        win1000000.setVisibility(View.VISIBLE);
                        ImageView win500000 = (ImageView) findViewById(R.id.win500000);
                        win500000.setVisibility(View.INVISIBLE);
                        ImageView win250000 = (ImageView) findViewById(R.id.win250000);
                        win250000.setVisibility(View.INVISIBLE);
                    }
                    else if(checkedId == R.id.btn_chip50){
                        ImageView win1000000 = (ImageView) findViewById(R.id.win1000000);
                        win1000000.setVisibility(View.INVISIBLE);
                        ImageView win500000 = (ImageView) findViewById(R.id.win500000);
                        win500000.setVisibility(View.VISIBLE);
                        ImageView win250000 = (ImageView) findViewById(R.id.win250000);
                        win250000.setVisibility(View.INVISIBLE);
                    }
                    else if(checkedId == R.id.btn_chip25){
                        ImageView win1000000 = (ImageView) findViewById(R.id.win1000000);
                        win1000000.setVisibility(View.INVISIBLE);
                        ImageView win500000 = (ImageView) findViewById(R.id.win500000);
                        win500000.setVisibility(View.INVISIBLE);
                        ImageView win250000 = (ImageView) findViewById(R.id.win250000);
                        win250000.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RadioGroup chips = (RadioGroup) findViewById(R.id.radioGroup1);
        chips.clearCheck();
        chipBlinkedEvent = new ChipBlinkedEvent(chipButton100,100,400);
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
            EventBus.getDefault().post(END_OD_ANIMATION);
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

        ImageView win1000000 = (ImageView) findViewById(R.id.win1000000);
        ViewGroup.LayoutParams win1000000Params = win1000000.getLayoutParams();
        win1000000Params.height = (int) (newBmapHeight * 0.0299F);
        win1000000Params.width = (int) (newBmapWidth * 0.1906F);;

        ImageView win500000 = (ImageView) findViewById(R.id.win500000);
        ViewGroup.LayoutParams win500000Params = win500000.getLayoutParams();
        win500000Params.height = win1000000Params.height;
        win500000Params.width = win1000000Params.width;;

        ImageView win250000 = (ImageView) findViewById(R.id.win250000);
        ViewGroup.LayoutParams win250000Params = win250000.getLayoutParams();
        win250000Params.height = win1000000Params.height;
        win250000Params.width = win1000000Params.width;;

        //scale spin  button
        Button spinButton = (Button) findViewById(R.id.spinButton);
        int buttonHeight = (int) (newBmapHeight * 0.2407F);
        int buttonWidth = (int) (newBmapWidth * 0.1307F);
        ViewGroup.LayoutParams buttonSpinParams = spinButton.getLayoutParams();
        buttonSpinParams.height = buttonHeight;
        buttonSpinParams.width = buttonWidth;

        ImageView spingring = (ImageView) findViewById(R.id.spingring);
        ViewGroup.LayoutParams spingringarams = spingring.getLayoutParams();
        spingringarams.height = buttonSpinParams.height;
        spingringarams.width = buttonSpinParams.width;

        Button buttonHome = (Button) findViewById(R.id.homeButton);
        ViewGroup.LayoutParams buttonParmHome = buttonHome.getLayoutParams();
        buttonParmHome.height = (int) (newBmapHeight * 0.09259F);
        buttonParmHome.width = (int) (newBmapWidth * 0.04024F);

        ImageView numbers = (ImageView) findViewById(R.id.numbers);
        ViewGroup.LayoutParams numbersParams = numbers.getLayoutParams();
        numbersParams.height = (int) (newBmapHeight * 0.1666F);
        numbersParams.width = (int) (newBmapWidth * 0.0905F);

        ImageView mainCircle = (ImageView) findViewById(R.id.mainCircle);
        ViewGroup.LayoutParams mainCircleParams = mainCircle.getLayoutParams();
        mainCircleParams.height = (int) (newBmapHeight * 0.5410F);
        mainCircleParams.width = (int) (newBmapWidth * 0.2937F);


        ImageView winCircle = (ImageView) findViewById(R.id.winCircle);
        ViewGroup.LayoutParams winCircleParams = winCircle.getLayoutParams();
        winCircleParams.height = (int) (newBmapHeight * 0.5410F);
        winCircleParams.width = (int) (newBmapWidth * 0.2937F);

        ImageView winBackground = (ImageView) findViewById(R.id.winBackground);
        ViewGroup.LayoutParams winBackgroundParams = winBackground.getLayoutParams();
        winBackgroundParams.height = (int) (newBmapHeight * 0.8370F);
        winBackgroundParams.width = (int) (newBmapWidth * 0.4396F);
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

    private static final String END_OD_BLINKIG = "the end of bliking";

    private static final String END_OD_ANIMATION = "the end of animation";
}