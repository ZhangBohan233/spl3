import "math" as mat

class A {
    a: int;
    b: int = 7;

    fn init() void {

    }
}



fn add(a: int, b: int) int {
    return a + b;
}

fn main() int {
    a: int;
    a = 5;
    b: int = 3;

    //c: int = add(a, add(2, b));
    //d: int = mat.max(2, 4);

    e: A = new A();
    e.a = a;

    return e.a;
}
