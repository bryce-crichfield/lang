package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.List;
import java.util.Optional;

public class EnforceTypeUsages extends SlangBaseListener {
    ParseTreeProperty<Scope> scopes;
    ParseTreeProperty<Type> types;

    public EnforceTypeUsages(ParseTreeProperty<Scope> scopes, ParseTreeProperty<Type> types) {
        this.scopes = scopes;
        this.types = types;
    }

    private static Boolean equivalent(Type a, Type b) {
        return a.getName().equals(b.getName());
    }

    @Override
    public void enterVariable(SlangParser.VariableContext ctx) {
        // variable ::= variableDeclaration '=' expression
        if (ctx.variableDeclaration() != null && ctx.expression() != null) {
            String variableName = ctx.variableDeclaration().ID().getText();
            VariableSymbol variableSymbol = (VariableSymbol) scopes.get(ctx).resolve(variableName);
            Type variableType = variableSymbol.getType();
            Type expressionType = types.get(ctx.expression());
            if (!equivalent(variableType, expressionType)) {
                String error = String.format("Cannot assign %s to %s", expressionType.getName(), variableType.getName());
                throw new RuntimeException(error);
            }
        }
        // variable ::= variableAssignment '=' expression
        if (ctx.variableAssignment() != null) {
            SlangParser.AccessorContext accessor = ctx.variableAssignment().accessor();
            Type typeLhs = types.get(accessor);
            Type typeRhs = types.get(ctx.expression());
            if (!equivalent(typeLhs, typeRhs)) {
                String error = String.format("Cannot assign %s to %s", typeRhs.getName(), typeLhs.getName());
                throw new RuntimeException(error);
            }
        }
    }

    @Override
    public void enterStatementBlock(SlangParser.StatementBlockContext ctx) {
        // Enforce return statement possession
    }

    @Override
    public void enterStatementReturn(SlangParser.StatementReturnContext ctx) {
        // return ::= 'return' expression
        Optional<FunctionSymbol> functionSymbol = Optional.empty();
        Scope current = scopes.get(ctx);
        while (current != null) {
            if (current instanceof FunctionSymbol) {
                functionSymbol = Optional.of((FunctionSymbol) current);
                break;
            }
            if (current instanceof LocalScope) {
                current = current.getEnclosingScope();
            } else {
                break;
            }
        }

        if (functionSymbol.isEmpty()) {
            throw new RuntimeException("Return statement must be inside a function");
        }

        FunctionSymbol function = functionSymbol.get();
        Type returnType = function.getType();
        Type expressionType = types.get(ctx.expression());
        if (!equivalent(returnType, expressionType)) {
            String error = String.format("Cannot return %s from function %s", expressionType.getName(), function.getName());
            throw new RuntimeException(error);
        }
    }

    @Override
    public void enterStructInitializer(SlangParser.StructInitializerContext ctx) {
        // structInitializer ::= ID structInitializerField (',' structInitializerField)*
        String structName = ctx.ID().getText();
        StructSymbol structSymbol = (StructSymbol) scopes.get(ctx).resolve(structName);

        int nDeclaredFields = structSymbol.getSymbols().stream().filter(s -> s instanceof FieldSymbol).map(s -> (FieldSymbol) s).toList().size();
        int nInitializedFields = ctx.structInitializerField().size();

        if (nDeclaredFields != nInitializedFields) {
            String error = String.format("Struct %s expects %d fields, got %d", structName, nDeclaredFields, nInitializedFields);
            throw new RuntimeException(error);
        }

        for (var fieldInit: ctx.structInitializerField()) {
            // structInitializerField ::= ID '=' expression
            String fieldName = fieldInit.ID().getText();
            FieldSymbol fieldSymbol = (FieldSymbol) structSymbol.resolve(fieldName);
            Type fieldType = fieldSymbol.getType();
            Type expressionType = types.get(fieldInit.expression());
            if (!equivalent(fieldType, expressionType)) {
                String error = String.format("Cannot assign %s to %s", expressionType.getName(), fieldType.getName());
                error += String.format(" in struct %s", structName);
                error += String.format(" in field %s", fieldName);
                throw new RuntimeException(error);
            }
        }
    }

    @Override
    public void enterApplication(SlangParser.ApplicationContext ctx) {
        // Check that the number of arguments matches the number of parameters
        // Then check that the types of the arguments match the types of the parameters

        // application ::= expression '(' expressionList? ')'
        String functionName = ctx.accessor().getText();
        // Could be function or method symbol, assume function for now
        FunctionSymbol functionSymbol = (FunctionSymbol) scopes.get(ctx).resolve(functionName);

        int nExpressions = ctx.expression().size();
        int nParameters = functionSymbol.getNumberOfParameters();

        if (nExpressions != nParameters) {
            String error = String.format("Function %s expects %d parameters, got %d", functionName, nParameters, nExpressions);
            throw new RuntimeException(error);
        }

        List<ParameterSymbol> parameters = functionSymbol.getSymbols().stream().filter(s -> s instanceof ParameterSymbol).map(s -> (ParameterSymbol) s).toList();

        for (int i = 0; i < nExpressions; i++) {
            Type expressionType = types.get(ctx.expression(i));
            Type parameterType = parameters.get(i).getType();
            if (!equivalent(expressionType, parameterType)) {
                String error = String.format("Cannot assign %s to %s", expressionType.getName(), parameterType.getName());
                error += String.format(" in function %s", functionName);
                error += String.format(" in parameter %d", i);
                throw new RuntimeException(error);
            }
        }
    }

    /* TODO:
            - Binary operators
            - Unary operators
            - Array access
            - Postfix operators
     */
}
