package in.walkwithus.eguide.events;

import android.media.MediaPlayer;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class PausePlayingEvent {
    public MediaPlayer mediaPlayer;
    public boolean isPaused;
    public PausePlayingEvent(MediaPlayer mediaPlayer,boolean isPaused){
        this.mediaPlayer=mediaPlayer;
        this.isPaused=isPaused;
    }
    public PausePlayingEvent(boolean isPaused){
        this.isPaused=isPaused;
    }
}
