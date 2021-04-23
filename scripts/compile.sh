#!/bin/sh

cd $(dirname $0)/..
sh scripts/install.sh
[ -d build ] || mkdir build
javac -d build -cp ".:lib/*" projet/src/*/*.java
