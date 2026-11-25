package library;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in, StandardCharsets.UTF_8)) {
            BookMenu menu = new BookMenu(input, LibrarySetup.createLibrary());
            // TODO 회원 기능 연동 시 로그인 성공 후 menu.run(memberId, memberName)을 호출하세요.
            menu.runWithTemporaryMember();
        } catch (NoSuchElementException e) {
            System.out.println("\n입력이 종료되었습니다.");
        }
    }
}
