public class Member {

    private String memberId;       // 회원 아이디
    private String name;           // 회원 이름
    private String phone;          // 전화번호
    private String password;       // 비밀번호
    private boolean canBorrow;     // 대출 가능 여부

    // 생성자
    public Member(String memberId, String name, String phone, String password) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.canBorrow = true;
    }

    // 회원 정보 출력
    public void showInfo() {
        System.out.println("회원 ID : " + memberId);
        System.out.println("이름 : " + name);
        System.out.println("전화번호 : " + phone);
        System.out.println("대출 가능 여부 : " + (canBorrow ? "가능" : "불가능"));
    }

    // 로그인 확인
    public boolean login(String password) {
        return this.password.equals(password);
    }

    // 회원 정보 수정
    public void updateInfo(String name, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    // 대출 가능 여부 변경
    public void setCanBorrow(boolean canBorrow) {
        this.canBorrow = canBorrow;
    }

    public boolean isCanBorrow() {
        return canBorrow;
    }

    // Getter
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}