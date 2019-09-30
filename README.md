# UPA-projekt
Projekt UPA - FIT VUT BRNO

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

## Témata projektů
Projekty nejsou tématicky nijak omezeny. Můžete si tedy zvolit jakékoliv téma, při kterém vytvořením odpovídající aplikace prokážete splnění všech bodů zadání.

Příkladem mohou být aplikace pro cestovní nebo realitní kanceláře, systémy zahrnující skladovou evidenci, evidenci zboží a obchodních transakcí, personální evidenci vč. evidence oprávnění a zodpovědností, nebo GIS pro IZS, státní správu či energetické společnosti.

Pokud si nejste tématem jistí, je doporučeno konzultovat s některým z vyučujících, nejlépe s vyučujícím zodpovědným za projekty.


## Body zadání

Body zadání, které musí databáze a aplikace splňovat, jsou:

- **Multimediální databáze**:
  - Operace vkládání, mazání a úprava multimédií (alespoň statických obrázků, kde operace úprava může být např. jejich rotace)
  - a jejich vyhledávání dle multimediálního obsahu.
- **Prostorové databáze**:
  - Operace vkládání, mazání a změna uživatelských prostorových dat pro alespoň 4 druhů geometrických entit (různé hodnoty SDO_GTYPE v SDO_GEOMETRY),
  - a to interaktivní formou (např. změna souřadnic a velikosti entit tažením kurzoru na mapě)
  - a dále netriviální použití alespoň jednoho prostorového operátoru (výsledkem je geometrie, využívá index),
  - dalších 2 prostorových operátorů (opět geometrie s indexem)
  - nebo funkcí (výsledkem je geometrie, ale index není potřeba)
  - a alespoň 3 analytických funkcí nebo operací  (výsledkem je číslo, např. obvod geometrie) demonstrujících práci nad prostorovými daty (pro příklady vizte demonstrační cvičení).
- **Obecně k databázi*:
  - databáze (schéma a data) musí běžet na databázovém serveru Oracle 12c a vyšší s tím, že jako referenční platforma slouží fakultní databázový server Oracle určený pro tento předmět.
- **Obecně k aplikaci*:
  - aplikace s grafickým uživatelským rozhraním (použitelná nápověda, intuitivní ovládání) běžící spolehlivě na platformách Linux nebo Windows, která se připojuje k databázovému serveru a využívá funkcí prostorových a multimediálních databází včetně propojení s relačními daty,
  - aplikace musí umožňovat volbu přihlašovacích údajů pro připojení k databázovému serveru a (znovu-)naplnění tamní databáze tabulkami se sadou ukázkových dat (implementováno např. formou s aplikací dodávaného inicializačního skriptu, jehož příkazy aplikace na požádání přečte a odešle do připojené databáze),
  - preferovaný implementační jazyk je Java, ale je možné zvolit i jiný, avšak ověřte si, že má podporu pro připojení k databázi Oracle nejlépe i s podporou jejích multimediálních a prostorových rozšíření,
  - využití pouze výtvorů a knihoven jejichž jste autorem nebo jsou běžně dostupné se základní distribucí implementačního jazyka (pro Javu tedy Java SE/EE knihovny, dostupné např. s JDK 8 a Java EE), s distribucí použitého vývojového prostředí (IntelliJ, jDeveloper, NetBeans, Eclipse) a knihovny Oracle (ze stránek nebo cvičení); použití cizích zdrojových kódů (i částí) a dalších knihoven vyžaduje schválení (žádost emailem cvičícímu s odkazem na knihovnu a zdůvodněním plánovaného použití).
- **Způsob práce na projektu**:
  - je vyžadováno použití Git repositáře, tj. systému správy verzí zdrojového kódu, pro SQL kód databázového schéma i zdrojový kód aplikace,
  - repositář projektu musí vždy obsahovat veškerá data aktuálního stavu řešení a jednotlivé verze (tj. commits) musí být odeslány odpovědným členem týmu (tj. autorem změn) a musí obsahovat stručný popis změn (tj. neměl by být prováděn commit bez komentáře)
  - repositář musí být přístupný pouze členům příslušného týmu a vyučujícím (uživatelské jméno "marek-rychly"), nikomu jinému (jedná se tedy o privátní repositář, např. na [https://gitlab.com/ GitLab], kde se lze přihlásit i přes [https://www.vutbr.cz/intra/navody/cloudy/gsuite VUT Google APPS] účet, nebo [https://github.com/ GitHub] s možností privátních repositářů [https://education.github.com/students pro studenty zdarma]).
  - v hlavní větvi repositáře (tj. většinou master) je zakázáno přepisovat historii (např. dělat rebase a přepisovat commits pomoci force push), tj. tato větev musí být chráněná (protected), což je výchozí nastavení většiny Git serverů; pro průběžné verze (commits) s možností jejich změn, rebase, atp., je doporučeno používat jiné (samostatné) větve a výsledky do hlavní větve začleňovat jednorázově (tj. merge) 
  - hlavní větev repositáře (master) musí při dokončení projektu obsahovat vše odevzdané (odevzdá se archiv s jejím obsahem nahráním do WISu, tj. '''repositář pro odevzdání nestačí, je nutné výsledek nahrát do WISu!'''), včetně instrukcí pro sestavení (build) a zprovozněné (deploy) výsledné aplikace vč. závislostí (např. použitím vhodného build nástroje, jako je Gradle, Maven, atp., a popisem v souboru README),
  - repositář bude použitelný také pro kontroly množství a kvality práce odvedené jednotlivými členy týmu.

## Postup a odevzdávané výsledky
1. tým zaregistrovat do WISu
2. vytvořit Git repositář, přidat členy týmu a vyučujícího, odkaz na repositář (vč. seznamu fakultních loginů všech členů týmu) odeslat na email vyučujícího zodpovědného za projekty
3. vytvořit databázové schéma a aplikaci
4. archiv hlavní větve repositáře se zdrojovými kódy a postupy pro sestavení a nasazení odeslat do WISu jako finální výsledek projektu
5. prezentovat výsledky na závěrečné obhajobě
