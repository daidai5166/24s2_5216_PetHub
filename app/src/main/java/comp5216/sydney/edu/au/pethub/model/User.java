package comp5216.sydney.edu.au.pethub.model;

public class User {
    private String firebaseId;
    private String username;
    private String email;
    private String gender;
    private int phone;
    private String address;
    private String avatarPath;

    // 无参构造方法
    public User() {
    }

    // 带参数的构造方法
    public User(String firebaseId, String username, String email, String gender, int phone, String address, String avatarPath) {
        this.firebaseId = firebaseId;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.avatarPath = avatarPath;
    }

    // Getter 和 Setter 方法
    public String getFirebaseId() {
        return firebaseId;
    }
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    // 重写 toString 方法，方便调试和打印
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                '}';
    }
}
