package library;

import java.time.LocalDate;

public final class Loan {
    private final String memberId;
    private final Book book;
    private final LocalDate borrowedOn;
    private final LocalDate dueOn;
    private final LocalDate returnedOn;

    public Loan(String memberId, Book book, LocalDate borrowedOn, LocalDate dueOn) {
        this(memberId, book, borrowedOn, dueOn, null);
    }

    private Loan(String memberId, Book book, LocalDate borrowedOn,
                 LocalDate dueOn, LocalDate returnedOn) {
        this.memberId = memberId;
        this.book = book;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
        this.returnedOn = returnedOn;
    }

    public String memberId() { return memberId; }
    public LocalDate borrowedOn() { return borrowedOn; }
    public LocalDate returnedOn() { return returnedOn; }

    public Book book() { return book; }
    public LocalDate dueOn() { return dueOn; }
    public boolean isOpen() { return returnedOn == null; }
    public boolean belongsTo(String memberId) { return this.memberId.equals(memberId); }

    // 반납할 때는 검증을 거쳐 반납일이 기록된 새 대출 기록을 만듭니다.
    public Loan returnOn(LocalDate date) {
        if (!isOpen() || date.isBefore(borrowedOn)) {
            throw new IllegalArgumentException("이미 반납했거나 날짜가 올바르지 않습니다.");
        }
        return new Loan(memberId, book, borrowedOn, dueOn, date);
    }
}
