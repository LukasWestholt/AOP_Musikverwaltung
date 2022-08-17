# Musikverwaltung

- This project uses JavaFX 18.0.1 SDK.
- I use a newer JavaJDK-version: Oracle OpenJDK version 17.0.2
- 16.08. 24 Uhr Abgabe

## Contribution
- Zuko
- Lukas

## Beschreibung

Unsere Musikverwaltung erlaubt es Nutzer:innen, Musik aus eigenen Speicher- und Musikordnern direkt in die App zu laden und hier einzusehen, abzuspielen, zu sortieren und in Playlisten zu packen.

Das Programm startet in einem Willkommenfenster (HelloView) mit ausgewählten, aber randomisiert angezeigten Zitaten von Musiker:innen. Während einer Wartezeit von zwei Sekunden, in denen das Zitat gelesen werden kann, werden die Mediadateien geladen und anschließend öffnet sich das Hauptfenster (MainView). Hier sieht man eine Tabelle, in der die ausgesuchte Musik mit den Informationen Titel, Interpret und Genre dargestellt wird. Über der Tabelle befindet sich eine Anzeige, welche über die letzte vom Programm ausgeführte Aktion informiert und auch anzeigt, wenn ein neues Menu geöffnet wird.

Unter dem Menüpunkt Einstellungen in der Menüleiste können auf dem verwendeten Gerät die Ordner ausgewählt werden, welche die eigene Musik beinhalten. Musik, die nicht abgespielt werden kann, kann über die entsprechende Checkbox sichtbar gemacht werden. Danach sind automatisch alle ausgewählten Songs in der Tabelle enthalten und deren Information (sofern in der Datei vorhanden) eingetragen.

Die Tabelle ist das Herzstück der Verwaltung und erlaubt es, alle Songs zu betrachten und per Doppelklick abzuspielen. Durch Eingabe eines Suchbegriffs und die Auswahl einer Kategorie (unter der Tabelle) kann diese in Gänze oder spaltenweise gefiltert werden. So kann zum Beispiel nach einem bestimmten Interpretennamen oder Genre suchen. Die Ergebnisse finden sich nun in der Tabelle und werden ab ausreichender Suchwortlänge (ab dem vierten Buchstaben) farblich markiert. In der Menüleiste des Hauptfensters befinden sich die Einstellungen sowie ein Button zum Wechseln in die Playlistansicht. Außerdem gibt es die Möglichkeit, an die Credits und damit zu den Quellen der beispielhaft verwendeten Musik zu gelangen.

Das Erstellen von Playlisten kann ebenfalls in diesem Menü erfolgen. Dafür vorgesehen sind die Checkboxen neben jedem Song in der Tabelle. Sobald Songs ausgewählt wurden (durch einzelnes Auswählen oder durch den Knopf, um jeden Song auszuwählen), erscheint unter der Tabelle eine Eingabe für den Namen der zu erstellenden Playlist und ein Knopf, um die Erstellung abzuschließen. Diese oder andere Auswahlveränderungen können durch den neuladen Knop rückgängig gemacht werden.

Beim Wechsel über die Menüleiste (entsprechender Knopf) oder beim Erstellen einer neuen Playlist wird in die Playlistansicht gewechselt. Dort sind alle gerade eben oder bereits früher erstellten Playlists zu sehen. Jede Playlist hat einen vom Nutzer oder automatisch erstellten Namen und ein Bild, was der Nutzer dazu aussuchen kann. Die Möglichkeiten zur Auswahl dieses Bildes, zum Löschen der Playlist, zur Änderung des Namens oder zum Anzeigen des Inhalts der Playlist (Auswahl „zeigen“) eröffnen sich über einen Rechtsklick auf die Playlist. Mit einem Linksklick wird die Playlist als Gesamtes abgespielt und über einen Doppelklick wird die Detailansicht geöffnet.

