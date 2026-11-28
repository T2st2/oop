package User;

import java.util.Scanner;

public class Login {

    private UserRepository repository;
    private Scanner scanner;

    public Login(UserRepository repository) {
        this.repository = repository;
        this.scanner = new Scanner(System.in);
    }

    // 회원가입
    public void signUp() {

        System.out.println("\n===== 회원가입 =====");

        System.out.print("아이디: ");
        String id = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        System.out.print("이름: ");
        String name = scanner.nextLine();

        User member = new Member(id, password, name);

        if (repository.addUser(member)) {
            System.out.println("회원가입이 완료되었습니다.");
        } else {
            System.out.println("이미 존재하는 아이디입니다.");
        }
    }

    // 로그인
    public User login() {

        System.out.println("\n===== 로그인 =====");

        System.out.print("아이디: ");
        String id = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        User user = repository.findUser(id, password);

        if (user != null) {
            System.out.println("로그인 성공!");
            return user;
        }

        System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        return null;
    }

    public Scanner getScanner() {
        return scanner;
    }
}