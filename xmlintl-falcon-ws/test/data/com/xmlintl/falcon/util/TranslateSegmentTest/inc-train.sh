#/bin/sh
MTPATH=/Users/andrzejzydron/Documents/Java/XTM/xmlintl-falcon-ws/test/data/com/xmlintl/falcon/util/ListEnginesTest
ENGINEPATH=$MTPATH/engines/$4
echo $MTPATH
echo $ENGINEPATH
if [ -z $1 ]
then
        echo "Usage: `basename $0` <src_lang> <tgt_lang> <train_filename> <engine_name>"
        echo "File name must be without language extension"
        exit 1
fi

rm -rf  $ENGINEPATH/new_data $ENGINEPATH/aligned_data
mkdir $ENGINEPATH/new_data
echo cp $3.$1 $ENGINEPATH/new_data/
cp $3.$1 $ENGINEPATH/new_data/  
echo cp $3.$2 $ENGINEPATH/new_data/
cp $3.$2 $ENGINEPATH/new_data/  	

mkdir $ENGINEPATH/aligned_data
cp $ENGINEPATH/mtdata/train.clean.$1 $ENGINEPATH/aligned_data/train.clean.$1
cp $ENGINEPATH/mtdata/train.clean.$2 $ENGINEPATH/aligned_data/train.clean.$2
cp $ENGINEPATH/train/model/aligned.grow-diag-final $ENGINEPATH/aligned_data/

