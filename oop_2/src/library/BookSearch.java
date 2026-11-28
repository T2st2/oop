package library;

import java.util.List;
import java.util.Locale;

// 검색 절차를 상속하고, 각 하위 클래스가 일치 조건을 구현합니다.
public abstract class BookSearch {
    public final List<Book> search(List<Book> books, String keyword) {
        return books.stream().filter(book -> matches(book, keyword)).toList();
    }

    protected abstract boolean matches(Book book, String keyword);

    protected final boolean containsIgnoreCase(String text, String keyword) {
        return text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }
}
