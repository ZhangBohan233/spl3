fn test(a: int, f: [int] -> int) int {
    return f(a);
}


class C {
    x: int = 1;

    fn init(a: int) void {
        this.x = a;
    }

    fn ff() void {
        System.println(123);
    }
}


fn main() int {
    a := 3;
    b: [int, int] -> int = lambda(x: int, y: int) -> x + y;

    origC := new C(3);

    c: C = new C(2) <- {
        fn ff() void {
            System.println(321);
        }
    }
    System.println(c.x);
    c.ff();

    return test(5, lambda(x: int) -> x + a);
}