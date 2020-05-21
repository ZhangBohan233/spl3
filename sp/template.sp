import "util"


class Lst<T> {
    arr : T[] = new T[8];
    size: int = 0;

    fn add(item: T) void {
        arr[size++] = item;
    }

    fn get(index: int) T {
        return arr[index];
    }
}

class LL<T, K> extends Lst<T> {
    val: K;

    fn setK(k: K) void {
        val = k;
    }

    fn getK() K {
        return val;
    }
}

fn main() int {
    lst := new LL<util.Integer, Lst<String> >();

    lst.add(new util.Integer(77));

    ll: Lst<String> = new Lst<String>();
    ll.add("fff");
    lst.setK(ll);

    gg := lst.getK();
    System.println(lst.getK().get(0));

    return lst.get(0).value;
}