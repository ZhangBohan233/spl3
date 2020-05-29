s: int = 10;

class A {
    arr : int[] = new int[s];
    size: int = s;

    //fn init() void {
    //}

    fn toString() String {
        return "fs";
    }
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

    System.memoryView();
    //c: char[] = new char[14];
    //s: String = new String(c);
    b = "fs";

    return a.size;
}