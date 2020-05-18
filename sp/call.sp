import "util"

class A {

    fn t(i : int) int {
        System.println(i);
        return i + 1;
    }
}

fn g(i: int) int {
    return i * 2;
}

fn main() int {
    a := new A();
    for i: int = 0; i < 5; i++ {
        a.t(i);
    }
    return 0;
}