package library;

public final class Book {
    private final String title;
    private final String category;
    private final String publisher;
    private final long priceWon;

    public Book(String title, String category, String publisher, long priceWon) {
        if (title.isBlank() || category.isBlank() || publisher.isBlank() || priceWon < 0) {
            throw new IllegalArgumentException("도서 정보와 정가를 확인하세요.");
        }
        this.title = title.strip();
        this.category = category.strip();
        this.publisher = publisher.strip();
        this.priceWon = priceWon;
    }

    public String title() { return title; }
    public String category() { return category; }
    public String publisher() { return publisher; }
    public long priceWon() { return priceWon; }

    public String description() {
        return String.format("%s (%s / %s)", title, category, publisher);
    }
}
