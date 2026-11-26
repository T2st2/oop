package library;

public final class PublisherSearch extends BookSearch {
    @Override
    protected boolean matches(Book book, String keyword) {
        return containsIgnoreCase(book.publisher(), keyword);
    }
}
