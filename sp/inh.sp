import "imp/imp.sp"

class B {
    const a: int = 0;

    fn t() int {
        return 1;
    }

    fn printThis() void {
        system.out.println(toString());
    }

    fn toString() String {
        return "Inst of B";
    }
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
}


fn main() int {
    a: C = new C(2);
    print := system.out.println;
    print(a.super.a);

    a.printThis();

    return a.t();
}
