import "util"


fn main() int {
    lst := new util.List<util.Integer>();

    for i: int = 0 ; i < 20; i++ {
        lst.add(new util.Integer(i));
    }

    //System.println(System.typeName(lst));
    System.println(lst instanceof util.List<util.Integer>);

    //o: Object = lst.get(0);
    //lst2: util.List<String> = lst;

    lst.print();

    //lst2 := lst.tail();
    //lst2.print();

    //System.gc();

    //lst3 := lst2.prev();
    //lst3.print();

    return lst.get(11).value;
}