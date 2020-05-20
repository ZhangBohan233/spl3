import "util"


class A<T, K> {
    a: int = 0;
    b: T = "aaa";

}

fn main() int {
    aa := new A<String, util.Integer>();

    return aa.b.length;
}