Das Playlistmenü verfügt außerdem über die Funktion, mit dem Knopf „Playlist Vorschläge“ automatisch generierte Playlisten zu erstellen. Sobald fünf oder mehr Songs denselben Interpreten oder dasselbe Genre besitzen, werden sie als Playlist erstellt (sofern diese Playlist nicht bereits existiert).

Genau wie das Hauptfenster bietet das Playlistmenü auch jederzeit die Möglichkeit, den Player zu öffnen, um z. B. aktuell abgespielte Musik zu pausieren oder, falls noch gar nichts abgespielt wurde, den bei der letzten Nutzung des Programms abgespielten Song erneut aufzurufen.

Dieser Player stellt die wichtigste Komponente der gesamten Musikverwaltung dar: Er spielt Einzelsongs und Playlisten. Außerdem verfügt er über alle wichtigen Funktionen wie das
-	Pausieren,
-	Verändern der Lautstärke,
-	Wechseln zum nächsten oder vorherigen Song (falls vorhanden) und das
-	Bewegen innerhalb des Songs. Hier kann der Nutzer mithilfe der Knöpfe zum 15 Sekunden nach vorne oder hinten skippen oder präziser mit dem Slider auswählen, an welcher Stelle er weiterhören will.
     
Der Player zeigt in der Menüleiste immer den Namen des aktuellen Songs und standartmäßig das Bild eines Lautsprechers, oder ein Bild, welches in der Audiodatei des Songs eingearbeitet. Anstelle des Bildes gibt es die Möglichkeit, über einen Links- oder Rechtsklick den Visualisierungsgraph zum aktuellen Song auszuwählen. Dieser zeigt den Frequenzbereich der Musik und bietet eine Visualisierung der Musik. Außerdem besteht in der Menüleiste des Players die Option, die Musikverwaltung wieder zu öffnen, sollte sie geschlossen worden sein. Der Player vefügt außerdem über die Möglichkeit eine Playlist bzw Einzelsong auf „repeat“ zu setzen und die Playlist wiederholt sich ab dann automatisch.

Die Ansicht innerhalb der Playlist ist ähnlich aufgebaut wie das Hauptfenster, aber farblich davon unterschieden. Es bietet dieselben nützlichen Such- und Auswahlfunktionen, das Neuladen, die Darstellung von Aktionen sowie die Möglichkeit, eine weitere Playlist zu erstellen. Der Name jeder Playlist findet sich oben links.

Testen lässt sich die Anwendung ganz einfach mit den 26 bereitgestellten Lizenzfreien Songs im Media Ordner. Diese wie beschrieben im Einstellungsmenu als Ordner auswählen und alles ist eingerichtet.

## TODO
- Add and delete Song in playlist.
- "Verwaltungsmodus" and "Usermodus"
- Musicvideo play? or open yt link?
- Projekt in der 106/107 (PC-Pool) lauffähig? (Java Version?)
- Edit Song (with setupEditableStringColumn) in adminMode
and save it in settings file or in metadata.

## How to use in IDE

- Eclipse: https://stackoverflow.com/a/52156678/8980073
- JetBrains Idea: just clone

## Compile

Compile with Java VM Options:

Command for Windows:

    dir /s /B src\*.java > sources.txt
    mkdir bin
    javac -d bin --module-path lib\openjfx-18.0.1_windows-x64_bin-sdk\javafx-sdk-18.0.1\lib --add-modules javafx.controls,javafx.media,javafx.web -encoding utf8 @sources.txt

Command for Linux:

    find ./src/ -name "*.java" > sources.txt
    mkdir bin
    javac -d bin --module-path lib/openjfx-18.0.1_linux-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media,javafx.web -encoding utf8 @sources.txt

## Run

Run with Java VM Options:

Command for Windows:

    java --module-path lib/openjfx-18.0.1_windows-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media,javafx.web -classpath "bin;src/main/resources" -Dfile.encoding=UTF-8 -XX:+ShowCodeDetailsInExceptionMessages musikverwaltung.Musikverwaltung

