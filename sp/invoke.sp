class A {

    fn toString() String {
        return "AA";
    }
}

fn test(v: Object) void {
    system.out.println(v);
}


fn main() int {
    a := new A();
    b : Integer = 21;
    system.out.println(a);
    return b.value;
}