s: int = 10;

class A {
    arr : int[] = new int[s];
    size: int = s;

    //fn init() void {
    //}
}


class B {
    arr : int[] = new int[s];
}

fn gc() void {
    System.gc();
}


fn main() int {
    a := new A();
    a = new A();
    b := new B();

    System.memoryView();
    System.println(System.id(a));

    return a.size;
}