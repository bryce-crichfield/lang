package lang.scope;

import lang.antlr.generated.SlangParser;
import org.antlr.symtab.Scope;
import org.antlr.symtab.Type;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeFactory {
    public static Type create(SlangParser.TypeContext context, ParseTreeProperty<Scope> scopes) {
        Scope current = scopes.get(context);

        if (context.ID() != null) {
            // Resolve primitive types
            String typeName = context.ID().getText();
            return (Type) current.resolve(typeName);
        }

        if (context.typeTuple() != null) {
            // Resolve tuple types
            List<Type> typeList = new ArrayList<>();
            for (var typeContext : context.typeTuple().type()) {
                typeList.add(create(typeContext, scopes));
            }
            return new TypeTuple(typeList);
        }

        if (context.typeUnion() != null) {
            // Resolve union types
            Set<Type> typeSet = new HashSet<>();
            for (var typeContext : context.typeUnion().type()) {
                typeSet.add(create(typeContext, scopes));
            }
            return new TypeUnion(typeSet);
        }

        if (context.typeArray() != null) {
            // Resolve array types
            Type type = create(context.typeArray().type(), scopes);
            return new TypeArray(type);
        }

        String error = String.format("Could not resolve type %s", context.getText());
        throw new RuntimeException(error);
    }

}
