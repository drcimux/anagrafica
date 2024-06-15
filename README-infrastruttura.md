# Infrastruttura AWS per lo Sviluppo del Progetto

Per sviluppare l'esercizio proposto, concentreremo l'attenzione sull'infrastruttura necessaria, utilizzando AWS come piattaforma cloud. Ecco come procedere:

## Amazon RDS (Relational Database Service):

Utilizzeremo Amazon RDS per ospitare il database relazionale. È possibile scegliere  PostgreSQL, o un altro DBMS supportato da AWS. 
Creeremo un'istanza RDS con le seguenti caratteristiche:

- **Motore del database**: PostgreSQL
- **Dimensioni e configurazioni delle istanze**: Configurate in base ai requisiti.
## Amazon S3 (Simple Storage Service):

Amazon S3 sarà utilizzato per memorizzare i file CSV che verranno caricati tramite le API REST. Creeremo un bucket S3 dedicato per questo scopo, impostando le politiche di accesso necessarie per garantire la sicurezza e la disponibilità dei dati.

## Amazon EC2 (Elastic Compute Cloud) o AWS Fargate:

Utilizzeremo EC2 o Fargate per ospitare l'applicazione Spring Boot. EC2 offre maggiore controllo sull'ambiente di esecuzione, mentre Fargate semplifica la gestione dell'infrastruttura.
