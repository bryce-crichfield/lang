package lang.steps;

import lang.antlr.generated.SlangBaseListener;
import lang.antlr.generated.SlangParser;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;
import org.antlr.symtab.VariableSymbol;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class InferVariableTypes extends SlangBaseListener {
    ParseTreeProperty<Scope> scopes;
    ParseTreeProperty<Type> types;

    public InferVariableTypes(ParseTreeProperty<Scope> scopes, ParseTreeProperty<Type> types) {
        this.scopes = scopes;
        this.types = types;
    }

    @Override
    public void exitVariableDeclaration(SlangParser.VariableDeclarationContext ctx) {
        if (ctx.type() == null) {
            // The type was not explicitly declared, so we need to derive it from the expression
            Scope current = scopes.get(ctx);
            VariableSymbol symbol = (VariableSymbol) current.resolve(ctx.ID().getText());

            SlangParser.VariableContext variable = (SlangParser.VariableContext) ctx.getParent();
            SlangParser.ExpressionContext expression = variable.expression();
            Type type = types.get(expression);

            symbol.setType(type);
        }
    }
}
