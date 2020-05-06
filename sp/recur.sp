fn fib(n: int) int {
    if n < 2 {
        return n;
    } else {
        return fib(n - 1) + fib(n - 2);
    }
}

fn main() int {
    t0: int = System.clock();

    f: int = fib(8);
    System.println(f);

    t1: int = System.clock();
    System.print("Time: ");
    System.println(t1 - t0);

    return 0;
}