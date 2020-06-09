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
    map := new HashMap<Integer, String>();

    map.put(1, "asd");
    map.put(9, "dfg");
    map.put(1, "3asd");

    print(map.get(1));

    return map.size();
}
