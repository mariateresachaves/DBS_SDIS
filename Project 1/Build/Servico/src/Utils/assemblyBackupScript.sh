#/bin/bash


#USAGE: script <inputFolder> <outputFile>

FOLDER=$1
FILE=$2

echo $FOLDER
echo $FILE
if [ ! -d $Folder ]
then
echo "[-] Not a Folder FOLDER"
echo "[!] Exiting"
exit 1
fi


for f in $FOLDER/* 
do
 echo "Processing $f"
 # do something on $f
 xxd -r -p $f>> $2 
done
exit
