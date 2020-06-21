package com.example.memesfilter;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AnimationManager {
    private static Animation animation;

    //does a flip animation on the passed ImageButton
    public static void play_flip_animation(ImageButton im, Context context) {
        animation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        im.setAnimation(animation);
    }

    //does a touch animation on the passed ImageButton
    public static void play_touch_animation(ImageButton im, Context context)//does a flip animation on the passed ImageButton
    {
        animation = AnimationUtils.loadAnimation(context, R.anim.touch_effect);
        im.setAnimation(animation);
    }

    //does a left movement animation on the passed ImageButton
    public static void play_left_animation(ImageView im, Context context)//does a flip animation on the passed ImageButton
    {
        animation = AnimationUtils.loadAnimation(context, R.anim.left_animatiom);
        im.setAnimation(animation);
    }

    //does a right movement animation on the passed ImageButton
    public static void play_right_animation(ImageView im, Context context) {
        animation = AnimationUtils.loadAnimation(context, R.anim.right_animation);
        im.setAnimation(animation);
    }
}
