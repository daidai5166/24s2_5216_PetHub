package comp5216.sydney.edu.au.pethub.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Request implements Parcelable {
    private String userId;
    private String petID;
    private String requestId;
    private String userName;
//    private String petId;
    private String email;
    private String address;
    private String message;
    private int phone;
    private String date;

    public Request(String petID, String userId, String requestId, String userName, String email, String address, String message, int phone, String date) {
        this.petID = petID;
        this.userId = userId;
        this.requestId = requestId;
        this.userName = userName;
        this.email = email;
        this.address = address;
        this.message = message;
        this.phone = phone;
        this.date = date;
    }

    protected Request(Parcel in) {
        petID = in.readString();
        userId = in.readString();
        requestId = in.readString();
        userName = in.readString();
        email = in.readString();
        address = in.readString();
        message = in.readString();
        phone = in.readInt();
        date = in.readString();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(petID);
        parcel.writeString(userId);
        parcel.writeString(requestId);
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(message);
        parcel.writeInt(phone);
        parcel.writeString(date);
    }
}
