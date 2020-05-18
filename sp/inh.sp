import "imp/imp.sp"

class B {
    const a: int = 0;

    fn t() int {
        return 1;
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
}


fn main() int {
    a: C = new C(2);
    System.println(a.super.a);

    return a.t();
}
