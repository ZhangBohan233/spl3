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
}

fn main() int {
    a: C = new C();
    b: imp.Imp = new imp.Imp(1);

    cl:int = System.clock();

    return a.t();
}
