# Prova Finale - Ingegneria del Software - A.A. 2022/23
![Logo](src/main/resources/images/icons/title.png)

### Gruppo GC02
- [Francesco Tarantino](https://github.com/francescotarantino)
- [Carlo Prestifilippo](https://github.com/carloprestifilippo)
- [Davide Vinci](https://github.com/Darco0)
- [Alessandro Scibilia](https://github.com/tw-fl)

## Funzionalità
- Regole complete
- TUI
- GUI 
- RMI
- Socket
- 2 funzionalità avanzate 
  - Partite multiple 
  - Resilienza alle disconnessioni

## Compilazione
La compilazione avviene tramite Maven.
```bash
mvn package
```
Verranno generati due file jar nella cartella `target`:
- `myshelfie-client.jar` è il client, sia testuale che grafico (include anche le librerie di JavaFX).
- `myshelfie-server.jar` è il server.

>È possibile che la GUI di un client non venga avviata correttamente se il file `.jar` è stato compilato su una architettura di processore diversa da quella in cui viene eseguito.
Ciò avviene perché JavaFX utilizza librerie native e, al momento della compilazione, vengono incluse solo quelle per l'architettura di processore in uso.
In tal caso è necessario ricompilare il progetto sulla macchina in cui si vuole eseguire il client, per assicurarsi di includere le librerie native corrette.
Ciò non avviene se la JVM installata include già le librerie native di JavaFX.

## Esecuzione
### Server
Per lanciare il server è necessario eseguire il comando:
```bash
java -jar myshelfie-server.jar [server ip] [socket port]
```
dove
- `server ip` specifica l'indirizzo IP del server,
- `socket port` specifica la porta su cui il server deve mettersi in ascolto per le connessioni Socket.
Se non specificata, la porta di default è la `12345`.
>L'indirizzo IP del server è richiesto per il funzionamento del protocollo RMI in remoto.
### Client
Per lanciare un client è necessario eseguire il comando:
```bash
java -jar myshelfie-client.jar [RMI|SOCKET] [ip] [port]
```
dove:
- `RMI|SOCKET` specifica il protocollo di comunicazione da utilizzare.
- `ip` specifica l'indirizzo IP del server.
- `port` specifica la porta del server.

I parametri sono opzionali. Se non specificati, verranno chiesti all'utente prima dell'avvio.

## Test Coverage
![Coverage Report](deliverables/Test%20Coverage%20Report.png)

## UML Diagrams
- Class Diagrams
  - [Model/Controller](deliverables/uml/UML%20Class%20Diagram%20-%20Model%20Controller.pdf)
  - [Network](deliverables/uml/UML%20Class%20Diagram%20-%20Network.pdf)
  - [Detailed](deliverables/uml/UML%20Class%20Diagram%20-%20Detailed.png)
- Sequence Diagrams
  - [RMI](deliverables/uml/UML%20Sequence%20Diagram%20-%20RMI.pdf)
  - [SOCKET](deliverables/uml/UML%20Sequence%20Diagram%20-%20SOCKET.pdf)

## DISCLAIMER
NOTA: My Shelfie è un gioco da tavolo sviluppato ed edito da Cranio Creations Srl. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.
