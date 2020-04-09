

class B {
    fn t() int {
        return 1;
    }
}

class C extends B {
    a: int;

    fn t() int {
        return a;
    }

    fn set(x: int) void {
        super;
    }
}

fn main() int {
    a: C = new C();

    a.set(3);

    return a.t();
}
