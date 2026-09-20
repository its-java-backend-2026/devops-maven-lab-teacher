# DevOps Maven Lab

Progetto didattico minimale per introdurre la Continuous Integration con Maven e GitHub Actions.

## Obiettivo

Il progetto calcola il totale di un ordine. L'esercizio consiste nell'aggiungere una regola di sconto:

> Se il totale supera 50 euro, applicare uno sconto del 10%.

Gli studenti devono modificare il codice, aggiungere un test, aprire una Pull Request e verificare la pipeline CI.

## Esecuzione locale

Richiede Java 21 e Maven 3.9+.

```bash
mvn clean test
mvn package
```

Il risultato della build è:

```text
target/devops-maven-lab-1.0.0-SNAPSHOT.jar
```

Per eseguire il programma dopo il package:

```bash
java -jar target/devops-maven-lab-1.0.0-SNAPSHOT.jar
```

## Laboratorio

1. Creare un branch, per esempio `feature/discount`.
2. Implementare lo sconto nel calcolatore.
3. Aggiungere almeno un test per la nuova regola.
4. Eseguire `mvn clean test` localmente.
5. Eseguire `mvn package` e osservare il JAR prodotto.
6. Fare push e aprire una Pull Request.
7. Leggere il risultato del workflow GitHub Actions.
8. Correggere eventuali errori e aggiornare la Pull Request.

## Le due pipeline

### Build Maven

Il workflow `Build Maven` esegue:

```text
checkout → Java 21 → test → package → upload JAR artifact
```

### Build Docker

Il workflow `Build Docker` esegue:

```text
checkout → docker build → immagine Docker
```

Il `Dockerfile` è multi-stage: compila e testa il progetto nello stage Maven, poi copia il JAR in un'immagine runtime Java più piccola.

La pipeline costruisce l'immagine ma non la pubblica su un registry e non esegue alcun deploy. Render sarà un'attività separata.
