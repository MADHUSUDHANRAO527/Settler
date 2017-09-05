package app.mobile.settler.events;

/**
 * Created by Madhu on 30/07/17.
 */

public class AddToCartEvent {
    public final boolean success;
    public final int errorCode;


    public AddToCartEvent(boolean success, int errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }
}
