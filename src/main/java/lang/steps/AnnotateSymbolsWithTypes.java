package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import lang.scope.TypeArray;
import lang.scope.TypeTuple;
import lang.scope.TypeUnion;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.*;

public class AnnotateSymbolsWithTypes extends SlangBaseListener {
    private final ParseTreeProperty<Scope> scopes;

    public AnnotateSymbolsWithTypes(ParseTreeProperty<Scope> scopes) {
        this.scopes = scopes;
    }

    public void enterVariableDeclaration(SlangParser.VariableDeclarationContext ctx) {
        if (ctx.type() != null) {
            // The type is explicitly declared
            Scope current = scopes.get(ctx);
            VariableSymbol symbol = (VariableSymbol) current.resolve(ctx.ID().getText());
            SlangParser.TypeContext typeContext = ctx.type();
            Type type = resolve(typeContext, scopes);
            System.out.println("Variable " + symbol.getName() + " has type " + type.getName());
            symbol.setType(type);
        } else {
            // We will have to derive the type from the expression which is in the next step
        }
    }

    public void enterVariableAssignment(SlangParser.VariableAssignmentContext ctx) {

    }

    public void enterFunctionParameter(SlangParser.FunctionParameterContext ctx) {
        Scope current = scopes.get(ctx);
        String parameterName = ctx.ID().getText();
        ParameterSymbol symbol = (ParameterSymbol) current.resolve(parameterName);
        SlangParser.TypeContext typeContext = ctx.type();
        Type type = resolve(typeContext, scopes);
        System.out.println("Parameter " + symbol.getName() + " has type " + type.getName());
        symbol.setType(type);
    }

    public void enterFunctionDeclaration(SlangParser.FunctionDeclarationContext ctx) {
        FunctionSymbol symbol = (FunctionSymbol) scopes.get(ctx);
        SlangParser.TypeContext returnTypeContext = ctx.type();
        Type returnType = resolve(returnTypeContext, scopes);
        System.out.println("Function " + symbol.getName() + " has return type " + returnType.getName());
        symbol.setType(returnType);
    }

    public void enterStructMethodDeclaration(SlangParser.StructMethodDeclarationContext ctx) {
        MethodSymbol symbol = (MethodSymbol) scopes.get(ctx);
        SlangParser.TypeContext returnTypeContext = ctx.type();
        Type returnType = resolve(returnTypeContext, scopes);
        System.out.println("Method " + symbol.getName() + " has return type " + returnType.getName());
        symbol.setType(returnType);
    }

    public void enterInterfaceMethodDeclaration(SlangParser.InterfaceMethodDeclarationContext ctx) {
        MethodSymbol symbol = (MethodSymbol) scopes.get(ctx);
        SlangParser.TypeContext returnTypeContext = ctx.type();
        Type returnType = resolve(returnTypeContext, scopes);
        System.out.println("Method " + symbol.getName() + " has return type " + returnType.getName());
        symbol.setType(returnType);
    }

    public void enterStructFieldDeclaration(SlangParser.StructFieldDeclarationContext ctx) {
        Scope current = scopes.get(ctx);
        FieldSymbol symbol = (FieldSymbol) current.resolve(ctx.ID().getText());
        SlangParser.TypeContext typeContext = ctx.type();
        Type type = resolve(typeContext, scopes);
        System.out.println("Field " + symbol.getName() + " has type " + type.getName());
        symbol.setType(type);
    }

    public static Type resolve(SlangParser.TypeContext context, ParseTreeProperty<Scope> scopes) {
        Type result = null;

        if (context.ID() != null) {
            // Resolve primitive types
            Scope current = scopes.get(context);
            String typeName = context.ID().getText();
            result = (Type) current.resolve(typeName);
        } else if (context.typeTuple() != null) {
            // Resolve tuple types
            List<Type> typeList = new ArrayList<>();
            for (var typeContext : context.typeTuple().type()) {
                typeList.add(resolve(typeContext, scopes));
            }
            result = new TypeTuple(typeList);
        } else if (context.typeUnion() != null) {
            // Resolve union types
            Set<Type> typeSet = new HashSet<>();
            for (var typeContext : context.typeUnion().type()) {
                typeSet.add(resolve(typeContext, scopes));
            }
            result = new TypeUnion(typeSet);
        } else if (context.typeArray() != null) {
            // Resolve array types
            Type type = resolve(context.typeArray().type(), scopes);
            result = new TypeArray(type);
        }

        if (result == null) {
            String error = String.format("Could not resolve type %s", context.getText());
            throw new RuntimeException(error);
        }

        return result;
    }
}
