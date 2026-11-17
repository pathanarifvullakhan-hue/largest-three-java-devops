#!/bin/sh
set -eu
ROOT="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "$ROOT"
rm -rf build
mkdir -p build/classes build/test-classes
javac -d build/classes src/main/java/com/example/largest/LargestOfThree.java src/main/java/com/example/largest/CalculatorServer.java
javac -cp build/classes -d build/test-classes src/test/java/com/example/largest/LargestOfThreeTest.java
java -cp build/classes:build/test-classes com.example.largest.LargestOfThreeTest
