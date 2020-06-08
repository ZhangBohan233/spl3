import namespace "util"

class A {
    //fn eq(o: Object) boolean {
    //    return this == o;
    //}
}

fn main() int {
    map := new HashMap<Integer, String>();
    a := new A();
    b := a;
    system.out.println(a.eq(b));

    return 0;
}
