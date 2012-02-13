#!/bin/bash 

#DROPDIR=/data/1.XLive/LiveTOLFILES
DROPDIR=/home/dmandel/devel
DROPFILE=tol-all-content-nc.xml
ZIPFILE=tol-all-content-nc.tar.gz

E_XCD=66 # can't change directory? 

cd "$DROPDIR"

if [ "$PWD" != "$DROPDIR" ]
then
	echo "Can't change to $DROPDIR"
	exit $E_XCD;
fi

tar czf $ZIPFILE $DROPFILE

exit 0;