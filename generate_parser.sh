#!/usr/bin/env bash

echo "Generating ANTLR4 parser for Slang"

GRAMMAR_DIR="src/main/resources/antlr"
GENERATE_DIR="src/main/java/lang/antlr/generated"

if [ -d ${GENERATE_DIR} ]; then
    rm -rf ${GENERATE_DIR}
fi
antlr4 ${GRAMMAR_DIR}/Slang.g4 -Dlanguage=Java -visitor -listener -o ${GENERATE_DIR}
rm -f ${GENERATE_DIR}/*.interp ${GENERATE_DIR}/*.tokens

mv ${GENERATE_DIR}/${GRAMMAR_DIR}/* ${GENERATE_DIR}
rm -rf ${GENERATE_DIR}/src
rm -rf ${GENERATE_DIR}/*.interp ${GENERATE_DIR}/*.tokens