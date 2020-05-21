package interpreter.types;

import ast.Node;
import interpreter.env.Environment;

public class UndTemplateType extends PointerType {

    public final String templateName;

    public UndTemplateType(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public int getPointerType() {
        return 0;
    }

    @Override
    protected boolean isSuperclassOfOrEqualsNotNull(Type child, Environment env) {
        return false;
    }

    @Override
    public String toString() {
        return "Template<" + templateName + ">";
    }
}
