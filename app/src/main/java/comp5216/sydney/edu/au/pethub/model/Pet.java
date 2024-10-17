package comp5216.sydney.edu.au.pethub.model;

import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import comp5216.sydney.edu.au.pethub.R;

public class Pet {
    private String petName;
    private int age;
    private boolean gender; // true: male, false: female
    private String description;
    private String category;
    private String address;
    private double longitude;
    private double latitude;
    private String ownerId;
    private String adopterId;
    private List<String> interestedUserIds;
    private List<String> uriStringList; // List of image URIs in string format
    private List<String> blogTitles;

    private OnSuccessListener<String> successListener;
    private OnFailureListener failureListener;

    // Constructor
    public Pet(String petName, int age, boolean gender, String description, String category,
               String address, double longitude, double latitude, String ownerId, String adopterId,
               List<String> interestedUserIds, List<String> uriStringList, List<String> blogTitles) {
        this.petName = petName;
        this.age = age;
        this.gender = gender;
        this.description = description;
        this.category = category;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.ownerId = ownerId;
        this.adopterId = adopterId;
        this.interestedUserIds = interestedUserIds;
        this.uriStringList = uriStringList;
        this.blogTitles = blogTitles;
    }

    // Getters and Setters
    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(String adopterId) {
        this.adopterId = adopterId;
    }

    public List<String> getInterestedUserIds() {
        return interestedUserIds;
    }

    public void setInterestedUserIds(List<String> interestedUserIds) {
        this.interestedUserIds = interestedUserIds;
    }

    public List<String> getUriStringList() {
        return uriStringList;
    }

    public void setUriStringList(List<String> uriStringList) {
        this.uriStringList = uriStringList;
    }

    public List<String> getBlogTitles() {
        return blogTitles;
    }

    public void setBlogTitles(List<String> blogTitles) {
        this.blogTitles = blogTitles;
    }
}

