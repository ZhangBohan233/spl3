import namespace "util"


fn main() int {

    t0: int = system.clock();
    for j: int = 0; j < 100; j = j + 1 {
        //system.out.println(j);
    }
    t1: int = system.clock();
    system.out.println(t1 - t0);

    arr := new int[4];
    for i: int = 0; i < 4; ++i {
        arr[i] = i;
    }

    for ele: int; arr {
        system.out.println(ele);
    }

    lst: List<Integer> = new List<Integer>();
    for i: int = 0; i < 4; ++i {
        lst.add(i);
    }
    ite := lst.iterator();
    while ite.hasNext() {
        print(ite.next());
    }

    for ele: Integer; lst {
        print(ele);
    }

    return arr[1];
}