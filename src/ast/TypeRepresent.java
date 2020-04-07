package ast;

import interpreter.env.Environment;
import interpreter.types.Type;

public interface TypeRepresent {

    Type evalType(Environment environment);
}
