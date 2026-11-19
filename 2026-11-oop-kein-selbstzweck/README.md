# OOP ist kein Selbstzweck – Begleitcode

Dieses Projekt enthält den lauffähigen Java-Code zur zweiteiligen Blogserie
«Objektorientierte Programmierung ist kein Selbstzweck» auf rolfsblog.ch:

- **Teil 1: Objekte, Kapselung und Vererbung an einer Kursverwaltung**

  <https://rolfsblog.ch/objektorientierte-programmierung-ist-kein-selbstzweck-1-2/>

  Vom Montagmorgen mit 14 Teilnehmenden in einem Schulungsraum für 12 über
  Objekte mit klarer Verantwortung und Kapselung bis zu Vererbung und Komposition.

- **Teil 2: Polymorphie und Abstraktion an einer Kursverwaltung**

  <https://rolfsblog.ch/objektorientierte-programmierung-ist-kein-selbstzweck-2-2/>

  Wie Polymorphie die `if`-Kaskaden in der Rechnung ersetzt, wie Abstraktion
  neue Zahlungswege wie TWINT ohne Umbau der Anmeldung ermöglicht – und welche
  Alternative Sealed Types, Records und Pattern Matching in modernem Java bieten.

### Die Idee der Serie

Die vier Säulen der objektorientierten Programmierung – Kapselung, Vererbung,
Polymorphie und Abstraktion – werden in der Serie nicht über Definitionen
eingeführt, sondern an einem einzigen, wachsenden Beispiel: der Software eines
Kursanbieters. Jedes Konzept taucht erst dann auf, wenn ein konkretes Problem
es verlangt. Zuerst das Problem, dann das Konzept.

### Was dieses Projekt bietet

Jedes Beispiel aus den beiden Beiträgen ist hier lauffähig umgesetzt und mit
Unit-Tests abgesichert. Die Tests sind dabei die eigentliche Dokumentation:
Ihre deutschen Anzeigenamen (`@DisplayName`) beschreiben, was ein Beispiel
zeigen soll – auch dort, wo ein Gegenbeispiel absichtlich einen Fehler enthält.
So lässt sich jedes Beispiel ausführen, verändern und an den Tests beobachten,
was sich dadurch ändert.

### Für wen

- **Einsteigerinnen und Einsteiger** nach einem Java-Einführungskurs finden in
  den Packages `procedural`, `course`, `invoice` und `payment` die Hauptlinie
  der Serie.
- **Fortgeschrittene** finden in den Packages `identity`, `lsp` und `sealed` die
  Vertiefungen aus den Exkursen: Identität und Gleichheit, das Liskovsche
  Substitutionsprinzip sowie Sealed Types mit Record Patterns.

### Empfohlenes Vorgehen

1. Einen Abschnitt im Blogbeitrag lesen.
2. Die zugehörigen Klassen und Tests ansehen (siehe Tabelle «Vom Blogbeitrag zum Code»).
3. `mvn test` ausführen, den Code verändern und die Tests erneut laufen lassen.
4. Zum Schluss die Übungen lösen.

## Voraussetzungen

- **JDK 25.** Der Code nutzt Anweisungen vor `super(...)` (JEP 513, ab Java 25)
  sowie Record Patterns mit dem Platzhalter `_` (ab Java 22). Mit einem älteren
  JDK bricht der Build ab.
- **Maven 3.9** oder neuer.

Prüfen mit:

```bash
java -version
mvn -version
```

## Schnellstart

```bash
mvn test                       # alle Tests
mvn test -Dtest=LiskovTest     # eine einzelne Testklasse
```

## Projektstruktur

```text
src/main/java/ch/rolfsblog/oop/
├── procedural/   ProceduralBooking              Ausgangslage mit losen Variablen
├── course/       Participant, Enrollment,       Objekt, Kapselung,
│                 PaymentStatus, CourseRun,      Vererbung, Komposition
│                 CourseOffering, ClassroomCourse,
│                 OnlineCourse, Program
├── identity/     Price                          Identität und Gleichheit
├── lsp/          Rectangle, Square              Liskovsches Substitutionsprinzip
├── invoice/      Invoice, TypeCheckingInvoice   Polymorphie vs. if-Kaskade
├── sealed/       CourseOffering, ClassroomCourse,
│                 OnlineCourse, Program,         Sealed Types, Records,
│                 PriceCalculator                Pattern Matching
└── payment/      PaymentMethod, PaymentResult,  Abstraktion
                  CardPayment, TwintPayment
src/test/java/ch/rolfsblog/oop/                  je Package eine oder mehrere Testklassen
```

