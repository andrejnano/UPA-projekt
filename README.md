# UPA-projekt
Projekt UPA - FIT VUT BRNO

## Requirements

- Java JDK 13
- [Java FX 13](https://openjfx.io/)
- Oracle JDBC
- Oracle Spatial

## Project setup

1. Install **Java 13**.
2. Install [**JavaFX SDK 13**](https://gluonhq.com/products/javafx/) to `~/java/java-sdk-13/`.
3. Install Oracle JDBC to `~/java/jdbc/`.
4. Install Oracle Spatial to `~/java/spatial/`.
5. `git clone https://github.com/andrejnano/UPA-projekt`
6. [Setup IntelliJ](https://openjfx.io/openjfx-docs/) with this project.

## Project structure

- [`src/`](src/) &mdash; *Application source code*
  - [`controllers/`](src/controllers/) &mdash; *Controllers/Handlers/Event binding/...*
  - [`models/`](src/models/) &mdash; *App data model, DB setup & connection, data binding,...*
  - [`views/`](src/views/) &mdash; *GUI components/FXML*
  - [`App.js`](src/App.js) &mdash; *Initial starting point for the whole app*
- [`init.sql`](init.sql) &mdash; *Initial SQL script for DB scheme setup/reset*

## O projekte

Zoznam členov:

1.	Marko Peter (**xmarko15**)
2.	Naňo Andrej (**xnanoa00**)
3.	Vašek Dominik	(**xvasek06**)

## Cíle projektu
Cílem projektu je vše z následujícího:

- vhodně **navrhnout schéma databáze** pro Vaši aplikaci (vizte témata projektů) s využitím prvků prostorových a multimediálních databází,
- databázové **schéma implementovat** pro databázový server Oracle 12c a vyšší,
- **vytvořit aplikaci** pro práci s implementovanou databází, kde bude využívat příkazy a dotazy pro práci s prostorovými a multimediálními daty,
- **předvést databázi a aplikaci** na závěrečné obhajobě projektu a prokázat, že jsou splněny body zadání.

## Body zadání

- **Multimediální databáze**:
  - Operace: 
    - [ ] *vkládání*
    - [ ] *mazání*
    - [ ] *úprava multimédií* (alespoň statických obrázků, kde operace úprava může být např. jejich rotace)
    - [ ] jejich *vyhledávání* dle multimediálního obsahu.
- **Prostorové databáze**:
  - Operace: 
    - [ ] *vkládání*
    - [ ] *mazání*
    - [ ] *změna* uživatelských prostorových dat 
    - pro alespoň *4 druhů geometrických entit* (různé hodnoty SDO_GTYPE v SDO_GEOMETRY), a to interaktivní formou (např. změna souřadnic a velikosti entit tažením kurzoru na mapě)
  - netriviální použití alespoň jednoho *prostorového operátoru* (výsledkem je geometrie, využívá index),
  - dalších 2 prostorových operátorů (opět geometrie s indexem)
  - nebo funkcí (výsledkem je geometrie, ale index není potřeba)
  - a alespoň 3 analytických funkcí nebo operací  (výsledkem je číslo, např. obvod geometrie) demonstrujících práci nad prostorovými daty (pro příklady vizte demonstrační cvičení).
- **Obecně k aplikaci**:
  - GUI: 
    - [ ] použitelná nápověda
    - [ ] běžící spolehlivě na platformách Linux nebo Windows
    - [ ] **volba přihlašovacích údajů** pro připojení k databázovému serveru
    - [ ] (znovu-)naplnění tamní databáze tabulkami se sadou ukázkových dat (implementováno např. formou s aplikací dodávaného inicializačního skriptu, jehož příkazy aplikace na požádání přečte a odešle do připojené databáze),
- **Způsob práce na projektu**:
  - hlavní větev repositáře (master) musí při dokončení projektu obsahovat vše odevzdané (odevzdá se archiv s jejím obsahem nahráním do WISu, tj. '''repositář pro odevzdání nestačí, je nutné výsledek nahrát do WISu!'''), včetně instrukcí pro sestavení (build) a zprovozněné (deploy) výsledné aplikace vč. závislostí (např. použitím vhodného build nástroje, jako je Gradle, Maven, atp., a popisem v souboru README),
  - repositář bude použitelný také pro kontroly množství a kvality práce odvedené jednotlivými členy týmu.
