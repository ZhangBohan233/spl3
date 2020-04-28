import "imp/imp.sp"

class B {
    fn t() int {
        return 1;
    }
}

class C extends B {
    a: int;

    fn init() void {
        a = 2;
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
    a: C = new C();
    v: int = 1;
    b: imp.Imp = new imp.Imp(v);
    c: imp.Imp[] = new imp.Imp[2];
    a.self().set(6);

    //cl:int = System.clock();

    return a.t();
}
