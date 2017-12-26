package in.walkwithus.eguide.events;

import android.media.MediaPlayer;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class PlayFileEvent {
    public final String sRawName;
    public final String sFileName;
    public final MediaPlayer mediaPlayer;
    public PlayFileEvent(String sRawName,String sFileName,MediaPlayer mediaPlayer){
        this.sFileName=sFileName;
        this.mediaPlayer=mediaPlayer;
        this.sRawName=sRawName;
    }
}
