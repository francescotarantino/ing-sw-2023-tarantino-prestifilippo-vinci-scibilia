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
mvn clean package
```
Verranno generati due file jar nella cartella `target`:
- `myshelfie-client.jar` è il client, sia testuale che grafico (include anche le librerie di JavaFX).
- `myshelfie-server.jar` è il server.

>È possibile che la GUI di un client non venga avviata correttamente se il file `.jar` è stato compilato su una architettura di processore diversa da quella in cui viene eseguito.
Ciò avviene perché JavaFX utilizza librerie native e, al momento della compilazione, vengono incluse solo quelle per l'architettura di processore in uso.
In tal caso è necessario ricompilare il progetto sulla macchina in cui si vuole eseguire il client, per assicurarsi di includere le librerie native corrette.
Ciò non avviene se la JVM include già le librerie native di JavaFX.

## Esecuzione
### Server
Per lanciare il server è necessario eseguire il comando:
```bash
java -jar myshelfie-server.jar [socket port]
```
dove `socket port` specifica la porta su cui il server deve mettersi in ascolto per le connessioni Socket.
Se non specificata, la porta di default è la `12345`.
>Nel caso di mal funzionamento del protocollo RMI in remoto, può essere utile impostare la proprietà **`java.rmi.server.hostname`** per specificare l'indirizzo IP del server. Ad esempio:
>```bash
>java -Djava.rmi.server.hostname=xxx.xxx.xxx.xxx -jar myshelfie-server.jar
>```
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
