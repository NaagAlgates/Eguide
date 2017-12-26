package in.walkwithus.eguide.events;

import android.media.MediaPlayer;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class StopPlayingEvent {
    public MediaPlayer mediaPlayer;
    public boolean isStopped;
    public StopPlayingEvent(MediaPlayer mediaPlayer,boolean isStopped){
        this.mediaPlayer=mediaPlayer;
        this.isStopped=isStopped;
    }

    public StopPlayingEvent(boolean isStopped) {
        this.isStopped=isStopped;
    }
}
