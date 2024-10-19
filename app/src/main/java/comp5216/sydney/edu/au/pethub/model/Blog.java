package comp5216.sydney.edu.au.pethub.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Blog implements Parcelable {

    private String blogTitle;
    private String content;
    private String petName;
    private String category;
    private String postTime;
    private String ownerId;
    private String petID;
    private List<String> likedUsers;
    private String blogID; // New field for blogID

    public Blog(String blogID,
                String blogTitle,
                String content,
                String petName,
                String category,
                String postTime,
                String ownerId,
                String petID,
                List<String> likedUsers) {
        this.blogID = blogID;
        this.blogTitle = blogTitle;
        this.content = content;
        this.petName = petName;
        this.category = category;
        this.postTime = postTime;
        this.ownerId = ownerId;
        this.petID = petID;
        this.likedUsers = new ArrayList<>(); // Initializing with empty list
    }

    // Empty constructor for Firestore deserialization
    public Blog() {
    }

    // Constructor for Parcelable
    protected Blog(Parcel in) {
        blogID = in.readString();  // Reading blogID
        blogTitle = in.readString();
        content = in.readString();
        petName = in.readString();
        category = in.readString();
        postTime = in.readString();
        ownerId = in.readString();
        petID = in.readString();
        likedUsers = in.createStringArrayList();
    }

    // Parcelable Creator
    public static final Creator<Blog> CREATOR = new Creator<Blog>() {
        @Override
        public Blog createFromParcel(Parcel in) {
            return new Blog(in);
        }

        @Override
        public Blog[] newArray(int size) {
            return new Blog[size];
        }
    };

    // Getters and Setters
    public String getBlogID() {
        return blogID;
    }

    public void setBlogID(String blogID) {
        this.blogID = blogID;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public List<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(blogID);
        parcel.writeString(blogTitle);
        parcel.writeString(content);
        parcel.writeString(petName);
        parcel.writeString(category);
        parcel.writeString(postTime);
        parcel.writeString(ownerId);
        parcel.writeString(petID);
        parcel.writeStringList(likedUsers);
    }
}
