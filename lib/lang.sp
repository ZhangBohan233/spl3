class Object {

    fn eq(o: Object) boolean {
        system.out.println(Invokes.id(this));
        system.out.println(Invokes.id(o));
        return this == o;
    }

    fn hash() int {
        return 0;
    }

    fn toString() String {
        return "Object";
    }
}

class String {
    const chars: char[];
    const length: int;

    fn init(chs: char[]) void {
        chars = chs;
        length = chs.length;
    }
}

abstract class PrimitiveWrapper {

}

class Boolean extends PrimitiveWrapper {

    const value: boolean;

    fn init(v: boolean) void {
        value = v;
    }

    fn toString() String {
        return Invokes.string(value);
    }
}

class Character extends PrimitiveWrapper {

    const value: char;

    fn init(v: char) void {
        value = v;
    }

    fn toString() String {
        return Invokes.string(value);
    }
}

class Float extends PrimitiveWrapper {

    const value: float;

    fn init(v: float) void {
        value = v;
    }

    fn toString() String {
        return Invokes.string(value);
    }
}

class Integer extends PrimitiveWrapper {

    const value: int;

    fn init(v: int) void {
        value = v;
    }

    fn toString() String {
        return Invokes.string(value);
    }

    fn hash() int {
        return value;
    }
}

abstract class OutputStream {

}

abstract class NativeStream extends OutputStream {

    abstract fn print(obj: Object) void;

    abstract fn println(obj: Object) void;
}

class StdOut extends NativeStream {

    fn print(obj: Object) void {
        Invokes.print(obj);
    }

    fn println(obj: Object) void {
        Invokes.println(obj);
    }
}

class System {
    out: NativeStream = new StdOut();
}

class Error {
    errno: int;
    message: String;

    fn init(errno: int, message: String = null) void {
        this.errno = errno;
        this.message = message;
    }
}

system: System = new System();
