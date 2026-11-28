package User;

public abstract class User {
    private String id;
    private String password;
    private String name;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    // 은닉된 데이터에 접근하기 위한 Getter
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    // 추상 메소드
    public abstract void showUserType();
}