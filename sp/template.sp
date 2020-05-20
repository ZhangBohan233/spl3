import "util"


class A<T, K> {
    a: int = 0;
    b: T = "aaa";

}

fn main() int {
    aa := new A<String, util.Integer>();

    bb := new A<String, String>() <- {

        fn x() String {
            return b;
        }
    };

    System.println(bb.x());

    return aa.b.length;
}