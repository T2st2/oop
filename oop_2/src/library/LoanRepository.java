package library;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

// 대출 및 반납 기록의 파일 입출력을 담당합니다.
final class LoanRepository {
    private final Path file;

    LoanRepository(Path file) {
        this.file = file.toAbsolutePath();
    }

    List<Loan> load(List<Book> books) {
        List<Loan> loans = new ArrayList<>();
        if (Files.notExists(file)) {
            return loans;
        }

        Properties data = new Properties();
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            data.load(reader);
            int count = Integer.parseInt(required(data, "count"));
            if (count < 0 || count > data.size() / 7) {
                throw new IllegalArgumentException("대출 기록 개수가 올바르지 않습니다.");
            }
            Set<Book> openBooks = new HashSet<>();
            for (int i = 0; i < count; i++) {
                String key = "loan." + i + ".";
                String title = required(data, key + "title");
                String category = required(data, key + "category");
                String publisher = required(data, key + "publisher");
                // 저장된 도서 정보를 현재 목록의 실제 Book 객체와 연결합니다.
                List<Book> matches = books.stream()
                        .filter(book -> book.title().equals(title)
                                && book.category().equals(category)
                                && book.publisher().equals(publisher))
                        .toList();
                if (matches.size() != 1) {
                    throw new IllegalArgumentException("도서를 식별할 수 없습니다: " + title);
                }
                Book book = matches.get(0);
                String memberId = required(data, key + "memberId");
                LocalDate borrowedOn = LocalDate.parse(required(data, key + "borrowedOn"));
                LocalDate dueOn = LocalDate.parse(required(data, key + "dueOn"));
                String returnedOn = required(data, key + "returnedOn");
                if (dueOn.isBefore(borrowedOn)) {
                    throw new IllegalArgumentException("반납 예정일이 대출일보다 빠릅니다.");
                }
                Loan loan = new Loan(memberId, book, borrowedOn, dueOn);
                if (!returnedOn.isEmpty()) {
                    loan = loan.returnOn(LocalDate.parse(returnedOn));
                } else if (!openBooks.add(book)) {
                    throw new IllegalArgumentException("중복 대출 기록: " + title);
                }
                loans.add(loan);
            }
            return loans;
        } catch (IOException e) {
            throw new UncheckedIOException("대출 기록을 불러오지 못했습니다: " + file, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("대출 기록 파일을 확인하세요: " + file, e);
        }
    }

    void save(List<Loan> loans) {
        Properties data = new Properties();
        data.setProperty("count", Integer.toString(loans.size()));
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            String key = "loan." + i + ".";
            data.setProperty(key + "memberId", loan.memberId());
            data.setProperty(key + "title", loan.book().title());
            data.setProperty(key + "category", loan.book().category());
            data.setProperty(key + "publisher", loan.book().publisher());
            data.setProperty(key + "borrowedOn", loan.borrowedOn().toString());
            data.setProperty(key + "dueOn", loan.dueOn().toString());
            data.setProperty(key + "returnedOn", loan.isOpen() ? "" : loan.returnedOn().toString());
        }

        // 임시 파일에 먼저 저장하여 기존 파일이 중간에 잘리는 위험을 줄입니다.
        Path temp = null;
        try {
            temp = Files.createTempFile(file.getParent(), "loans-", ".tmp");
            try (var writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                data.store(writer, "Library loan records");
            }
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("대출 기록을 저장하지 못했습니다: " + file, e);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // 임시 파일 정리 실패가 원래 오류를 가리지 않게 합니다.
                }
            }
        }
    }

    private String required(Properties data, String key) {
        String value = data.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("누락된 항목: " + key);
        }
        return value;
    }
}
