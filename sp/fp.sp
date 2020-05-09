fn add(a: int, b: int) int {
    return a + b;
}

fn foo() [int, int]->int {
    b: int = 33;

    f: [int, int]->int = fn(a: int, c: int) int {
        return a + 12 + b + c;
    }
    return f;
}

fn main() int {

    return foo()(2, 3);
}