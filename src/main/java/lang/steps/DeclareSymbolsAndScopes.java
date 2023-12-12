package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import lang.scope.InterfaceSymbol;
import lang.scope.ModuleSymbol;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

// Builds the parse/scope tree and each node's symbol table, by defining each symbol
// declaration nominally.  This means terms with types are not resolved to their
// type objects, and terms with symbol usage are not resolved to their symbol objects.
public class DeclareSymbolsAndScopes extends SlangBaseListener {
    private final ParseTreeProperty<Scope> scopes;
    private Scope current;

    public DeclareSymbolsAndScopes(Scope current, ParseTreeProperty<Scope> scopes) {
        this.current = current;
        this.scopes = scopes;
    }

    // GLOBAL ==========================================================================================================
    @Override
    public void enterModuleDeclaration(SlangParser.ModuleDeclarationContext ctx) {
        String name = ctx.ID().getText();
        ModuleSymbol module = new ModuleSymbol(name);
        module.setEnclosingScope(current);
        current.define(module);
        scopes.put(ctx, module);
        current = module;
    }

    @Override
    public void enterImportDeclaration(SlangParser.ImportDeclarationContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterCompilationUnit(SlangParser.CompilationUnitContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void exitCompilationUnit(SlangParser.CompilationUnitContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterHeader(SlangParser.HeaderContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterBody(SlangParser.BodyContext ctx) {
        scopes.put(ctx, current);
    }

    // INTERFACE =======================================================================================================
    @Override
    public void enterInterfaceDeclaration(SlangParser.InterfaceDeclarationContext ctx) {
        String name = ctx.ID().getText();
        InterfaceSymbol interfaceSymbol = new InterfaceSymbol(name);
        interfaceSymbol.setEnclosingScope(current);
        interfaceSymbol.setDefNode(ctx);
        current.define(interfaceSymbol);
        scopes.put(ctx, interfaceSymbol);
        current = interfaceSymbol;
    }

    @Override
    public void enterInterfaceMethodDeclaration(SlangParser.InterfaceMethodDeclarationContext ctx) {
        String name = ctx.ID().getText();
        MethodSymbol method = new MethodSymbol(name);
        method.setEnclosingScope(current);
        method.setDefNode(ctx);
        current.define(method);
        current = method;
        scopes.put(ctx, current);
    }

    @Override
    public void exitInterfaceMethodDeclaration(SlangParser.InterfaceMethodDeclarationContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void exitInterfaceDeclaration(SlangParser.InterfaceDeclarationContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterInterfaceExtendsList(SlangParser.InterfaceExtendsListContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterInterfaceMethodList(SlangParser.InterfaceMethodListContext ctx) {
        scopes.put(ctx, current);
    }

    // STRUCT ==========================================================================================================
    @Override
    public void enterStructDeclaration(SlangParser.StructDeclarationContext ctx) {
        String name = ctx.ID().getText();
        StructSymbol struct = new StructSymbol(name);
        struct.setEnclosingScope(current);
        struct.setDefNode(ctx);
        current.define(struct);
        current = struct;
        scopes.put(ctx, current);
    }

    @Override
    public void enterStructFieldDeclaration(SlangParser.StructFieldDeclarationContext ctx) {
        String name = ctx.ID().getText();
        FieldSymbol field = new FieldSymbol(name);
        current.define(field);
        scopes.put(ctx, current);
    }


    @Override
    public void enterStructFieldInitializer(SlangParser.StructFieldInitializerContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStructMethodDeclaration(SlangParser.StructMethodDeclarationContext ctx) {
        String name = ctx.ID().getText();
        MethodSymbol method = new MethodSymbol(name);
        method.setEnclosingScope(current);
        method.setDefNode(ctx);
        current.define(method);
        current = method;
        scopes.put(ctx, current);
    }

    @Override
    public void exitStructMethodDeclaration(SlangParser.StructMethodDeclarationContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterVariable(SlangParser.VariableContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void exitStructDeclaration(SlangParser.StructDeclarationContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterStructImplementsList(SlangParser.StructImplementsListContext ctx) {
        scopes.put(ctx, current);
    }

    // FUNCTION ========================================================================================================
    @Override
    public void enterFunctionDeclaration(SlangParser.FunctionDeclarationContext ctx) {
        String name = ctx.ID().getText();
        FunctionSymbol function = new FunctionSymbol(name);
        function.setEnclosingScope(current);
        function.setDefNode(ctx);
        current.define(function);
        current = function;
        scopes.put(ctx, current);
    }

    @Override
    public void enterFunctionParameter(SlangParser.FunctionParameterContext ctx) {
        String name = ctx.ID().getText();
        ParameterSymbol parameter = new ParameterSymbol(name);
        current.define(parameter);
        scopes.put(ctx, current);
    }

    @Override
    public void exitFunctionDeclaration(SlangParser.FunctionDeclarationContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterFunctionParameterList(SlangParser.FunctionParameterListContext ctx) {
        scopes.put(ctx, current);
    }

    // STATEMENT =======================================================================================================
    @Override
    public void enterStatementBlock(SlangParser.StatementBlockContext ctx) {
        LocalScope block = new LocalScope(current);
        current.nest(block);
        current = block;
        scopes.put(ctx, current);
    }

    @Override
    public void enterVariableDeclaration(SlangParser.VariableDeclarationContext ctx) {
        String name = ctx.ID().getText();
        VariableSymbol variable = new VariableSymbol(name);
        current.define(variable);
        scopes.put(ctx, current);
    }

    @Override
    public void enterVariableAssignment(SlangParser.VariableAssignmentContext ctx) {
        String text = ctx.getText();
        scopes.put(ctx, current);
    }

    @Override
    public void exitStatementBlock(SlangParser.StatementBlockContext ctx) {
        current = current.getEnclosingScope();
    }

    @Override
    public void enterStatement(SlangParser.StatementContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementIf(SlangParser.StatementIfContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementWhile(SlangParser.StatementWhileContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementFor(SlangParser.StatementForContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementReturn(SlangParser.StatementReturnContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementBreak(SlangParser.StatementBreakContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementContinue(SlangParser.StatementContinueContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStatementDefer(SlangParser.StatementDeferContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterExpression(SlangParser.ExpressionContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterArrayConstructor(SlangParser.ArrayConstructorContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterApplication(SlangParser.ApplicationContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStructInitializer(SlangParser.StructInitializerContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterStructInitializerField(SlangParser.StructInitializerFieldContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterTerm(SlangParser.TermContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterFactor(SlangParser.FactorContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterTuple(SlangParser.TupleContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterOperand(SlangParser.OperandContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterAccessor(SlangParser.AccessorContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterType(SlangParser.TypeContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterTypeArray(SlangParser.TypeArrayContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterTypeTuple(SlangParser.TypeTupleContext ctx) {
        scopes.put(ctx, current);
    }

    @Override
    public void enterTypeUnion(SlangParser.TypeUnionContext ctx) {
        scopes.put(ctx, current);
    }
}
