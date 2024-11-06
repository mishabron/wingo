package com.mbronshteyn.wingo.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityOptionsCompat;

import com.mbronshteyn.wingo.R;

public class GameSelectorActivity extends WingoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selector);

        Button button6 = (Button) findViewById(R.id.button1_6);

        GameSelectorActivity activity = this;
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaySound(R.raw.select_bet_spanish);
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out).toBundle();
                startActivity(intent, bundle);
            }
        });

        scaleUi();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            playSound(R.raw.select_bet_spanish);
        }, 1000);
    }

    public void scaleUi() {

        // scale the screen
        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.game_selector, options);
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
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.gameSelector);
        ImageView iView = (ImageView) findViewById(R.id.gameSelectorBacgroundimageView);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.constrainHeight(iView.getId(), newBmapHeight);
        set.constrainWidth(iView.getId(), newBmapWidth);
        set.applyTo(layout);

        //scale button
        Button button_1_3 = (Button) findViewById(R.id.button1_3);
        ViewGroup.LayoutParams buttonParams1_3 = button_1_3.getLayoutParams();
        buttonParams1_3.height = (int) (newBmapHeight * 0.2192F);;
        buttonParams1_3.width = (int) (newBmapWidth * 0.2312F);;

        Button button_1_6 = (Button) findViewById(R.id.button1_6);
        ViewGroup.LayoutParams buttonParams1_6 = button_1_6.getLayoutParams();
        buttonParams1_6.height = buttonParams1_3.height;;
        buttonParams1_6.width =  buttonParams1_3.width;

        Button button_1_1 = (Button) findViewById(R.id.button1_1);
        ViewGroup.LayoutParams buttonParams1_1 = button_1_1.getLayoutParams();
        buttonParams1_1.height = buttonParams1_3.height;;
        buttonParams1_1.width =  buttonParams1_3.width;

        Button button_1_12 = (Button) findViewById(R.id.button1_12);
        ViewGroup.LayoutParams buttonParams1_12 = button_1_12.getLayoutParams();
        buttonParams1_12.height = buttonParams1_3.height;;
        buttonParams1_12.width =  buttonParams1_3.width;

        Button button_1_9 = (Button) findViewById(R.id.button1_9);
        ViewGroup.LayoutParams buttonParams1_9 = button_1_9.getLayoutParams();
        buttonParams1_9.height = buttonParams1_3.height;;
        buttonParams1_9.width =  buttonParams1_3.width;

        Button button_1_15 = (Button) findViewById(R.id.button1_15);
        ViewGroup.LayoutParams buttonParams1_15 = button_1_15.getLayoutParams();
        buttonParams1_15.height = buttonParams1_3.height;;
        buttonParams1_15.width =  buttonParams1_3.width;
    }
}