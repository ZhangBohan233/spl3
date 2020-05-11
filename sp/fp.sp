fn deal(a: int, f: [int]->int) int {
    return f(a);
}

fn foo() [int, int]->int {
    b: int = 3;

    f: [int, int]->int = fn(a: int, c: int) int {
        return a + 4 + b + c;
    }
    return f;
}

fn main() int {
    f: [int]->int = fn(x: int) int {
        return x + 1;
    }
    x: int = deal(2, lambda(x: int) x + 1);
    System.println(x);

    a: int = 2;
    return foo()(a, foo()(a, 2));
}