Command for Linux:

    java --module-path lib/openjfx-18.0.1_linux-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media,javafx.web -classpath "bin;src/main/resources" -Dfile.encoding=UTF-8 -XX:+ShowCodeDetailsInExceptionMessages musikverwaltung.Musikverwaltung


## Aufgabe
Erstellen Sie ein Programm, welches Musikstücke verwaltet. Hierbei soll zu jedem
Musikstück Zusatzinformation wie Titel, Interpret, Genre usw. gehalten werden. Der
Benutzer soll in der Lage sein, nach bestimmten Musikstücken nach verschiedenen Kriterien
zu suchen. Weiter sollen Playlisten automatisch nach verschiedenen Kriterien erstellt werden
können: z. B. Alle Musikstücke eines Interpreten oder einer Gruppe oder alle Musikstücke
eines Genres etc.. Die Verwaltung sollte durch eine geeignete graphische Oberfläche (GUI)
erfolgen.

Hierbei sollte zwischen einem Verwaltungsmodus zur Darstellung aller vorhanden
Musikstücke, zum Einfügen und Löschen von Musikstücken, Sortieren usw. und einem
Benutzermodus zum Erstellen von Playlisten und Abspielen unterschieden werden.
## Optionale Aufgaben
Lassen Sie sich zu den einzelnen Musikstücken Bilder oder eine Art Diashow anzeigen.
Das Abspielen von Videos scheint mit swing sehr schwierig zu sein. JavaFX darf
ausnahmsweise nur benutzt werden, wenn alle benötigten Bibliotheken AUTOMATISCH
eingebunden werden. Falls Sie JavaFX benutzen, MÜSSEN Sie auf einem neutralen Rechner
prüfen, ob mit einem einfachen Import Ihr Programm ausführbar ist.
Hinweis: Musikstücke, Videos und Bilder unterliegen meist einem Urheberrecht. Zur
Demonstration sollten Sie frei verfügbare eventuell auch unsinnige Quellen (z. B.
Katzenvideos für Punk-Rock etc.) verwenden. Hierzu auch ein Link:
https://www.terrasound.de/gemafreie-musik-kostenlos-downloaden/
Bitte beachten Sie auch den nachfolgenden rechtlichen Hinweis.
## Rechtlicher Hinweis:
§ 52a Abs.1 UrhG regelt die öffentliche Zugänglichmachung für Unterricht und Forschung. Es ist
zulässig, veröffentlichte kleine Teile eines Werkes, Werke geringen Umfangs sowie einzelne Beiträge
aus Zeitungen oder Zeitschriften zur Veranschaulichung im Unterricht an Hochschulen oder
Teilnehmern eines Forschungsprojektes zugänglich zu machen, soweit dies zu dem jeweiligen Zweck
geboten und zur Verfolgung nicht kommerzieller Zwecke gerechtfertigt ist. Das Material darf nur dem
begrenzten Teilnehmerkreis bereitgestellt werden, bei einem Zugang über Online-Portale
muss der Zugang technisch auf die Teilnehmer beschränkt werden (also keine Inhalte auf die Interoder
Intranetseite der Hochschule!). Es dürfen dabei 15 % eines Werkes, bei Filmen und Musik
maximal 5 Minuten, ohne Einwilligung gezeigt werden. Bei Filmausschnitten müssen seit der
deutschen Kinopremiere mehr als zwei Jahren vergangen sein, ansonsten benötigt man eine
Erlaubnis.

Die hierfür erforderlichen Vervielfältigungen dürfen ohne Zustimmung erstellt werden. Dies beinhaltet
auch, Texte einzuscannen, um sie dann auf einen Server zu stellen.
Verwenden Sie bei Bildern oder Videos nur frei zugängliche (evtl auch unsinnige) Bilder oder Videos.
(z. B. frei zugängliche Katzenbilder bzw. -videos)
