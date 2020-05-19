abstract class Number {

}

class Integer extends Number {
    const value: int;

    fn init(v: int) void {
        value = v;
    }

    fn add(o: Object) Integer {
        return new Integer(value + o.value);
    }

    fn toString() String {
        return System.string(value);
    }
}

interface Collection {
    abstract fn size() int;
}

abstract class AbstractList implements Collection {
    abstract fn get(index: int) Object;

    abstract fn add(obj: Object) void;
}

class List extends AbstractList {

    _size: int = 0;
    _array: Object[] = new Object[8];

    fn init() void {

    }

    fn add(obj: Object) void {
        _array[_size++] = obj;
        if _size == _array.length {
            expand();
        }
    }

    fn get(index: int) Object {
        return _array[index];
    }

    fn pop() Object {
        return _array[--_size];
    }

    fn size() int {
        return _size;
    }

    fn head() Object {
        return get(0);
    }

    fn tail() List {
        sl := new List();
        for i: int = 1; i < _size; i++ {
            sl.add(get(i));
        }
        return sl;
    }

    fn last() Object {
        return get(_size - 1);
    }

    fn prev() List {
        sl := new List();
        for i: int = 0; i < _size - 1; i++ {
            sl.add(get(i));
        }
        return sl;
    }

    // private methods

    fn expand() void {
        na := new Object[_array.length * 2];
        for i: int = 0; i < _array.length; i++ {
            na[i] = _array[i];
        }
        _array = na;
    }

    fn collapse() void {

    }

    fn print() void {
        System.print("[");
        for i: int = 0; i < _size; i++ {
            System.print(_array[i]);
            System.print(", ");
        }
        System.println("]");
    }
}

fn fade(n : int) int {
    return n + 1;
}
