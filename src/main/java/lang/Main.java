package lang;

import lang.antlr.generated.SlangLexer;
import lang.antlr.generated.SlangParser;
import lang.command.CommandLineInterface;
import lang.steps.*;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/*
    # Semantic Analysis Pass Order
        1. Name Declaration         - Construct scope tree and local symbol tables
        2. Name Resolution          - Resolve all type, struct, function and variable names to their declarations
        3. Type Checking            - Check that all expressions have the correct type
        4. Control Flow Analysis    - Check that all paths through a function return a value
*/
public class Main {
    public static String printScopeTree(Scope scope) {
        StringBuilder sb = new StringBuilder();
        sb.append(scope.getName());
        sb.append("\n");
        for (Scope s : scope.getNestedScopes()) {
            sb.append(printScopeTree(s));
        }
        return sb.toString();
    }

    public static SlangParser.CompilationUnitContext loadCompilationUnit(String filename) throws IOException {
        String path = Objects.requireNonNull(Main.class.getResource(filename)).getPath();
        CharStream input = CharStreams.fromPath(Path.of(path));
        SlangLexer lexer = new SlangLexer(input);
        TokenStream tokens = new CommonTokenStream(lexer);
        SlangParser parser = new SlangParser(tokens);
        return parser.compilationUnit();
    }

    public static void main(String[] args) throws IOException {
        // TODO: Use me!
        CommandLineInterface cli = new CommandLineInterface();

        SlangParser.CompilationUnitContext tree = loadCompilationUnit("/test.slang");

        PredefinedScope predefinedTypes = new PredefinedScope();
        predefinedTypes.define(new PrimitiveType("Error"));
        predefinedTypes.define(new PrimitiveType("Number"));
        predefinedTypes.define(new PrimitiveType("Void"));
        predefinedTypes.define(new PrimitiveType("Boolean"));
        predefinedTypes.define(new PrimitiveType("String"));
        GlobalScope global = new GlobalScope(predefinedTypes);

        ParseTreeProperty<Scope> scopes = new ParseTreeProperty<>();
        ParseTreeProperty<Type> types = new ParseTreeProperty<>();

        DeclareSymbolsAndScopes declareSymbolsAndScopes = new DeclareSymbolsAndScopes(global, scopes);
        AnnotateSymbolsWithTypes annotateSymbolsWithTypes = new AnnotateSymbolsWithTypes(scopes);
        AnnotateExpressionsWithTypes annotateExpressionsWithTypes = new AnnotateExpressionsWithTypes(scopes, types);
        InferVariableTypes inferVariableTypes = new InferVariableTypes(scopes, types);
        EnforceTypeUsages enforceTypeUsages = new EnforceTypeUsages(scopes, types);

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(declareSymbolsAndScopes, tree);
        walker.walk(annotateSymbolsWithTypes, tree);
        walker.walk(annotateExpressionsWithTypes, tree);
        walker.walk(inferVariableTypes, tree);
        walker.walk(enforceTypeUsages, tree);
    }
}