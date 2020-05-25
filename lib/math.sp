fn fib(n: int) int {
    if n < 2 {
        return n;
    } else {
        return fib(n - 1) + fib(n - 2);
    }
}


fn max(a: int, b: int) int {
    return a;
}