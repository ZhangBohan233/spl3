import "imp/imp.sp"

abstract class B {
    const a: int = 0;

    fn t() int {
        return 1;
    }

    fn printThis() void {
        system.out.println(this);
    }

    fn toString() String {
        return "Inst of B";
    }

    abstract fn xx() int;
}

class C extends B {
    const a: int;

    fn init(x: int) void {
        a = x;
    }

    fn t() int {
        return a;
    }

    fn set(x: int) void {
        a = x;
    }

    fn self() C {
        return this;
    }

    fn toString() String {
        return "Inst of C";
    }

    fn xx() int {
        return 1;
    }
}


fn main() int {
    a: C = new C(2);
    b: C = new C(2);
    print := system.out.println;
    print(a.eq(b));

    a.printThis();

    return a.t();
}
