package library;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class BookMenu {
    private final Scanner input;
    private final LibraryService library;

    public BookMenu(Scanner input, LibraryService library) {
        this.input = input;
        this.library = library;
    }

    public void runWithTemporaryMember() {
        String memberId = readText("임시 회원 ID: ");
        run(memberId, memberId);
    }

    public void run(String memberId, String memberName) {
        System.out.println(memberName + "님, 반갑습니다.");
        if (library.isCatalogEmpty()) {
            System.out.println("등록된 도서가 없습니다. LibrarySetup에 도서를 추가하세요.");
        }

        while (true) {
            System.out.println("\n1. 도서 검색  2. 대출  3. 반납  4. 연체료 확인  0. 종료");
            switch (readChoice("메뉴 번호: ", 4)) {
                case 1 -> searchBooks();
                case 2 -> borrowBook(memberId);
                case 3 -> returnBook(memberId);
                case 4 -> estimateFee();
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
            Loan loan = library.borrow(memberId, book, LocalDate.now());
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
            LocalDate returnedOn = LocalDate.now();
            Optional<BigDecimal> fee = library.returnBook(memberId, loan, returnedOn);
            System.out.println("'" + loan.book().title() + "' 반납 완료.");
            printFee(fee);
        }
    }

    private void estimateFee() {
        if (!showLoanPeriodStatus()) {
            return;
        }
        Book book = pickBook();
        if (book == null) {
            return;
        }
        LocalDate borrowedOn = readDate("대출일 (YYYY-MM-DD): ");
        LocalDate returnedOn = readDate("반납일 (YYYY-MM-DD): ");
        while (returnedOn.isBefore(borrowedOn)) {
            System.out.println("반납일은 대출일과 같거나 그 이후여야 합니다.");
            returnedOn = readDate("반납일 (YYYY-MM-DD): ");
        }
        LocalDate dueOn = library.dueDateFor(borrowedOn);
        System.out.println("반납 예정일은 " + dueOn + "입니다.");
        printFee(library.calculateLateFee(book, dueOn, returnedOn));
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

    private LocalDate readDate(String prompt) {
        while (true) {
            String text = readText(prompt);
            if (text.length() == 10) {
                try {
                    return LocalDate.parse(text);
                } catch (DateTimeParseException e) {
                    // 날짜 형식이나 달력상 날짜가 잘못된 경우 아래 안내를 표시합니다.
                }
            }
            System.out.println("실제 날짜를 YYYY-MM-DD 형식으로 입력하세요.");
        }
    }
}
