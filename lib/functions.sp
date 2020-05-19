import namespace "util"


fn reduce(lst: List, ftn: [Object, Object]->Object) Object {
    if lst.size() == 1 {
        return lst.get(0);
    } else {
        last1 := lst.pop();
        last2 := lst.pop();
        res := ftn(last1, last2);
        lst.add(res);
        return reduce(lst, ftn);
    }
}

fn intAdd(a: Object, b: Object) Object {
    return a.add(b);
}

fn sum(lst: List) Object {
    return reduce(lst, intAdd);
}
