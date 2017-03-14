package com.moonapps.moonmoon.tabatatimer.gestures;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.moonapps.moonmoon.tabatatimer.timer_configuration.SwipeGestures;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TabataConfigurationActivity;

/**
 * Created by Moon Moon on 2/25/2017.
 */

public class TabataGestureListener extends GestureDetector.SimpleOnGestureListener {

    TabataConfigurationActivity triggerActivity;

    /*
    gesture identification configuration
     */
    private int SWIPE_MIN_DISTANCE = 80;
    private int SWIPE_MIN_VELOCITY = 80;

    public TabataGestureListener(TabataConfigurationActivity triggerActivity) {
        super();
        this.triggerActivity = triggerActivity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        float horizontalDistance = Math.abs(e1.getX() - e2.getX());
        float verticalDistance = Math.abs(e1.getY() - e2.getY());
        /*
        check gesture against threshold - is min distance and speed respected
         */
        if (isValidSwipeGesture(horizontalDistance, verticalDistance, velocityX, velocityY)) {
        /*
        detect gesture verticality
         */
            if (isHorizontalSwipe(horizontalDistance, verticalDistance)) {
                if (e1.getX() < e2.getX()) {
                    triggerActivity.onSwipe(SwipeGestures.RIGHT);
                } else {
                    triggerActivity.onSwipe(SwipeGestures.LEFT);
                }
            } else {
                if (e1.getY() < e2.getY()) {
                    triggerActivity.onSwipe(SwipeGestures.DOWN);
                } else {
                    triggerActivity.onSwipe(SwipeGestures.UP);
                }
            }
        /*
        detect swipe direction
         */
        }
        return false;
    }

    /**
     * check which distance is greater to estimate user intentions
     *
     * @param horizontalDistance
     * @param verticalDistance
     * @return
     */
    private boolean isHorizontalSwipe(float horizontalDistance, float verticalDistance) {
        return (horizontalDistance > verticalDistance);
    }

    /**
     * check if the gesture qualifies as a swipe action
     *
     * @param horizontalDistance
     * @param verticalDistance
     * @param velocityX
     * @param velocityY
     * @return
     */
    private boolean isValidSwipeGesture(float horizontalDistance, float verticalDistance, float velocityX, float velocityY) {
        /*
        check distance criteria
         */

        if (horizontalDistance < SWIPE_MIN_DISTANCE && verticalDistance < SWIPE_MIN_DISTANCE) {
            return false;
        }

        /*
        check speed criteria
         */
        if (velocityX < SWIPE_MIN_VELOCITY && velocityY < SWIPE_MIN_VELOCITY) {
            return false;
        }
        return true;
    }
}
