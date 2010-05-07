### a script to compile the program
CLASSPATH=
for x in $(find | grep jar$); do
    CLASSPATH="$CLASSPATH:$x"; 
done
SRC=$(find | grep java$)
javac -cp "$CLASSPATH" $SRC
