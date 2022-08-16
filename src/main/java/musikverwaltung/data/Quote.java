package musikverwaltung.data;

/**
 * Represents Quotes and their author
 */
public class Quote {
    private final String text;
    private final String author;

    /**
     * @param text = the quote itself
     * @param author = author of quote
     */
    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
    }

    /**
     * @return the quote itself
     */
    public String getText()   {
        return text;
    }

    /**
     * @return author of quote
     */
    public String getAuthor() {
        return author;
    }
}
