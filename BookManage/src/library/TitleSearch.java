package library;

public final class TitleSearch extends BookSearch {
    @Override
    protected boolean matches(Book book, String keyword) {
        return containsIgnoreCase(book.title(), keyword);
    }
}
