fn test(a: int, f: [int] -> int) int {
    return f(a);
}


fn main() int {
    a := 3;
    b: [int, int] -> int = lambda(x: int, y: int) -> x + y;


    return test(5, lambda(x: int) -> x + a);
}