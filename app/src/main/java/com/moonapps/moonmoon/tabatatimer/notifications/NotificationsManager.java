package com.moonapps.moonmoon.tabatatimer.notifications;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.moonapps.moonmoon.tabatatimer.R;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by Moon Moon on 2/26/2017.
 */

public class NotificationsManager {

    /******************************************************
     * CLASS CONSTANTS SECTION
     *****************************************************/
    /*
    MAX VOLUME
     */
    private static final float MAX_NOTIFICATION_VOLUME = 1;
    /*
    load only 2 notifications - the bell and the tick
    todo: find other notifications to mark pre-playLongSound, round and pause
     */
    private static final int NO_OF_AVAILABLE_NOTIFICATIONS = 2;
    /*
    default duration for the notification sound in seconds
     */
    private static final char NOTIFICATION_2S_DURATION = 2; /*seconds*/

    /******************************************************
     * CLASS FIELDS SECTION
     *****************************************************/
    /*
    caller context
     */
    private static Context context;
    /*
    the sounds container
     */
    private static SoundPool soundPool;
    /*
    flag signaling that the sound is loaded in the container and ready to use
     */
    private static boolean soundLoaded;
    /*
    ID used to identify the sound in the container
     */
    private static int soundIntervalId;
    private static int soundTickId;

    /*
    timer for the sound
     */
    private static char notificationCounter;

    /*
    sound time duration
     */
    private static char notificationDuration;
    /*
    flag used to signal notification has finished playing
     */
    private static boolean longSoundIsPlayingFlag;


    /****************************************************
     * CLASS PUBLIC METHODS
     ****************************************************/

    public static void create(Context parent) {

        context = parent;
        /*
        reset Timer and Duration
         */
        notificationDuration = NOTIFICATION_2S_DURATION;
        notificationCounter = notificationDuration;
        /*
        set the sound loaded flag to false
         */
        longSoundIsPlayingFlag = false;
        soundLoaded = false;
        /*
        create and pre-load sounds in the soundPool - to avoid load delays at runtime
         */
        soundPool = new SoundPool(NO_OF_AVAILABLE_NOTIFICATIONS, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundLoaded = true;
            }
        });
        /*
        todo: load from assets
         */
        soundIntervalId = soundPool.load(context, R.raw.round_start, 1);
        soundTickId = soundPool.load(context, R.raw.tick_round, 1);
    }

    /**
     * Bell sound for use in between different set intervals
     */
    public static void playLongSound(float volume) {
        if (!isNotificationStarted()) {
            playNotification(soundIntervalId, volume);
        }
        int counter = runCounter();
        /*
        if counter has expired reset the longSoundPlaying flag
         */
        if (counter == 0) {
            resetLongSoundIsPlaying();
        }

    }

    /***
     * tick sound to count seconds
     */
    public static void playShortSound(float volume) {
        playNotification(soundTickId, volume);
    }

    public static float getMaxNotificationVolume() {
        return MAX_NOTIFICATION_VOLUME;
    }

    /**
     * return the long sound is playing flag
     *
     * @return
     */
    public static boolean isLongSoundPlaying() {
        return longSoundIsPlayingFlag;
    }

    /****************************************************
     *  CLASS PRIVATE METHODS
     ****************************************************/

    /**
     * play the sound indicated by the ID
     *
     * @param soundId
     */
    private static void playNotification(int soundId, float volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (soundLoaded) {
            soundPool.play(soundId, volume, volume, 1, 0, 0.99f);
        }
    }

    /**
     * run the associated counter
     *
     * @return
     */
    private static int runCounter() {
        /*
        increment if the notification duration is not reached
         */
        if (notificationCounter > 0) {
            notificationCounter--;
        }
        return notificationCounter;
    }

    private static void resetLongSoundIsPlaying() {
        longSoundIsPlayingFlag = false;
    }

    private static boolean isNotificationStarted() {
        return longSoundIsPlayingFlag;
    }


}
