package lang.scope;

import org.antlr.symtab.Type;

import java.util.List;

public class TypeTuple implements Type {
    List<Type> types;

    public TypeTuple(List<Type> types) {
        this.types = types;
    }


    @Override
    public String getName() {
        StringBuilder string = new StringBuilder();
        string.append("(");
        for (int i = 0; i < types.size(); i++) {
            string.append(types.get(i).getName());
            if (i != types.size() - 1) {
                string.append(", ");
            }
        }
        string.append(")");
        return string.toString();
    }

    @Override
    public int getTypeIndex() {
        return 0;
    }
}
