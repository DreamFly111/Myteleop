package com.github.rosjava.android_apps.teleop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;

import org.ros.android.view.RosImageView;
import org.ros.message.MessageListener;

import nav_msgs.Odometry;

/**
 * Created by Dream on 2016/3/21.
 */
public class MyRosImageView extends RosImageView implements Animation.AnimationListener,MessageListener<Odometry> {

    private String topicName;
    public MyRosImageView(Context context) {
        super(context);
        this.initRosImageView(context);
        this.topicName = "on_off";
    }

    private void initRosImageView(Context context) {

    }

    public MyRosImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initRosImageView(context);
        this.topicName = "on_off";
    }

    public MyRosImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.topicName = "on_off";
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        super.onAnimationEnd();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onNewMessage(Odometry odometry) {

    }
}
