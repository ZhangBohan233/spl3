import namespace "util"

/*
 * Notice that this function mutate the list.
 */
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

fn map(lst: List, ftn: [Object]->Object) List {
    if lst.size() == 0 {
        return new List();
    } else {
        recLst := map(lst.prev(), ftn);
        recLst.add(ftn(lst.last()));
        return recLst;
    }
}

fn intAdd(a: Integer, b: Integer) Integer {
    return a.add(b);
}

fn sum(lst: List<Integer>) Integer {
    return reduce(lst.copy(), intAdd);
}
