import "util"


fn main() int {
    lst := new util.List();

    for i: int = 0 ; i < 10; i++ {
        lst.add(new util.Integer(i));
    }

    lst.print();

    lst2 := lst.tail();
    lst2.print();

    System.gc();

    lst3 := lst2.prev();
    lst3.print();

    return 0;
}