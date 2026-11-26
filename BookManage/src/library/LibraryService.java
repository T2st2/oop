package library;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LibraryService {
    private final List<Book> books;
    private final List<Loan> loans = new ArrayList<>();
    private final Integer loanPeriodDays;
    private final BigDecimal dailyLateRate;

    public LibraryService(List<Book> books, Integer loanPeriodDays, BigDecimal dailyLateRate) {
        if ((loanPeriodDays != null && loanPeriodDays <= 0)
                || (dailyLateRate != null && dailyLateRate.signum() < 0)) {
            throw new IllegalArgumentException("대출 기간은 양수, 연체율은 0 이상이어야 합니다.");
        }
        this.books = List.copyOf(books);
        this.loanPeriodDays = loanPeriodDays;
        this.dailyLateRate = dailyLateRate;
    }

    public List<Book> search(BookSearch search, String keyword) {
        return search.search(books, keyword);
    }

    public boolean isCatalogEmpty() {
        return books.isEmpty();
    }

    public boolean hasLoanPeriod() {
        return loanPeriodDays != null;
    }

    public LocalDate dueDateFor(LocalDate borrowedOn) {
        if (!hasLoanPeriod()) {
            throw new IllegalStateException("대출 기간을 먼저 설정하세요.");
        }
        return borrowedOn.plusDays(loanPeriodDays);
    }

    public boolean isAvailable(Book book) {
        return books.contains(book)
                && loans.stream().noneMatch(loan -> loan.book() == book && loan.isOpen());
    }

    public Loan borrow(String memberId, Book book, LocalDate borrowedOn) {
        if (!isAvailable(book)) {
            return null;
        }
        Loan loan = new Loan(memberId, book, borrowedOn, dueDateFor(borrowedOn));
        loans.add(loan);
        return loan;
    }

    public List<Loan> activeLoans(String memberId) {
        return loans.stream().filter(loan -> loan.belongsTo(memberId) && loan.isOpen()).toList();
    }

    public Optional<BigDecimal> returnBook(String memberId, Loan loan, LocalDate returnedOn) {
        int index = loans.indexOf(loan);
        if (index < 0 || !loan.belongsTo(memberId)) {
            throw new IllegalArgumentException("해당 회원의 대출 기록을 찾을 수 없습니다.");
        }
        Loan returned = loan.returnOn(returnedOn);
        Optional<BigDecimal> fee = calculateLateFee(loan.book(), loan.dueOn(), returnedOn);
        loans.set(index, returned);
        return fee;
    }

    public Optional<BigDecimal> calculateLateFee(Book book, LocalDate dueOn, LocalDate returnedOn) {
        long lateDays = ChronoUnit.DAYS.between(dueOn, returnedOn);
        if (lateDays <= 0) {
            return Optional.of(BigDecimal.ZERO);
        }
        if (dailyLateRate == null) {
            return Optional.empty();
        }
        // 정가 × 일일 연체율 × 연체 일수. 최종 금액을 원 단위로 반올림합니다.
        BigDecimal fee = BigDecimal.valueOf(book.priceWon()).multiply(dailyLateRate)
                .multiply(BigDecimal.valueOf(lateDays)).setScale(0, RoundingMode.HALF_UP);
        return Optional.of(fee);
    }
}
