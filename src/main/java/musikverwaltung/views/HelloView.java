package musikverwaltung.views;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import musikverwaltung.ScreenController;
import musikverwaltung.data.Quote;

public class HelloView extends GenericView {
    public HelloView(ScreenController sc) {
        super(sc);
        Label quotesLabel = new Label(getRandomQuote());
        quotesLabel.setWrapText(true);
        quotesLabel.getStyleClass().add("quote");
        VBox vbox = new VBox(quotesLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vbox);
        BorderPane.setMargin(vbox, new Insets(30));

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label bottomText = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion);
        bottomText.setWrapText(true);
        borderPane.setBottom(bottomText);
        BorderPane.setMargin(bottomText, new Insets(30));
        BorderPane.setAlignment(bottomText, Pos.CENTER);

        showNodes(borderPane);
    }

    private static String getRandomQuote() {
        List<Quote> quotes = Arrays.asList(
                new Quote("Die wenigsten Fehltritte begeht man mit den Füßen.", "Rod Stewart"),
                new Quote("Wer sich an die 80er noch erinnern kann, der war ned dabei.", "Falco"),
                new Quote("Bekannt wollte ich eigentlich nie werden, mir genügte es, der Größte zu sein.",
                        "Ray Charles"),
                new Quote("One thing I’ve learned is that I’m not the owner of my talent; I’m the manager of it.",
                        "Madonna"),
                new Quote("You can’t knock on an opportunity’s door and not be ready.", "Bruno Mars"),
                new Quote("Musicians don’t retire; they stop when there’s no more music in them.",
                        "Louis Armstrong"),
                new Quote("The music is not in the notes, but in the silence between.",
                        "Wolfgang Amadeus Mozart"),
                new Quote("I would rather write 10,000 notes than a single letter of the alphabet.",
                        "Ludwig van Beethoven"),
                new Quote("I can’t understand why people are frightened of new ideas."
                        + "I’m frightened of the old ones.", "John Cage"),
                new Quote("Works of art make rules; rules do not make works of art.", "Claude Debussy"),
                new Quote("Es gibt keine Religion außer Sex und Musik.", "Sting"),
                new Quote("Ich habe 30 Jahre gebraucht, um über Nacht berühmt zu werden.",
                        "Harry Belafonte"),
                new Quote("Meine Musik bekämpft das System, das uns beibringt, zu leben und zu sterben.",
                        "Bob Marley"),
                new Quote("Ohne Musik wär’ alles nichts.", "Wolfgang Amadeus Mozart"),
                new Quote("Rap-Musik ist die einzige wichtige Musikform, die seit dem Punkrock eingeführt wurde.",
                        "Kurt Cobain"),
                new Quote("Über Musik zu reden ist wie über Architektur zu tanzen.", "Frank Zappa"),
                new Quote("Time that you enjoy wasting, was not wasted.", "John Lennon"),
                new Quote("There is more stupidity than hydrogen in the universe.", "Frank Zappa"),
                new Quote("I need drama in my live to keep making music", "Eminem"),
                new Quote("Wenn Affen Klavierspielen können, warum sollten Menschen nicht dazu singen?",
                        "John Lennon"),
                new Quote("Ich möchte die Welt mit unserer Musik nicht verändern. In unseren Songs sind keine "
                        + "Nachrichten versteckt. Ich schreibe gerne Songs für den modernen Konsum.",
                        "Freddy Mercury"),
                new Quote("Everything will be okay in the end. If it’s not okay, then it’s not the end.",
                        "Ed Sheeran"),
                new Quote("I’ve never really wanted to go to Japan, simply because I don’t like eating fish and I "
                        + "know that’s very popular out there in Africa.", "Britney Spears"),
                new Quote("Imagination creates reality.", "Richard Wagner"),
                new Quote("Dare to wear the foolish clown face.", "Frank Sinatra"),
                new Quote("Wie bringst du Gott am einfachsten zum Lachen? Erzähl ihm deine Pläne.",
                        "Joe Perry (Aerosmith)"),
                new Quote("I’m not a businessman, I’m a business, man!", "Jay Z"),
                new Quote("Ich habe so lange wie möglich versucht, nicht erwachsen zu werden.",
                        "Marius Müller-Westernhagen"),
                new Quote("Schau nie die Posaunen an, Du machst ihnen nur Mut.", "Igor Stravinsky"),
                new Quote("Ein Gitarrenriff sollte nie länger sein, als es dauert, eine Bierflasche zu köpfen.",
                        "Lemmy Kilmister (Motörhead)"),
                new Quote("Miami ist eine fantastische Stadt, um eine Banane zu essen.", "Iggy Pop")
        );
        Quote quote = quotes.get(new Random().nextInt(quotes.size()));
        return "\"" + quote.getText() + "\"\n- " + quote.getAuthor();
    }
}
