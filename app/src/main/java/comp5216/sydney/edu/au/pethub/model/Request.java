package comp5216.sydney.edu.au.pethub.model;

public class Request {
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
}
