package lang.steps;

import lang.antlr.generated.SlangParser;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Symbol;
import org.antlr.symtab.SymbolWithScope;
import org.antlr.symtab.TypedSymbol;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class Resolvers {
    public static TypedSymbol resolveAccessorToTypedSymbol(ParseTreeProperty<Scope> scopes, SlangParser.AccessorContext accessor) {
        Queue<String> ids = accessor.ID().stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toCollection(LinkedList::new));

        Scope scope = scopes.get(accessor);
        while (ids.size() > 1) {
            String id = ids.remove();
            Symbol symbol = scope.resolve(id);

            if (symbol instanceof SymbolWithScope symbolWithScope) scope = symbolWithScope;
            else if (symbol instanceof TypedSymbol typed) {
                if (typed.getType() instanceof SymbolWithScope symbolWithScope) scope = symbolWithScope;
                else throw new RuntimeException("Symbol " + id + " is not a symbol with scope");
            }
            else throw new RuntimeException("Symbol " + id + " is not a symbol with scope");
        }

        String id = ids.remove();
        Symbol symbol = scope.resolve(id);
        if (symbol instanceof TypedSymbol typed) return typed;
        else throw new RuntimeException("Symbol " + id + " is not a typed symbol");
    }
}
