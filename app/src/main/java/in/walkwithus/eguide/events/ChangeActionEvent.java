package in.walkwithus.eguide.events;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */

public class ChangeActionEvent {
    public final boolean isSearching;
    public final boolean isStopped;
    public ChangeActionEvent(boolean isSearching,boolean isStopped){
        this.isSearching=isSearching;
        this.isStopped=isStopped;
    }
}
