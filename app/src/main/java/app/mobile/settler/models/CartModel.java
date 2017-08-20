package app.mobile.settler.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Madhu on 20/08/17.
 */

public class CartModel {
    @SerializedName("ActiveHours")
    @Expose
    private String activeHours;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("OTP")
    @Expose
    private String oTP;
    @SerializedName("OfferDesc")
    @Expose
    private String offerDesc;
    @SerializedName("OfferId")
    @Expose
    private String offerId;
    @SerializedName("OfferName")
    @Expose
    private String offerName;
    @SerializedName("OfferStartDateTime")
    @Expose
    private String offerStartDateTime;
    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("SettlerId")
    @Expose
    private String settlerId;
    @SerializedName("StorName")
    @Expose
    private String storName;
    @SerializedName("StoreLatitude")
    @Expose
    private String storeLatitude;
    @SerializedName("StoreLongitude")
    @Expose
    private String storeLongitude;

    public String getActiveHours() {
        return activeHours;
    }

    public void setActiveHours(String activeHours) {
        this.activeHours = activeHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOTP() {
        return oTP;
    }

    public void setOTP(String oTP) {
        this.oTP = oTP;
    }

    public String getOfferDesc() {
        return offerDesc;
    }

    public void setOfferDesc(String offerDesc) {
        this.offerDesc = offerDesc;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferStartDateTime() {
        return offerStartDateTime;
    }

    public void setOfferStartDateTime(String offerStartDateTime) {
        this.offerStartDateTime = offerStartDateTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSettlerId() {
        return settlerId;
    }

    public void setSettlerId(String settlerId) {
        this.settlerId = settlerId;
    }

    public String getStorName() {
        return storName;
    }

    public void setStorName(String storName) {
        this.storName = storName;
    }

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

}
