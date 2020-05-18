import "util"


fn main() int {
    lst := new util.List();

    lst.add("a");
    lst.add("b");
    lst.add("c");

    lst.print();

    lst2 := lst.tail();
    lst2.print();

    return 0;
}