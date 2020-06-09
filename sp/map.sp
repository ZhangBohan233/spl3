import namespace "util"

class A {
    num: int;

    fn init(n: int) void {
        num = n;
    }

    fn eq(o: Object) boolean {
        return o instanceof A && o.num == num;
    }
}

fn main() int {
    a: Object = new A(1);
    print(a instanceof A);

    map := new HashMap<Integer, String>();

    map.put(1, "asd");
    map.put(9, "dfg");

    print(map.get(3));

    return map.size();
}
