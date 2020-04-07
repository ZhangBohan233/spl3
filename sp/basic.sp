import "math" as mat

fn add(a: int, b: int) int {
    return a + b;
}

fn main() int {
    a: int;
    a = 5;
    b: int = 3;

    c: int = add(a, add(2, b));
    d: int = mat.max(2, 4);

    return d;
}
