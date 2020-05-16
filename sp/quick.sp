fn test(a: int, f: [int] -> int) int {
    return f(a);
}


class C {
    const x: int;

    fn init(x: int) void {
        this.x = x;
    }

    fn ff() void {
        System.println(123);
    }
}

class D extends C {
    fn init() void {
        super.init(12);
    }
}

fn main() int {
    a := 3;
    b: [int, int] -> int = lambda(x: int, y: int) -> x + y;

    //origC := new C(3);

    c: C = new C(2) <- {
        fn ff() void {
            System.println(321);
        }
    }
    System.println(c.x);
    c.ff();

    d := new D();
    System.println(d.x);

    return test(5, lambda(x: int) -> x + a);
}