fn main() int {

    a: int = 1 == 0 ? 333 : 121;
    if false && false {
        System.println(true);
    } else {
        System.println(false);
    }

    b: String = "123213";
    if b != null && b.length > 3 {
        return 3;
    }

    return -a;
}