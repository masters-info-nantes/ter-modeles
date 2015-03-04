#ter-modeles

##Sujet
Le sujet de recherche vise à trouver une manière d'interfacer des modèles différents (automates, réseau de Pétri, ect.) et les faire intéragir ensembles.

##Projet
Le but du projet et de pouvoir importer des automates au format [DOT](http://fr.wikipedia.org/wiki/DOT_(langage)) dans le logiciel [UPPAAL](http://www.uppaal.org) qui permet de composer des automates. 

##Utilisation
Compiler, tester et executer avec maven

* Avec eclipse

```
$ mvn eclipse:eclipse
```

* Dans un shell

```
$ mvn compile
$ mvn test # Optionel: pour lancer les tests
$ mvn exec:java

Avoid maven messages: 
$ mvn exec:java | grep -Ev "(\[INFO\])|(\[WARNING\])" 
```

##Liens
* Projet sur [CIEforge](https://cieforge.univ-nantes.fr/projects/hetersys/) qui contient le forum et des documents de référence.
* [Diagramme de Gantt](https://drive.google.com/open?id=0B-uNQeLFVbHVdDFldXFaVDJ0T28&authuser=0) hébergé sur google drive
* [TODO list](https://github.com/masters-info-nantes/ter-modeles/blob/master/TODO.md)

##Infos
**La structure de graphe est tirée du projet [control-flow-graph](https://github.com/masters-info-nantes/control-flow-graph) auquel un d'entre nous a participé. Elle a subi quelques modifications pour s'adapter à notre projet**

##Auteurs
* Antoine Forgerou
* Jérémy Bardon
* Nicolas Bourdin
