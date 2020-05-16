b: int;

class A {
    const a: int;

    fn init(a: int) void {
        this.a = a;
        const b: float = 3.3;
    }
}

fn main() int {
    aa := new A(3);

    return aa.a;
}