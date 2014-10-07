#!/bin/sh
# shell script to run decoding for a single segment
#
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/gcc-4.8.2/lib/../lib64:/usr/local/lib:

#set -x


MPATH=/Users/andrzejzydron/Documents/Java/XTM/xmlintl-falcon-ws/test/data/com/xmlintl/falcon/util/ListEnginesTest/

#echo $MPATH

if [ -z $1 ]
then
	echo "Usage: `basename $0` <engine_name> <source_segment> <UUID>"
	exit 1
fi

#########################################################################################
####Manually Change the mose.ini file and place the same in binarised-model directory####
#### before running the decoder##########################################################
#########################################################################################

if [ ! -d $MPATH/engines/$1/logs ]
then
    mkdir -p $MPATH/engines/$1/logs
fi

if [ ! -d $MPATH/engines/$1/tmp ]
then
    mkdir -p $MPATH/engines/$1/tmp
fi

#OUTPUT=$MPATH/engines/$1/tmp/HelloWorld

OUTPUT=$MPATH/$1/tmp/HelloWorld

#echo $OUTPUT
#REF_FILE=$MPATH/engines/$1/tmp/$3.bleu

#ERROR_LOG=$MPATH/engines/$1/logs/$3
#BLEU_EVAL=$OUTPUT.bleu

#echo $MPATH/bin/mosesdecoder/bin/moses -f $MPATH/engines/$1/moses.ini $2 \>$OUTPUT 2\>\>$ERROR_LOG

##nohup nice $MPATH/bin/mosesdecoder/bin/moses -f $MPATH/engines/$1/moses.ini $2 >$OUTPUT 2>>$ERROR_LOG
#echo $2 | $MPATH/bin/mosesdecoder/bin/moses -f $MPATH/engines/$1/moses.ini >$OUTPUT 2>>$ERROR_LOG

#echo cp $OUTPUT $REF_FILE

#cp $OUTPUT $REF_FILE

#Evaluate translation accuracy
#nohup nice $MPATH/bin/mosesdecoder/scripts/generic/multi-bleu.perl -lc $REF_FILE <$OUTPUT >>$BLEU_EVAL

echo OUTPUT: `cat $OUTPUT` 
#cat $OUTPUT
echo
#echo -n BLEU: 
#cat $BLEU_EVAL
#echo
