package User;

import library.BookMenu;
import library.LibrarySetup;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        UserRepository repository = new UserRepository();
        Login login = new Login(repository);

        Scanner scanner = login.getScanner();

        BookMenu bookMenu = new BookMenu(
                scanner, LibrarySetup.createLibrary()
        );


        while (true) {

            System.out.println("\n======================");
            System.out.println("     도서 관리 시스템");
            System.out.println("======================");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 종료");
            System.out.print("메뉴 선택: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    User loginUser = login.login();

                    if (loginUser != null) {
                        bookMenu.run(
                                loginUser.getId(),
                                loginUser.getName()
                        );

                        System.out.println("로그아웃되었습니다.");
                    }
                    break;

                case "2":
                    login.signUp();
                    break;

                case "3":
                    System.out.println("프로그램을 종료합니다.");
                    return;

                default:
                    System.out.println("잘못된 메뉴입니다.");
            }
        }
    }
}