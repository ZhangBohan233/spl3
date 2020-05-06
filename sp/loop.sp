fn main() int {

    t0: int = System.clock();
    for j: int = 0; j < 1000000; j = j + 1 {
        //System.println(j);
    }
    t1: int = System.clock();
    System.println(t1 - t0);

    return 1;
}