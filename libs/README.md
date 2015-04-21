# HOW TO LOAD LIBRAIRIES IN MAVEN PROJECT ? (Comment charger les librairies externes dans un projet Maven ?)


Pour charger des librairies ne pouvant être accessible par des dépendances maven, on doit utiliser la commande install:install-file, par exemple :

"""bash
install:install-file -Dfile=turtlekit-3.0.0.4c.jar -DgroupId=edu -DartifactId=TurtleKit -Dversion=3.0.0.4c -Dpackaging=jar
"""



