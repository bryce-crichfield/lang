package lang.scope;

import org.antlr.symtab.Type;

import java.util.Set;

public class TypeUnion implements Type {
    Set<Type> types;

    public TypeUnion(Set<Type> types) {
        this.types = types;
    }

    @Override
    public String getName() {
        StringBuilder string = new StringBuilder();
        string.append("(");
        for (Type type : types) {
            string.append(type.getName());
            string.append(" | ");
        }
        string.delete(string.length() - 3, string.length());
        string.append(")");
        return string.toString();
    }

    @Override
    public int getTypeIndex() {
        return 0;
    }
}
