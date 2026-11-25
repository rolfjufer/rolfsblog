# SAML oder OpenID Connect?

Code zum Blogbeitrag «SAML oder OpenID Connect?» auf rolfsblog.ch:

<https://rolfsblog.ch/saml-oder-openid-connect/>

Eine Spring-Boot-Anwendung, zwei Profile: `oidc` und `saml`. Controller, Templates und
Zugriffsregeln sind identisch, nur die Security-Konfiguration wechselt.

| Profil | Protokoll | Nachweis | Spring-DSL |
| --- | --- | --- | --- |
| `oidc` | OpenID Connect (Authorization Code Flow) | ID Token (JWT) | `oauth2Login()` |
| `saml` | SAML 2.0 | signierte Antwort mit Assertion (XML) | `saml2Login()` |

## Voraussetzungen

- JDK 25
- Maven 3.9 oder neuer
- Docker mit Compose v2 und `openssl`, um die Anwendung mit Keycloak zu starten

Verwendet werden Spring Boot 4.1.3, Spring Security 7.1.3 und Keycloak 26.8.1. OpenSAML 5 lädt Maven aus
dem Repository des Shibboleth-Projekts, weil es nicht auf Maven Central liegt.

## Bauen und testen

```bash
mvn verify
```

Die Tests brauchen weder Docker noch Schlüssel: Sie spielen den Identity Provider selbst und
signieren ID Tokens und SAML-Antworten mit Schlüsseln, welche pro Testlauf entstehen.

## Starten

```bash
# 1. Schlüsselpaar für die SAML-Signaturen erzeugen (einmalig)
./generate-keys.sh

# 2. Keycloak mit Realm "demo", Clients und Benutzern starten, etwa 30 Sekunden warten
docker compose up -d

# 3. Anwendung starten: oidc oder saml
mvn spring-boot:run -Dspring-boot.run.profiles=oidc
```

Danach <http://localhost:8081> öffnen. Die Admin-Konsole von Keycloak liegt unter
<http://localhost:8080/admin> (`admin` / `admin`).

| Benutzer | Passwort | Gruppe | `/dozierende` |
| --- | --- | --- | --- |
| `jane` | `jane` | dozierende | erlaubt |
| `max` | `max` | studierende | 403: angemeldet, aber nicht berechtigt |

## Vom Blogbeitrag zum Code

| Abschnitt im Beitrag | Code | Test |
| --- | --- | --- |
| Variante A: OpenID Connect | `OidcSecurityConfig`, `application-oidc.yml` | `OidcLoginFlowTest` |
| Variante B: SAML 2.0 | `SamlSecurityConfig`, `application-saml.yml` | `SamlLoginFlowTest` |
| Gruppen auf Rollen abbilden | `OidcGroupAuthoritiesMapper`, `SamlGroupAuthoritiesConverter`, `GroupRoles` | `unknownGroupsAreIgnored` in beiden Tests |
| Die Zugriffsregel | `AccessRules`, `/dozierende` | `lecturerMayEnter`, `studentIsForbidden` |
| Welcher Nachweis ist für wen bestimmt? | `IdentityView`, `/me`, `/api/me` | `IdentityViewTest` |
| Gruppen-Mapper in Keycloak | `keycloak/import/realm-demo.json` | – |

Welches Protokoll aktiv ist, bestimmt die Property `app.sso.protocol`, welche das Profil setzt. Genau
eine der beiden Konfigurationsklassen ist über `@ConditionalOnProperty` aktiv.

## Endpunkte

| Pfad | Zugriff | Zweck |
| --- | --- | --- |
| `/` | öffentlich | Startseite, zeigt das aktive Protokoll |
| `/me`, `/api/me` | angemeldet | Claims bzw. Attribute als Tabelle und als JSON |
| `/dozierende` | Rolle `DOZIERENDE` | Zugriffsregel, für beide Protokolle gleich |
| `/logout` (POST) | angemeldet | beendet auch die Session bei Keycloak |
| `/login/oauth2/code/keycloak` | – | Redirect URI (OIDC) |
| `/login/saml2/sso/keycloak` | – | Assertion Consumer Service (SAML) |
| `/saml2/metadata/keycloak` | öffentlich | Metadaten der Anwendung (SAML) |

## Übungen

1. **Reines OAuth statt OIDC.** Entferne in `application-oidc.yml` den Scope `openid`. Warum scheitert
   der Login mit Keycloak? (Tipp: `logging.level.org.springframework.security=DEBUG`.) Was würde ein
   Provider bewirken, welcher den UserInfo-Aufruf trotzdem zulässt?
2. **Abgelaufenes Zertifikat.** Führe `CERT_DAYS=1 ./generate-keys.sh` und
   `docker compose up -d --force-recreate` aus. Woran erkennst du im Betrieb rechtzeitig, dass ein
   Zertifikat abläuft?
3. **Neuer Schlüssel nur auf einer Seite.** Führe `./generate-keys.sh` aus und starte nur die Anwendung
   neu. Was meldet Keycloak, und wo steht der Grund?
4. **Ungenaue Redirect URI.** Setze in Keycloak die *Valid redirect URI* des Clients `saml-vs-oidc` auf
   `http://localhost:8081/*`. Warum funktioniert der Login weiterhin, und warum ist das trotzdem
   gefährlich?
5. **Front Channel und Back Channel.** Schneide beide Logins mit
   [SAML-tracer](https://addons.mozilla.org/firefox/addon/saml-tracer/) mit. In welchem Fall sieht der
   Browser den Nachweis selbst?
6. **`SameSite`.** Setze `server.servlet.session.cookie.same-site: lax` und öffne die Anwendung mit dem
   Profil `saml` unter <http://127.0.0.1:8081> statt `localhost`. Welche Adressen musst du in Keycloak
   zusätzlich eintragen, und warum scheitert der Login danach trotzdem?
7. **Nicht angeforderte Assertion.** `saml2Login()` nimmt Assertions ohne `InResponseTo` an. Richte in
   Keycloak beim SAML-Client einen *IDP-Initiated SSO URL name* ein und probiere es aus. Wie weist man
   solche Assertions ab?

## Fehlersuche

| Symptom | Ursache |
| --- | --- |
| `run ./generate-keys.sh first` | `.env` mit dem Zertifikat fehlt. |
| Anwendung startet nicht (`oidc`) | Keycloak ist noch nicht bereit; `issuer-uri` wird beim Start gelesen. |
| Anwendung startet nicht (`saml`) | Keycloak ist noch nicht bereit, oder `keys/` fehlt. |
| Keycloak: «Invalid requester» (SAML) | Zertifikat in Keycloak und Schlüssel der Anwendung passen nicht zusammen: `docker compose up -d --force-recreate`. |
| Keycloak: «Invalid parameter: redirect_uri» | Port oder Pfad weicht von der registrierten Adresse ab. |
| «Invalid credentials» nach dem Login bei Keycloak | Die Anwendung lehnt die Antwort ab. Grund mit `logging.level.org.springframework.security=TRACE`. |
| 401 auf `/logout/saml2/slo` | `singlelogout` fehlt in der Registrierung. |

## Sicherheitshinweis

Client Secret, Passwörter und Keycloak im Entwicklungsmodus sind nur für diese Demo gedacht. Das
SAML-Schlüsselpaar entsteht lokal und ist von Git ausgeschlossen. Die Anwendung nimmt
unaufgefordert gesendete Assertions an (Übung 7); in einem echten Projekt gehört diese Prüfung dazu.
