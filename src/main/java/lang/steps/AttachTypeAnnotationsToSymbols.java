package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import lang.scope.TypeFactory;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class AttachTypeAnnotationsToSymbols extends SlangBaseListener {
    private final ParseTreeProperty<Scope> scopes;

    public AttachTypeAnnotationsToSymbols(ParseTreeProperty<Scope> scopes) {
        this.scopes = scopes;
    }

    public void enterVariableDeclaration(SlangParser.VariableDeclarationContext ctx) {
        Scope current = scopes.get(ctx);
        VariableSymbol symbol = (VariableSymbol) current.resolve(ctx.ID().getText());
        Type type = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(type);
    }

    public void enterFunctionParameter(SlangParser.FunctionParameterContext ctx) {
        Scope current = scopes.get(ctx);
        ParameterSymbol symbol = (ParameterSymbol) current.resolve(ctx.ID().getText());
        Type type = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(type);
    }

    public void enterFunctionDeclaration(SlangParser.FunctionDeclarationContext ctx) {
        FunctionSymbol symbol = (FunctionSymbol) scopes.get(ctx);
        Type returnType = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(returnType);
    }

    public void enterStructMethodDeclaration(SlangParser.StructMethodDeclarationContext ctx) {
        MethodSymbol symbol = (MethodSymbol) scopes.get(ctx);
        Type returnType = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(returnType);
    }

    public void enterInterfaceMethodDeclaration(SlangParser.InterfaceMethodDeclarationContext ctx) {
        MethodSymbol symbol = (MethodSymbol) scopes.get(ctx);
        Type returnType = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(returnType);
    }

    public void enterStructFieldDeclaration(SlangParser.StructFieldDeclarationContext ctx) {
        Scope current = scopes.get(ctx);
        FieldSymbol symbol = (FieldSymbol) current.resolve(ctx.ID().getText());
        Type type = TypeFactory.create(ctx.type(), scopes);
        symbol.setType(type);
    }
}
