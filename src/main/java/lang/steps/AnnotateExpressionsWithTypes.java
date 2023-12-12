package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import lang.scope.TypeTuple;
import org.antlr.symtab.*;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class AnnotateExpressionsWithTypes extends SlangBaseListener {
    private final ParseTreeProperty<Scope> scopes;
    private final ParseTreeProperty<Type> types;

    public AnnotateExpressionsWithTypes(ParseTreeProperty<Scope> scopes, ParseTreeProperty<Type> types) {
        this.scopes = scopes;
        this.types = types;
    }

    @Override
    public void exitAccessor(SlangParser.AccessorContext ctx) {
        TypedSymbol symbol = Resolvers.resolveAccessorToTypedSymbol(scopes, ctx);
        types.put(ctx, symbol.getType());
    }

    @Override
    public void exitExpression(SlangParser.ExpressionContext ctx) {
        // expression ::= expression EXPR_OPERATOR term
        if (ctx.expression() != null && ctx.EXPR_OPERATOR() != null && ctx.term() != null) {
            Type lhs = types.get(ctx.expression());
            Type rhs = types.get(ctx.term());
            // TODO: check that lhs and rhs are the same type
            types.put(ctx, lhs);
            return;
        }
        // expression ::= structInitializer
        if (ctx.structInitializer() != null) {
            String name = ctx.structInitializer().ID().getText();
            Scope current = scopes.get(ctx);
            Type type = (Type) current.resolve(name);
            System.out.println("Struct initializer: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }
        // expression ::= arrayConstructor
        if (ctx.arrayConstructor() != null) {
            String name = ctx.arrayConstructor().typeArray().type().getText();
            Scope current = scopes.get(ctx);
            Type type = (Type) current.resolve(name);
            System.out.println("Array constructor: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }
        // expression ::= application
        if (ctx.application() != null) {
            // TODO: check that the function returns a value
            String name = ctx.application().accessor().getText();
            Scope current = scopes.get(ctx);
            TypedSymbol type = (TypedSymbol) current.resolve(name);
            System.out.println("Application: " + ctx.getText() + " is of type " + type.getType().getName());
            types.put(ctx, type.getType());
            return;
        }
        // expression ::= term
        if (ctx.term() != null) {
            Type type = types.get(ctx.term());
            System.out.println("Term: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }

        throw new RuntimeException("Unknown expression type");
    }

    @Override
    public void exitTerm(SlangParser.TermContext ctx) {
        // term ::= term TERM_OPERATOR factor
        if (ctx.term() != null && ctx.TERM_OPERATOR() != null && ctx.factor() != null) {
            // the lhs must be a number and the rhs must be a number
            Type lhs = types.get(ctx.term());
            Type rhs = types.get(ctx.factor());
            if (lhs instanceof PrimitiveType primitive && primitive.getName().equals("Number")) {
                if (rhs instanceof PrimitiveType primitive2 && primitive2.getName().equals("Number")) {
                    types.put(ctx, lhs);
                } else {
                    System.err.println("Type error: " + ctx.getText() + " is not a number");
                }
            } else {
                System.err.println("Type error: " + ctx.getText() + " is not a number");
            }

            return;
        }
        // term ::= factor
        if (ctx.factor() != null) {
            Type type = types.get(ctx.factor());
            types.put(ctx, type);
            return;
        }

        throw new RuntimeException("Unknown term type");
    }

    @Override
    public void exitFactor(SlangParser.FactorContext ctx) {
        // factor ::= operand POSTFIX
        if (ctx.operand() != null && ctx.POSTFIX() != null) {
            Type type = types.get(ctx.operand());
            if (type instanceof PrimitiveType primitive && primitive.getName().equals("Number")) {
                types.put(ctx, type);
            } else {
                System.err.println("Type error: " + ctx.getText() + " is not a number");
            }

            return;
        }
        // factor ::= UNARY operand
        if (ctx.operand() != null && ctx.UNARY() != null) {
            if (ctx.UNARY().getText().equals("!")) {
                Type type = types.get(ctx.operand());
                if (type instanceof PrimitiveType primitive && primitive.getName().equals("Boolean")) {
                    types.put(ctx, type);
                } else {
                    System.err.println("Type error: " + ctx.getText() + " is not a boolean");
                }
            }

            if (ctx.UNARY().getText().equals("-")) {
                Type type = types.get(ctx.operand());
                if (type instanceof PrimitiveType primitive && primitive.getName().equals("Number")) {
                    types.put(ctx, type);
                } else {
                    System.err.println("Type error: " + ctx.getText() + " is not a number");
                }
            }

            return;
        }
        // factor ::= operand
        if (ctx.operand() != null) {
            Type type = types.get(ctx.operand());
            types.put(ctx, type);
            return;
        }
        // factor ::= '(' expression ')'
        if (ctx.expression() != null) {
            Type type = types.get(ctx.expression());
            types.put(ctx, type);
            return;
        }

        throw new RuntimeException("Unknown factor type");
    }

    @Override
    public void exitOperand(SlangParser.OperandContext ctx) {
        // operand ::= NUMBER
        if (ctx.NUMBER() != null) {
            Scope current = scopes.get(ctx);
            Type type = (Type) current.resolve("Number");
            System.out.println("Operand: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }
        // operand ::= STRING
        if (ctx.STRING() != null) {
            Scope current = scopes.get(ctx);
            Type type = (Type) current.resolve("String");
            System.out.println("Operand: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }
        // operand ::= NULL
        if (ctx.NULL() != null) {
            Scope current = scopes.get(ctx);
            Type type = (Type) current.resolve("Null");
            System.out.println("Operand: " + ctx.getText() + " is of type " + type.getName());
            types.put(ctx, type);
            return;
        }
        // operand ::= accessor
        if (ctx.accessor() != null) {
            // NOTE: I think this is redundant
            TypedSymbol symbol = Resolvers.resolveAccessorToTypedSymbol(scopes, ctx.accessor());
            System.out.println("Operand: " + ctx.getText() + " is of type " + symbol.getType().getName());
            types.put(ctx, symbol.getType());
            return;
        }
        // operand ::= tuple
        if (ctx.tuple() != null) {
            Scope current = scopes.get(ctx);
            String name = ctx.tuple().getText();
            TypeTuple typle = (TypeTuple) current.resolve(name);
            System.out.println("Operand: " + ctx.getText() + " is of type " + typle.getName());
            types.put(ctx, typle);
            return;
        }

        throw new RuntimeException("Unknown operand type");
    }

}
