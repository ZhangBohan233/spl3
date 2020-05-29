s: int = 10;

class A {
    arr : int[] = new int[s];
    size: int = s;

    fn init() void {

    }

    fn toString() String {
        return "fs";
    }

    fn more() A {
        return new A();
    }

    fn createB() B {
        return new B();
    }
}


class B {
    fn init() void {
        System.gc();
    }
}


fn main() int {
    a := new A();
    a = new A();

    b := a.createB();

    a = a.more();

    System.memoryView();
    //c: char[] = new char[14];
    //s: String = new String(c);
    //b := "fs";

    return a.size;
}