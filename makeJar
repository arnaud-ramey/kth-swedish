#!/bin/sh
# Se placer dans le repertoire pere de "src" et "bin"

# Creation du certificat (clef) :
#keytool -genkey -keystore ArnaudRamey -alias arnaudr

#Creation du fichier jar :
rm asker.jar
jar cf asker.jar ./*

# Signature du jar :
jarsigner -keystore ArnaudRamey asker.jar arnaudr

firefox Asker.html

##### googlecode
### project name : kth-swedish
### code : PU5Qg7Ap9Hh7
### first time ###
#svn checkout https://kth-swedish.googlecode.com/svn/trunk/ . --username arnaud.a.ramey
# svn add asker.rar
# svn add Asker.html
### upload ###
#svn commit -m 'upload' 
