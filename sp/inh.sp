

class B {
    fn t() int {
        return 1;
    }
}

class C extends B {
    a: int;

    fn t() int {
        return super.t();
    }

    fn set(x: int) void {
        a = x;
    }
}

fn main() int {
    a: C = new C();

    a.set(3);

    return a.t();
}
