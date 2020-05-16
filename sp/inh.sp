import "imp/imp.sp"

class B {
    fn t() int {
        return 1;
    }
}

class C extends B {
    a: int;

    fn init(x: int) void {
        this.a = x;
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

class D extends C {
    fn init() void {
        //super.init(2);
    }
}

fn main() int {
    a: C = new D();


    return a.t();
}
