import "util"


interface Col<T> {

}

class Lst<T> implements Col<T> {
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
    lst := new LL<Integer, String>();

    lst.add(new Integer(77));

    gg := lst.getK();
    system.out.println(lst.getK());
    system.out.println(lst instanceof Lst<String>);

    return lst.get(0).value;
}