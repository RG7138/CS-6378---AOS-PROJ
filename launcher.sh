#!/bin/bash

PROG=Main
NETID=$2
PROGRAM_PATH=$(pwd)

#rm -rf *.class *.out
find . -type f -name '*.class' -exec rm -rf {} \;
find . -type f -name '*.out' -exec rm -rf {} \;
echo $PROGRAM_PATH
#exit 0
javac $PROGRAM_PATH/src/MessagePackage/MessageStructure.java				
echo MESSAGE HELPER STUFF COMPILED
#javac $PROGRAM_PATH/src/Main.java
cd $PROGRAM_PATH/src ; javac Main.java ; cd ..
echo MAIN COMPILED SUCCESSFULY


#find . -type f -name '*.java' -exec javac {} \;

cat $1 | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i  
    totalNodes=$( echo $i | awk '{ print $1 }' )	
    
    for ((a=1; a <= $totalNodes ; a++))
    do
    	read line 
		nodeId=$( echo $line | awk '{ print $1 }' )
       	host=$( echo $line | awk '{ print $2 }' )
		ssh -o StrictHostKeyChecking=no -l "$NETID" "$host" "cd $PROGRAM_PATH/src;java $PROG $nodeId $1" &
		echo Started : $nodeId - $host
    done 
)



