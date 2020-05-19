fn t(a: int = 3) int {
    return a + 1;
}

class A {
    x: int;
    fn init(x: int = 2) void {
        this.x = x;
    }
}

class B extends A {
    fn init() void {
        super.init(0);
    }
}

fn main() int {
    g := lambda(a: int = 12, b: int = 33) -> a + b;
    return g();
}