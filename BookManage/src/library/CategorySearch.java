package library;

public final class CategorySearch extends BookSearch {
    @Override
    protected boolean matches(Book book, String keyword) {
        return book.category().equalsIgnoreCase(keyword);
    }
}
