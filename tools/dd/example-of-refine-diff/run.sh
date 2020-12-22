#!/bin/sh
cd example && javac *java && cd .. || exit 125
java -cp . example.Check
