package in.walkwithus.eguide.events;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class ShowToastEvent {
    public final String displayMessage;
    public final boolean isImportant;
    public ShowToastEvent(String displayMessage,boolean isImportant){
        this.displayMessage=displayMessage;
        this.isImportant=isImportant;
    }
}
