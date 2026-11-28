package User;

public class Member extends User {

    public Member(String id, String password, String name) {
        super(id, password, name);
    }

    @Override
    public void showUserType() {
        System.out.println("일반 회원입니다.");
    }
}