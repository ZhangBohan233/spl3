class A {

    fn toString() String {
        return "AA";
    }
}

fn test(v: Object) void {
    system.out.println(v);
}


fn tt() Integer {
    return 3;
}


fn main() int {
    a := new A();
    b : Integer = 21;
    c : float = new Float(1.0);
    d : float = 1 as float;
    system.out.println(d);
    return b;
}