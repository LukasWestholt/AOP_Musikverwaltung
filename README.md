# Musikverwaltung

- This project uses JavaFX 18.0.1 SDK.
- I use a newer JavaJDK-version: Oracle OpenJDK version 17.0.2
- 16.08. 24 Uhr Abgabe

## Contribution
- hami
- Zuko
- Lukas

## TODO
- Add and delete Song in playlist.
- "Verwaltungsmodus" and "Usermodus"
- Musicvideo play? or open yt link?
- Projekt in der 106/107 (PC-Pool) lauffähig? (Java Version?)
- Edit Song (with setupEditableStringColumn) in adminMode
and save it in settings file or in metadata.
- Credits field for "Musik: https://www.musicfox.com/"
- Delete media files with copyright problems and download some new without
- auto-Playlists
- add everywhere javadoc messages (like checkstyle suggests)
- setting checkbox show unplayable songs
- Credits: <a href="https://www.flaticon.com/free-icons/speaker" title="speaker icons">Speaker icons created by Freepik - Flaticon</a>

## How to use in IDE

- Eclipse: https://stackoverflow.com/a/52156678/8980073
- JetBrains Idea: just clone

## Compile

Compile with Java VM Options:

Command for Windows:

    dir /s /B src\*.java > sources.txt
    mkdir bin
    javac -d bin --module-path lib\openjfx-18.0.1_windows-x64_bin-sdk\javafx-sdk-18.0.1\lib --add-modules javafx.controls,javafx.media -encoding utf8 @sources.txt

Command for Linux:

    find ./src/ -name "*.java" > sources.txt
    mkdir bin
    javac -d bin --module-path lib/openjfx-18.0.1_linux-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media -encoding utf8 @sources.txt

## Run

Run with Java VM Options:

Command for Windows:

    java --module-path lib/openjfx-18.0.1_windows-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media -classpath "bin;src/main/resources" -Dfile.encoding=UTF-8 -XX:+ShowCodeDetailsInExceptionMessages musikverwaltung.Musikverwaltung

Command for Linux:

    java --module-path lib/openjfx-18.0.1_linux-x64_bin-sdk/javafx-sdk-18.0.1/lib --add-modules javafx.controls,javafx.media -classpath "bin;src/main/resources" -Dfile.encoding=UTF-8 -XX:+ShowCodeDetailsInExceptionMessages musikverwaltung.Musikverwaltung


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
## Mögliche Optionale Aufgaben
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
