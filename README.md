# rolfsblog – Code zu den Blogbeiträgen

Dieses Repository sammelt die Codebeispiele zu den Beiträgen auf
[rolfsblog.ch](https://rolfsblog.ch/). Jeder Beitrag – oder jede Serie von
Beiträgen – hat ein eigenes Verzeichnis mit einem eigenständigen, lauffähigen
Projekt samt Tests und einem README, das auf den Beitrag verweist.

## Beiträge

<!-- POSTS:START -->
| Verzeichnis | Beitrag | Technik |
| --- | --- | --- |
| [`2026-11-oop-kein-selbstzweck`](2026-11-oop-kein-selbstzweck/) | Objektorientierte Programmierung ist kein Selbstzweck – [Teil 1](https://rolfsblog.ch/objektorientierte-programmierung-ist-kein-selbstzweck-1-2/), [Teil 2](https://rolfsblog.ch/objektorientierte-programmierung-ist-kein-selbstzweck-2-2/) | Java 25, Maven, JUnit |
| [`2026-11-saml-oder-openid-connect`](2026-11-saml-oder-openid-connect/) | [SAML oder OpenID Connect?](https://rolfsblog.ch/saml-oder-openid-connect/) | Java 25, Spring Boot 4, Spring Security 7, Keycloak, Maven |
<!-- POSTS:END -->

## Aufbau

```text
rolfsblog/
├── README.md                    diese Übersicht
├── new-post.sh                  legt ein Verzeichnis für einen neuen Beitrag an
├── _template/                   Vorlage für neue Beiträge (Maven, JUnit)
├── .github/workflows/build.yml  baut und testet alle Projekte bei jedem Push
└── JJJJ-MM-kurzname/            ein Verzeichnis pro Beitrag oder Serie
    ├── README.md                Links zum Beitrag, Zuordnung Abschnitt → Code
    ├── pom.xml
    └── src/
```

## Konventionen

- **Verzeichnisname:** `JJJJ-MM-kurzname`, also Erscheinungsmonat und ein kurzer
  Name in Kleinbuchstaben, zum Beispiel `2026-11-oop-kein-selbstzweck`. So sind
  die Beiträge auf GitHub chronologisch sortiert.
- **Eigenständig:** Jedes Verzeichnis baut für sich allein, mit eigenem
  `pom.xml`. Es gibt kein gemeinsames Parent-Projekt, damit ältere Beispiele
  nicht brechen, wenn neue Beiträge neuere Versionen verwenden.
- **README pro Beitrag:** mit vollständigen Links zum Beitrag und einer Tabelle,
  welcher Abschnitt im Beitrag zu welchem Code gehört.
- **Code:** englische Bezeichner und Javadoc-Kommentare, kein Lombok, kein `var`.
- **Tests:** Jedes Beispiel ist mit Tests abgesichert. Die GitHub Action baut bei
  jedem Push alle Verzeichnisse mit einem `pom.xml`.

## Neuen Beitrag anlegen

```bash
./new-post.sh 2027-01-spring-modulith-events spring-modulith-events \
    "Events in Spring Modulith" \
    https://rolfsblog.ch/events-in-spring-modulith/
```

Das Skript kopiert `_template/` in das neue Verzeichnis, setzt Titel, Link und
Artifact-ID ein und trägt den Beitrag in die Tabelle oben ein.

## Bauen und testen

```bash
cd 2026-11-oop-kein-selbstzweck
mvn test
```
