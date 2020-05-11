fn main() int {

    t0: int = System.clock();
    for j: int = 0; j < 10; j = j + 1 {
        if j == 6 {
            return 1212;
        }
        System.println(j);
    }
    t1: int = System.clock();
    System.println(t1 - t0);

    return 1;
}