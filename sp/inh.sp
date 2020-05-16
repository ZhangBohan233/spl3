import "imp/imp.sp"

class B {
    fn t() int {
        return 1;
    }
}

class C extends B {
    const a: int = 1;

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


    return a.t();
}
