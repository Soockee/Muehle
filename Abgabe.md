# Abgabe des p20-Projekts (PiS, SoSe 2017)

> * Füllen Sie das Dokument aus
>   * "Kreuzen" Sie zutreffende Auswahlkästchen `[ ]` an, indem Sie ein "x" einfügen: `[x]`
>   * Fügen Sie an den entsprechenden Stellen Links zu Dateien aus Ihrem bzw. dem p20-Repository ein
>   * Fügen Sie textuelle Formate an den entsprechenden Stellen ein
>   * Hinterlassen Sie Kommentare und Anmerkungen zu Besonderheiten Ihres Codes; meist sind diese Stellen durch _Schrägschrift_ kenntlich gemacht
> * Stellen Sie das ausgefüllte Dokument zur Verfügung
>   * Legen Sie das Dokument in Ihrem Repository (Repo) ab.
>   * Bringen Sie eine **ausgedruckte Fassung** mit zur Abnahme

Zur Vorbereitung der Abgabe des p20-Projekts laden Sie sich dieses Markdown-Dokument bitte herunter (es ist im [p20-Repository](https://git.thm.de/dhzb87/p20) zu finden), füllen es aus und fügen die ausgefüllte Version bitte in Ihrem Projektverzeichnis im Top-Level ebenfalls unter dem Namen `Abgabe.md` hinzu. Ergänzend bringen Sie eine ausgedruckte Fassung des Dokuments zur Abgabe mit.

Was müssen Sie ausfüllen? Die entsprechenden Stellen sind im Dokument gekennzeichnet und im [Markdown-Format](https://git.thm.de/help/user/markdown.md) zu ersetzen. Erwähnen Sie Abweichungen und Besonderheiten Ihres Programmcodes von den Vorgaben. 

An einigen Stellen müssen Sie in diesem Dokument eine Referenz zur P20-Dokumentation einfügen. Wenn Sie z.B. von der Interface-Seite des Master-Branchs auf der [GitLab-Seite](https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md) ein spezielles Interface referenzieren wollen, dann bewegen Sie die Maus auf die entsprechende Überschrift, links am Rand erscheint dann ein Link-Symbol. Sie kopieren den Link, indem Sie die Maus über dem Symbol positionieren, die rechte Maustaste klicken und “Adresse des Links kopieren” auswählen. Für Links zu Dateien in Ihrem Repository kopieren Sie einfach die Seitenangabe des Browsers, wenn Sie die Datei im Web-Interface des git-Repositories geöffnet haben.

## Projektdaten

> - Geben Sie an, welchen Praktikumsblock Sie besucht haben
> - Geben Sie das Team-Repository an
> - Benennen Sie die beteiligten Team-Mitglieder
> - Geben Sie an, welchen Anteil jedes Team-Mitglied an dem Projekt hatte

Wir haben das Praktikum bei

* _Herrn Herzberg_ besucht
* im _4._ Block

Das Team-Repository auf GitLab: _https://git.thm.de/sskh68/Muehle_

Die Team-Mitglieder sind:

* _Krappatsch_, _Paul_: _paul.krappatsch@mni.thm.de_
* _Stockhause_, _Simon_: _simon.stockhause@mni.thm.de
* _Franke_, _Björn_: _bjoern.franke@mni.thm.de_

Die Beitragsanteile in Prozent verteilen sich wie folgt in unserem Team; die Summe der Prozentangaben ergibt 100% (oder 99% bei Drittelung).

| Nachname | Anteil |
| -------- | -------- |
| _Krappatsch_ | _33%_   |
| _Stockhause_ | _33%_   |
| _Franke_ | _33%_   |

**Hinweis**: Angenommen, die Abgabe des Teams für das p20-Projekt wird mit `p` Punkten bewertet. Und angenommen, die maximale Prozentangabe sei `m`. Dann berechnet sich der Punktwert einer Person mit dem Prozentanteil `t` zu `t/m*p`. Ein Beispiel: Paul hat sich mit 10% eingebracht, Paula mit 60%, Peter mit 30%; in Summe muss sich 100% ergeben. Das Projekt sei mit 18 Punkten bewertet. Dann erhält Paul `10/60*18 = 3` Punkte, Paula `60/60*18 = 18` Punkte und Peter `30/60*18 = 9` Punkte.

## Basispunkte (20P)

### Was gesagt werden muss

Gleich zu Anfang möchten wir auf Folgendes hinweisen und aufmerksam machen:

_Ihr Text_

### Programme starten

> - Geben Sie an, wie man das T3-Spiel startet
> - Geben Sie an, wie man das Mühle-Spiel startet

* Das T3-Spiel wird in der Konsole folgt gestartet:
```
. . .
. . .
. . .
[0: Computer move, ?: Help]
```

* Das Mühle-Spiel wird in der Konsole wie folgt gestartet:
```
.  -  -  -  -  -  .  -  -  -  -  -  .
|                 |                 |
|     .  -  -  -  .  -  -  -  .     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
.  -  .  -  .           .  -  .  -  .
|     |     .  -  .  -  .     |     |
|     |           |           |     |
|     .  -  -  -  .  -  -  -  .     |
|                 |                 |
.  -  -  -  -  -  .  -  -  -  -  -  .
[0: Computer move, ?: Help]
Enter position to set stone: 
```

### Der gemeinschaftliche Code zum Berechnen von Zügen (5x 2P)

> - Erläutern Sie, woran man erkennt, dass Sie zum Berechnen von Zügen für T3 bzw. Mühle den gleichen Code verwenden! (Beispielhafte Antworten könnten sein: Code taucht nur einmal auf, wir verwenden das Interface als Typ)
> - Geben Sie an, welche Punkte Sie aus der Anforderungtabelle erfüllen
> - Verweisen Sie auf die Code-Dateien und den Codebereich, welche die Anforderungen implementieren
> - Erläutern Sie, inwiefern Ihre Implementierung von den Vorgaben abweicht, was Sie z.B. anders oder nicht implementiert haben

Unser Code zur Berechnung eines T3- bzw. eines Mühle-Zugs ist in der Tat identisch. Das sieht man im Code daran, dass ...

* wir die Klasse Ai sowohl für Morris, als auch für T3 verwenden. Somit benutzen wir den identischen Code für beide Klassen
* Die Klasse Ui, welche das User-Interface für Mühle realisiert, und die Klasse UIT3
  , welche das User-Interface für Tic-Tac-Toe realisiert, verwenden beide die Methode  evaluateBestBoard der Klasse Ai, um 
  einen Computer-Spielzug zu generieren.
  Weiterhin wäre es mit der Ai-Klasse möglich, für jede korrekte Implementierung des Stream-Board-Interface, einen Computer-Spielzug zu generieren.
  
Beide Implementierungen nutzen für die folgenden Punkte den gleichen Code und
- [X] führen eine iterative Tiefensuche durch (2P)
- [X] und zwar als Alpha-Beta-Suche (2P),
- [X] eine Transpositionstabelle beschleunigt die Suche (2P)
- [X] die Suche bewertet eine Stellung jenseits des Suchhorizonts mit Hilfe der Monte-Carlo-Methode (2P)
- [X] die bis dahin beste gefundene Zugfolge wird mit jeder Iteration aktualisiert und kann bei Bedarf ausgegeben werden (2P)

Die Implementierung für die Anforderungen finden sich für
- die Tiefensuche (je nachdem, was Sie implementiert haben):
  - Minimax/Negamax: _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java _ (Zeilen _von 84_-_bis 137_)
  - Alpha-Beta:  _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java _ (Zeilen _von 84_-_bis 137_)
  - iterative Suche:  _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java _ (Zeilen _von 25_-_bis 81_)
- die Transpositionstabelle: _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java _ (Zeile _von 89_-_bis 101_) (Zeile _von 139_-_bis 147_)
- die Monte-Carlo-Methode: _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java _ (Zeilen _von 149_-_bis 183_)

Wir möchten folgende Anmerkungen zu unserem Code machen:

* In der Klasse Ai gibt es zwei Methoden, die die Methode evaluateBestBoard aufrufen kann, um eine iterative Suche durchzuführen.
  Die Methode iterativeDepthSearch führt zunächst eine Tiefensuche durch und verwendet dabei eine Killer-Heuristik, die Methode
  altIterativeDepthSearch wiederum führt zwar diese auch aus, jedoch ohne Killer-Heuristik, dies ermöglicht aber die Tiefensuch parallel 
   durchzuführen.

* Die Klasse CompletableFuture wurde in der UI benutzt
    * Aufgaben wurden verketten Beispiel:
        ```
        CompletableFuture<StreamBoard> cf1 = CompletableFuture.supplyAsync(() -> {
            // do something
        });
        CompletableFuture<StreamBoard> cf2 = cf1.thenComposeAsync((cf1Result) -> {
            // do something
        });
        ```

### Implementierung der Spielbrett- und Zuglogik (2P)

> - Geben Sie an, wie Sie die Spielbretter implementiert haben; löschen Sie nicht zutreffende Angaben
> - Geben Sie an, welches Interface Ihre Spielbretter implementieren
> - Erläutern Sie, falls Sie von den Vorgaben abweichen!

Wir haben die Spielbretter implementiert für
- [x] T3 (immutabel, strombasiert)
- [x] Mühle (immutabel, strombasiert)

Folgende Interfaces haben wir implementiert:
- Das T3-Board nutzt das Interface: _https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md#interface-streamboard_
- Das Mühle-Board nutzt das Interface: _https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md#interface-streamboard_

Wir möchten folgende Anmerkungen zu unserem Code machen:

_Mein Text_

### Der Programmdialog (2x 2P)

Wir sind aus folgendem Grund abgewichen vom spezifizierten Dialogverhalten:

Es wurden Erweiterungen eingebaut. Somit bleibt das Dialogverhalten nahe an dem, in der Vorlesung zu diesem Thema präsentierten, Dialogverhalten.

Es wurde sich dazu entschlossen, nach einem Spielerzug keinen direkten Ai Move zu machen, da das ständige "undo" aufrufen den Spielfluss gestört hätte
Somit ist eine Spieler vs. Spieler Situation direkt gegeben und auf Anfrage ein Computermove direkt machbar.

So sieht der Programmdialog mit dem T3-Spiel aus:



So sieht der Programmdialog mit dem Mühle-Spiel aus:

* Hilfe Ausgabe Hier zu sehen ist das Kommando '?'
```
Enter position to set stone: ?
You can enter the following commands: 
<exit> : Exit the Application
<save> : Saving the Game
<0> : AI is making the Move for you
<1-24>: Enter a number between 1 and 24 to make this move and follow the instructions afterwards
<undo>: undo the last move
<guide>: A Gameguide which helps you to understand how the game works
<save>: saves the current game
<load>: loads the file 'save.txt' in the current directory
<flip>: flip causes to switch the symbols of the stones
<new>: new game
1  -  -  -  -  -  2  -  -  -  -  -  3
|                 |                 |
|     9  -  -  - 10  -  -  - 11     |
|     |           |           |     |
|     |    17  - 18  - 19     |     |
8  - 16  - 24          20  - 12  -  4
|     |    23  - 22  - 21     |     |
|     |           |           |     |
|    15  -  -  - 14  -  -  - 13     |
|                 |                 |
7  -  -  -  -  -  6  -  -  -  -  -  5
```

* Setzphase: Stein in der Setzphase setze
```
Enter position to set stone: 1
X  -  -  -  -  -  .  -  -  -  -  -  .
|                 |                 |
|     .  -  -  -  .  -  -  -  .     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
.  -  .  -  .           .  -  .  -  .
|     |     .  -  .  -  .     |     |
|     |           |           |     |
|     .  -  -  -  .  -  -  -  .     |
|                 |                 |
.  -  -  -  -  -  .  -  -  -  -  -  .
[0: Computer move, ?: Help]
Enter position to set stone: 
```

* Laden:laden einer Spieldatei, mit dazugehöriger Abfrage der Datei
```
[0: Computer move, ?: Help]
Enter position to set stone: load
Please enter the file e.g. <save.txt>: moves_enabled.txt
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
O  -  O  -  .           .  -  O  -  O
|     |     O  -  .  -  X     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
X  -  -  -  -  -  X  -  -  -  -  -  O
[0: Computer move, ?: Help]
Enter Stone to move: 
```

* Zugphase: Spielstein in der Zugphase bewegen mit entfernen. Falscheingabe verusacht erneute Aufforderung erneuter Eingabe und zeigt mögliche Spielzüge an
```
[0: Computer move, ?: Help]
Enter Stone to move: 21
Enter position to move stone: 20
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
O  -  O  -  .           X  -  O  -  O
|     |     O  -  .  -  .     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
X  -  -  -  -  -  X  -  -  -  -  -  O
[0: Computer move, ?: Help]
Enter Stone to move: 22
Enter position to move stone: 2
Invalid move. Please try again
Following moves are possible: 
[10-18] [14-22] [16-24] [23-22] [23-24-01] [23-24-03] [23-24-06] [23-24-07] [23-24-09] [23-24-11] [23-24-13] [23-24-15] [23-24-20] 
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
O  -  O  -  .           X  -  O  -  O
|     |     O  -  .  -  .     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
X  -  -  -  -  -  X  -  -  -  -  -  O
[0: Computer move, ?: Help]
Enter Stone to move: 23
Enter position to move stone: 24
Enter stone to remove: 
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
O  -  O  -  O           X  -  O  -  O
|     |     .  -  .  -  .     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
.  -  -  -  -  -  X  -  -  -  -  -  O
[0: Computer move, ?: Help]
Enter Stone to move: 
```

* Speichern: Speichern einer Spielsittuation. Speicherpfad wird ausgegeben. Dateiname kann ausgewählt werden. --- //
```
[0: Computer move, ?: Help]
Enter Stone to move: save
Please enter the file e.g. <save.txt>: example_save.txt
Savefilepath: C:\Users\Simon\IdeaProjects\Muehle\example_save.txt
```

* Ai Move: Standard Sätze, die angeben, wie weit fortgeschritten die Ai bei ihrer Berechnung ist. Berechnung kann abgebrochen werden.
```
Enter Stone to move: 0
Let me think about it...
Enter any input to interrupt the search

	What if i do...	Or that...	This seems quite good...	Oh boy!...	I need to think this through...	This is going to be a masterful move!  stopTESTTESTanyCommand
I move from 5 to 6
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  .  -  .     |     |
O  -  O  -  O           X  -  O  -  O
|     |     .  -  .  -  .     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
X  -  -  -  -  -  .  -  -  -  -  -  O
[0: Computer move, ?: Help]
Enter Stone to move: 
```

* Endspielstand und Speicherabfrage
```
O Won!
Endgame: 
X  -  -  -  -  -  O  -  -  -  -  -  X
|                 |                 |
|     X  -  -  -  O  -  -  -  X     |
|     |           |           |     |
|     |     .  -  O  -  .     |     |
O  -  O  -  .           .  -  O  -  O
|     |     .  -  .  -  .     |     |
|     |           |           |     |
|     X  -  -  -  O  -  -  -  X     |
|                 |                 |
X  -  -  -  -  -  O  -  -  -  -  -  X
Do you want to save the game? type: <y> / <n>
y
Please enter the file e.g. <save.txt>: ex.txt
Savefilepath: C:\Users\Simon\IdeaProjects\Muehle\ex.txt
```

### Das Laden und Speichern von Spielen (2x 2P)

> - Geben Sie an, ob Ihr Programm Spiele speichern kann
> - Geben Sie an, ob Ihr Programm Spiele laden kann
> - Haben Sie das Laden fehlerhafter Spieldateien überprüft?
> - Geben Sie ein Beispiel für ein gespeichertes T3-Spiel an
> - Geben Sie ein Beispiel für ein gespeichertes Mühle-Spiel an

Wir speichern einen Spielstand gemäß [Spezifikation](https://git.thm.de/dhzb87/p20/blob/master/LoadSaveSpec.md)
- [x] für das T3-Spiel
- [x] für das Mühle-Spiel

Wir können einen Spielstand gemäß [Spezifikation](https://git.thm.de/dhzb87/p20/blob/master/LoadSaveSpec.md) laden
- [x] für das T3-Spiel
- [x] für das Mühle-Spiel
- [x] Wir haben überprüft, dass ungültige Speicherformate nicht geladen werden und keinen Fehler im Programmablauf erzeugen
 

So sieht der Dateiinhalt eines gespeicherten T3-Spiels von unserem Programm aus:

```
3,4,8,6,2,5,7,0,1
```
 
So sieht der Dateiinhalt eines gespeicherten Mühle-Spiels von unserem Programm aus:

```
02,12,05,15,06,13,11,22,18,21-02,02,19,17,14-05,09,08-11,16-19,04,09-01,13-05,02-03,21-20,03-11,20-19,01-00,19-20,06-07,05-13-11,17-09,13-21-07,09-17-04,15-23,00-01,21-13-01,18-10,13-21-10
```

## Bonuspunkte (15P)

### Transpositionstabelle oder Bitboards (entweder/oder 5P)

> - Geben Sie an, ob Sie entweder für Mühle
>   - entweder Transpositionstabellen 
>   - oder Bitboards umgesetzt haben

Sie können für die Umsetzung eines der beiden folgenden Punkte 5 Bonuspunkte erhalten; es ist ohne Mehrwert, beide Punkte umzusetzen:

- [x] Die Implementierung der Transpositionstabelle für Mühle nutzt Symmetrien der "Spielmechanik" aus, um symmetrische Stellungen zu erkennen
- [ ] Die Implementierung setzt Bitboards für Mühle um

Die Realisation ist im Code zu finden unter: _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Morris.java_ (Zeile _503_-_564_)(transposed Boards are equal), https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java_(implementieung in der Ai-Klasse als ttable)

### Immutabilität und Streams mit Lambdas (2x 5P)

#### Immutabilität (5P)

> - Geben Sie an, ob Sie immutable Datenstrukturen verwenden

Die Realisierung der Immutabilität ist im Code gegeben durch

- [x] Nutzung des entsprechenden immutablen Interfaces der Spezifikation: _https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md#interface-streamboard zu p20-Repo_
- [x] Folgende Klasse implementiert das Interface: 
     * _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Morris.java_ , Zeilen 18-_18_
     * _https://git.thm.de/sskh68/Muehle/blob/master/T3/src/T3.java_ , Zeilen _17_-_17_
- [x] Dadurch, dass alle Felder als `final` ausgewiesen sind
- [x] Dadurch, dass alle Felder als `private` ausgewiesen sind 

#### Streams und Lambdas (5P)

> - Geben Sie an, ob Sie das `StreamBoard`-Interface nutzen (mit Verweis auf die Spezifikation)
> - Weisen Sie mind. 3 Codestellen aus, bei denen Streams zum Einsatz kommen
> - Weisen Sie mind. 6 Codestellen aus, bei denen Lambda-Ausdrücke genutzt werden

Sie nutzen an vielen Stellen Streams und Lambda-Expressions:

- [x] Wir implementieren das `StreamBoard`: https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md#interface-streamboard
- [x] An folgenden Stellen im Code kommen Streams zum Einsatz:
  - Streams und Labmda-Ausdrücke kommen im Code sehr häufig vor. Beispielhaft folgendes: 
  - _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Morris.java_, Zeilen: _174_-_180; 191 - 226; 252 - 335_
  - _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/UI.java_, Zeilen: _113
  
- [x] An folgenden Stellen im Code kommen Lambda-Ausdrücke zum Einsatz
  - _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/UI.java_, Zeilen: _363_ - _414_,
  - _https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Ai.java_, Zeilen: _170_ - _178_
  https://git.thm.de/sskh68/Muehle/blob/master/Morris/src/Morris.java_, Zeilen: _198 - 205, 218 - 221, 223 - 225, 234,244,247..._
  - _Linkangabe zur Datei_, Zeilen: _ZeileX_, _ZeileY_, _ZeileZ_, ...

### Git-Meisterschaft (5P/Person)
- [ ] Aus unserem Team haben sich die folgenden Mitglieder besonders bei der Erstellung, Korrektur und Stabilisierung der Spezifikationen im Git-Repository engagiert: