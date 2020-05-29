import namespace "functions"

fn addOne(x: util.Integer) util.Integer {
    return new util.Integer(x.value + 1);
}

fn main() int {
    lst := new util.List<util.Integer>();
    for i: int = 0 ; i < 10; i++ {
        lst.add(new util.Integer(i));
    }
    lst.pop();
    lst.print();

    //s: util.Object = sum(lst);
    //System.println(s);

    //lst2 := map(lst, addOne);
    //lst2.print();

    //lst.print();

    return lst.size();
}