# AttendanceManager-android-
Attendance Manager Android

## Synopsis

Desenvoluparem una aplicació AM Android per a dispositius Android.
Attendance Manager consisteix en una aplicació per facilitar als professors passar llista a classe,
mitjançant els seus telefons mòvils.

Utilitzarem l'arquitectura REST mitjançant un servlet especificament creat, que s'executa
al servidor d'aplicacions, aprofitant les funcionalitats ja implementades per l'aplicació del
servidor, concretament les classes de bases de dades. Implementarem un client REST a
l'aplicació Android que enviarà les peticiones al servlet que retornarà les respostes en
format JSON.

## Motivation

Project de final de curs de Desenvolupament de Aplicacions Mulitimedia, on es possen a prova els
coneixements adquirits durant el curs.

## Installation

Per fer la instalació del Servlet, haurem de tenir instalada una versio de AttendanceManager proporcionada
previament i la base de dades, per a que el servlet ens reconegui la base de dades, em de fer algun petit camvi
a aquesta versio de AttendanceManager.
Primer haurem de cambiar al arxiu AttendanceManager.properties l'apartat "mysql_database" i "mysql_datasource"
y posar el nom de la nostre base de dades.
El següent arxiu es el "Context.xml" que es troba a "src/main/webapp/META-INF" haurem de modificar el "username" i
"password" pero el nom i password que tenim a la nostre base de dades, a "name" i "url" hauriem de posar el nom
de la nostre base de dades.

Per fer la instalació haurem de clonar aquest repositori, i util-litzar Eclipse IDE, un cop fet aixó
podrem instal-lar l'aplicació al nostre terminal movil, aixo generara una APK per la seva distribució.

https://github.com/switcheroo1/AttendanceManager-android-/blob/master/Atm/bin/Atm.apk

![alt tag](https://github.com/switcheroo1/AttendanceManager-android-/blob/master/Atm/docs/qrplanet.png)

## API Reference

Android 4.4.2 Andorid Open Source Project 4.4.2 API level 19
Minimun SDK version API level 16. 
Target SDK version API level 19.

## License

GPL3.
