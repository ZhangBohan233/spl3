import namespace "functions"


fn main() int {
    lst := new util.List<util.Integer>();
    for i: int = 0 ; i < 10; i++ {
        lst.add(new util.Integer(i));
    }
    lst.pop();
    lst.print();

    //s: util.Object = sum(lst);
    //System.println(s);

    return lst.size();
}