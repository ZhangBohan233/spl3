class A {
    fn test() int {
        return 2;
    }
}


fn main() int {
    a := new A();
    t := a.test;

    b := System.println;
    b(213);

    return t();
}