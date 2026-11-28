package library;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class BookMenu {
    private final Scanner input;
    private final LibraryService library;
    private final LibraryClock clock = new LibraryClock();

    public BookMenu(Scanner input, LibraryService library) {
        this.input = input;
        this.library = library;
    }

    public void run(String memberId, String memberName) {
        System.out.println(memberName + "님, 반갑습니다.");
        if (library.isCatalogEmpty()) {
            System.out.println("등록된 도서가 없습니다. LibrarySetup에 도서를 추가하세요.");
        }

        while (true) {
            System.out.println("\n===== 도서 관리 메뉴 =====");
            System.out.println("현재 날짜: " + clock.today() + " (시연용)");
            System.out.println("1. 도서 검색");
            System.out.println("2. 대출");
            System.out.println("3. 반납");
            System.out.println("4. 연체료 확인");
            System.out.println("5. 대출 가능 도서 목록");
            System.out.println("6. 시간 건너뛰기");
            System.out.println("0. 로그아웃");
            switch (readChoice("메뉴 번호: ", 6)) {
                case 1 -> searchBooks();
                case 2 -> borrowBook(memberId);
                case 3 -> returnBook(memberId);
                case 4 -> estimateFee(memberId);
                case 5 -> showAvailableBooks();
                case 6 -> skipTime();
                case 0 -> {
                    return;
                }
            }
        }
    }

    private void searchBooks() {
        System.out.println("1. 제목  2. 카테고리  3. 출판사  0. 뒤로");
        int choice = readChoice("검색 방식: ", 3);
        if (choice == 0) {
            return;
        }
        List<BookSearch> methods = List.of(new TitleSearch(), new CategorySearch(), new PublisherSearch());
        BookSearch search = methods.get(choice - 1);
        List<Book> matches = library.search(search, readText("검색어: "));
        if (matches.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        }
        printBooks(matches);
    }

    private void borrowBook(String memberId) {
        if (!showLoanPeriodStatus()) {
            return;
        }
        while (true) {
            Book book = pickBook();
            if (book == null) {
                return;
            }
            Loan loan = library.borrow(memberId, book, clock.today());
            if (loan == null) {
                System.out.println("이미 대출 중인 도서입니다. 다른 제목을 입력하세요.");
                continue;
            }
            System.out.printf("'%s' 대출 완료. 반납 예정일은 %s입니다.%n", book.title(), loan.dueOn());
            return;
        }
    }

    private void returnBook(String memberId) {
        List<Loan> loans = library.activeLoans(memberId);
        if (loans.isEmpty()) {
            System.out.println("반납할 도서가 없습니다.");
            return;
        }
        for (int index = 0; index < loans.size(); index++) {
            Loan loan = loans.get(index);
            System.out.printf("%d. %s (반납 예정일 %s)%n", index + 1, loan.book().description(), loan.dueOn());
        }
        int choice = readChoice("반납할 번호 (0. 뒤로): ", loans.size());
        if (choice != 0) {
            Loan loan = loans.get(choice - 1);
            LocalDate returnedOn = clock.today();
            Optional<BigDecimal> fee = library.returnBook(memberId, loan, returnedOn);
            System.out.println("'" + loan.book().title() + "' 반납 완료.");
            printFee(fee);
        }
    }

    private void estimateFee(String memberId) {
        List<Loan> loans = library.activeLoans(memberId);
        System.out.println("\n===== 나의 연체료 현황 =====");
        if (loans.isEmpty()) {
            System.out.println("현재 대출 중인 도서가 없습니다.");
            return;
        }
        LocalDate today = clock.today();
        BigDecimal totalFee = BigDecimal.ZERO;
        boolean allFeesKnown = true;
        System.out.println("현재 날짜: " + today);
        System.out.println("대출 중인 도서: " + loans.size() + "권");
        for (Loan loan : loans) {
            long lateDays = ChronoUnit.DAYS.between(loan.dueOn(), today);
            Optional<BigDecimal> fee = library.calculateLateFee(loan.book(), loan.dueOn(), today);
            System.out.println("\n-------------------------");
            System.out.println("도서 제목: " + loan.book().title());
            System.out.println("반납 예정일: " + loan.dueOn());
            if (lateDays > 0) {
                System.out.println("상태: 연체");
                System.out.println("연체 일수: " + lateDays + "일");
            } else if (lateDays == 0) {
                System.out.println("상태: 오늘 반납 예정");
            } else {
                System.out.println("상태: 정상");
                System.out.println("남은 기간: " + (-lateDays) + "일");
            }
            if (fee.isPresent()) {
                System.out.println("연체료: " + fee.get().toPlainString() + "원");
                totalFee = totalFee.add(fee.get());
            } else {
                System.out.println("연체료 계산 불가");
                allFeesKnown = false;
            }
        }
        System.out.println("\n=========================");
        if (allFeesKnown) {
            System.out.println("총 연체료: " + totalFee.toPlainString() + "원");
        } else {
            System.out.println("총 연체료: 계산 불가 (연체율을 확인하세요.)");
        }
        System.out.println("=========================");
    }

    private void showAvailableBooks() {
        List<Book> availableBooks = library.getAvailableBooks();
        System.out.println("\n===== 대출 가능 도서 목록 =====");
        if (availableBooks.isEmpty()) {
            System.out.println("대출 가능한 도서가 없습니다.");
            return;
        }
        System.out.println("총 " + availableBooks.size() + "권");
        printBooks(availableBooks);
    }

    private void skipTime() {
        System.out.println("\n===== 시간 건너뛰기 =====");
        System.out.println("현재 날짜: " + clock.today());
        System.out.println("1. 일주일 후");
        System.out.println("2. 한 달 후");
        System.out.println("3. 일 년 후");
        System.out.println("0. 뒤로");
        int choice = readChoice("이동할 시간: ", 3);
        if (choice == 0) {
            return;
        }
        ChronoUnit unit = switch (choice) {
            case 1 -> ChronoUnit.WEEKS;
            case 2 -> ChronoUnit.MONTHS;
            case 3 -> ChronoUnit.YEARS;
            default -> throw new IllegalStateException("잘못된 선택입니다.");
        };
        LocalDate before = clock.today();
        try {
            clock.advance(unit);
            System.out.println("날짜 변경: " + before + " -> " + clock.today());
            System.out.println("4번 연체료 확인에서 변경된 연체료를 확인하세요.");
        } catch (IOException e) {
            System.out.println("가상 날짜를 저장하지 못했습니다. 날짜를 변경하지 않았습니다.");
        }
    }

    private Book pickBook() {
        BookSearch byTitle = new TitleSearch();
        while (true) {
            String title = readText("도서 제목 (0. 뒤로): ");
            if (title.equals("0")) {
                return null;
            }
            List<Book> matches = library.search(byTitle, title);
            if (matches.isEmpty()) {
                System.out.println("해당 도서가 없습니다. 제목을 다시 입력하세요.");
                continue;
            }
            if (matches.size() == 1) {
                return matches.get(0);
            }
            printBooks(matches);
            int choice = readChoice("도서 번호 (0. 제목 다시 입력): ", matches.size());
            if (choice != 0) {
                return matches.get(choice - 1);
            }
        }
    }

    private void printBooks(List<Book> books) {
        for (int index = 0; index < books.size(); index++) {
            Book book = books.get(index);
            String status = library.isAvailable(book) ? "대출 가능" : "대출 중";
            System.out.printf("%d. %s — %s%n", index + 1, book.description(), status);
        }
    }

    private boolean showLoanPeriodStatus() {
        if (library.hasLoanPeriod()) {
            return true;
        }
        System.out.println("LibrarySetup의 LOAN_PERIOD_DAYS에 대출 기간을 입력하세요.");
        return false;
    }

    private void printFee(Optional<BigDecimal> fee) {
        if (fee.isPresent()) {
            System.out.println("연체료 " + fee.get().toPlainString() + "원");
        } else {
            System.out.println("연체료를 계산하려면 LibrarySetup의 DAILY_LATE_RATE를 입력하세요.");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine().strip();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("값을 입력하세요.");
        }
    }

    private int readChoice(String prompt, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(readText(prompt));
                if (choice >= 0 && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // 문자를 입력했을 때도 같은 안내를 표시합니다.
            }
            System.out.printf("0부터 %d까지의 번호를 입력하세요.%n", max);
        }
    }

}
