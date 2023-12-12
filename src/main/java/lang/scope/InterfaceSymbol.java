package lang.scope;

import org.antlr.symtab.DataAggregateSymbol;
import org.antlr.symtab.Scope;
import org.antlr.symtab.StructSymbol;

import java.sql.Struct;

public class InterfaceSymbol extends DataAggregateSymbol {
    public InterfaceSymbol(String name) {
        super(name);
    }
}
