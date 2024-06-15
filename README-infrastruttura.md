# Infrastruttura AWS per lo Sviluppo del Progetto

Per sviluppare l'esercizio proposto, concentreremo l'attenzione sull'infrastruttura necessaria, utilizzando AWS come piattaforma cloud. Ecco come procedere:

## Amazon RDS (Relational Database Service):

Utilizzeremo Amazon RDS per ospitare il database relazionale. È possibile scegliere tra MySQL, PostgreSQL, o un altro DBMS supportato da AWS, escludendo Microsoft SQL Server come specificato. Creeremo un'istanza RDS con le seguenti caratteristiche:

- **Motore del database**: MySQL o PostgreSQL (scelta libera)
- **Dimensioni e configurazioni delle istanze**: Configurate in base ai requisiti (ad esempio, db.t3.small per iniziare)

## Amazon S3 (Simple Storage Service):

Amazon S3 sarà utilizzato per memorizzare i file CSV che verranno caricati tramite le API REST. Creeremo un bucket S3 dedicato per questo scopo, impostando le politiche di accesso necessarie per garantire la sicurezza e la disponibilità dei dati.