## Vom Blogbeitrag zum Code

| Blogabschnitt | Package | Klassen | Test |
| --- | --- | --- | --- |
| Teil 1 · Ausgangslage | `procedural` | `ProceduralBooking` | `ProceduralBookingTest` |
| Teil 1 · Objekt und Klasse | `course` | `Enrollment`, `Participant` | `EnrollmentTest` |
| Teil 1 · Exkurs Identität und Gleichheit | `identity`, `course` | `Price`, `Participant` | `IdentityTest` |
| Teil 1 · Kapselung | `course` | `CourseRun`, `Enrollment` | `CourseRunTest`, `EnrollmentTest` |
| Teil 1 · Vererbung und Komposition | `course` | `CourseOffering`, `ClassroomCourse`, `OnlineCourse`, `Program` | `CourseOfferingTest` |
| Teil 1 · Exkurs Liskov | `lsp` | `Rectangle`, `Square` | `LiskovTest` |
| Teil 2 · Polymorphie | `invoice` | `Invoice`, `TypeCheckingInvoice` | `InvoiceTest` |
| Teil 2 · Exkurs Gegenentwurf in modernem Java | `sealed` | `CourseOffering`, Records, `PriceCalculator` | `PriceCalculatorTest` |
| Teil 2 · Abstraktion | `payment`, `course` | `PaymentMethod`, `CardPayment`, `TwintPayment`, `Enrollment.pay()` | `PaymentMethodTest`, `EnrollmentTest` |

Die Packages `course` und `sealed` enthalten bewusst gleichnamige Typen
(`CourseOffering`, `ClassroomCourse` …): Sie zeigen dasselbe Modell einmal mit
einer klassischen Vererbungshierarchie und einmal mit einem versiegelten
Interface und Records.

## Absichtlich eingebaute Fehler

Einige Klassen sind Gegenbeispiele. Ihre Fehler sind gewollt, und die Tests
halten genau dieses Verhalten fest:

| Klasse | Fehler | Gezeigt in |
| --- | --- | --- |
| `ProceduralBooking` | `book()` vergisst `freeSeats--`, der Kurs wird überbucht. | `ProceduralBookingTest` |
| `TypeCheckingInvoice` | Die `if`-Kaskade vergisst beim Lehrgang den Paketrabatt. | `InvoiceTest` |
| `Square` | Verletzt den Vertrag von `Rectangle.setWidth()`. | `LiskovTest` |
| `CardPayment`, `TwintPayment` | Nur Skizzen, sie werfen `UnsupportedOperationException`. | `PaymentMethodTest` |

## Ergänzungen gegenüber dem Blogtext

Damit der Code lauffähig und testbar ist, enthält er einige Details, die in
den Beiträgen nicht abgedruckt sind:

- `Enrollment.pay(PaymentMethod)` verbindet Anmeldung und Zahlung über die Abstraktion.
- `CourseRun.freeSeats()` leitet die freien Plätze aus der Liste ab, ohne separaten Zähler.
- `Participant` ist vollständig ausprogrammiert, mit Gleichheit über die ID.
- Die Kursangebote im Package `course` haben Zugriffsmethoden wie `days()` oder
  `flatPrice()`, die `TypeCheckingInvoice` für ihre Typprüfungen braucht.

## Übungen

Die Übungen aus den Beiträgen sind bewusst nicht gelöst. Zu jeder Übung gehört
mindestens ein neuer Test.

1. Ersetze die `IllegalStateException` in `CourseRun` durch eine fachliche `CourseFullyBookedException`.
2. Ergänze `CourseRun` um `cancel(Enrollment)`. Welche Regeln gehören in diese Methode?
3. Füge `ElearningSubscription` als neuen Angebotstyp hinzu – einmal im Package
   `course`, einmal im Package `sealed`. Was musst du jeweils ändern, und wo hilft dir der Compiler?
4. Baue `QrBillPayment`. Passt das synchrone `process()` noch, wenn die Zahlung erst Tage später eintrifft?

## Konventionen

- Java 25, keine Laufzeitabhängigkeiten. Für die Tests nur JUnit Jupiter.
- Englische Bezeichner und Javadoc-Kommentare, deutsche Testanzeigenamen.
- Kein Lombok, kein `var`.
