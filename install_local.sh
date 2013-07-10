#!/bin/sh

dest=~/apps/jc
echo $dest
cp -v target/jk-0.1-SNAPSHOT.jar $dest/jc-0.1-SNAPSHOT.jar

mkdir -p $dest/lib
cp -r target/lib $dest

