package lang.scope;

import org.antlr.symtab.Type;

public class TypeArray implements Type {
    Type type;

    public TypeArray(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return "[]" + type.getName() ;
    }

    @Override
    public int getTypeIndex() {
        return 0;
    }
}
