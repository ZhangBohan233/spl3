interface Collection<T> {
    abstract fn size() int;
}

interface Map<K, V> {
    abstract fn size() int;

    abstract fn put(key: K, value: V) void;

    abstract fn get(key: K) V;
}

interface Queue<T> {

}

interface Stack<T> {

}

abstract class AbstractList<T> implements Collection<T> {
    abstract fn get(index: int) T;

    abstract fn add(obj: T) void;

    abstract fn pop() T;

    abstract fn head() T;

    abstract fn tail() List<T>;

    abstract fn last() T;

    abstract fn prev() List<T>;

    abstract fn copy() List<T>;
}

class List<T> extends AbstractList<T> {

    _size: int = 0;
    _array: T[] = new T[8];

    fn init() void {

    }

    fn add(obj: T) void {
        _array[_size++] = obj;
        if _size == _array.length {
            _expand();
        }
    }

    fn get(index: int) T {
        return _array[index];
    }

    fn pop() T {
        return _array[--_size];
    }

    fn size() int {
        return _size;
    }

    fn head() T {
        return get(0);
    }

    fn tail() List<T> {
        sl := new List<T>();
        for i: int = 1; i < _size; i++ {
            sl.add(get(i));
        }
        return sl;
    }

    fn last() T {
        return get(_size - 1);
    }

    fn prev() List<T> {
        sl := new List<T>();
        for i: int = 0; i < _size - 1; i++ {
            sl.add(get(i));
        }
        return sl;
    }

    fn copy() List<T> {
        sl := new List<T>();
        for i: int = 0; i < _size; i++ {
            sl.add(get(i));
        }
        return sl;
    }

    // private methods

    fn _expand() void {
        na := new T[_array.length * 2];
        for i: int = 0; i < _array.length; i++ {
            na[i] = _array[i];
        }
        _array = na;
    }

    fn _collapse() void {

    }

    fn print() void {
        system.out.print("[");
        for i: int = 0; i < _size; i++ {
            system.out.print(_array[i]);
            system.out.print(", ");
        }
        system.out.println("]");
    }
}

class LinkedNode<T> {
    value: T;
    next: LinkedNode<T>;
}

class LinkedList<T> extends AbstractList<T> {

}

/*
Each HashMapNode stores:
-- The key itself
-- The value
-- A link to next node

Keys that have the same (hash_code mod capacity) is stored in a link, occupying one position in the table.
*/
class HashMapNode<K, V> {

    key: K;
    value: V;
    next: HashMapNode<K, V> = null;

    fn init(key: K, value: V) void {
        this.key = key;
        this.value = value;
    }
}

/*
A linked hashed map implementation.

Typically it takes O(1) time to insert, and O(1) time to get.
*/
class HashMap<K, V> implements Map<K, V> {

    const defaultLoadFactor: float = 0.75;
    const defaultCapacity: int = 8;
    _size: int = 0;
    _loadFactor: float = defaultLoadFactor;
    _table: HashMapNode<K, V>[];

    fn init(capacity: int = defaultCapacity) void {
        _table = new HashMapNode<K, V>[capacity];
    }

    fn size() int {
        return _size;
    }

    fn put(key: K, value: V) void {
        hashCode: int = key.hash();
        node: HashMapNode<K, V> = _getNode(hashCode, key);

        if node == null {
            // insert new node
            newNode: HashMapNode<K, V> = new HashMapNode<K, V>(key, value);
            oldHead: HashMapNode<K, V> = _table[hashCode % _table.length];
            newNode.next = oldHead;
            _table[hashCode % _table.length] = newNode;
            _size++;
            currentLf: float = (_size as float) / _table.length;
            if currentLf > _loadFactor {
                _expand();
            }
        } else {
            // update entry
            node.value = value;
        }
    }

    fn get(key: K) V {
        hashCode: int = key.hash();
        node: HashMapNode<K, V> = _getNode(hashCode, key);
        if node == null {
            return null;
        } else {
            return node.value;
        }
    }

    // private methods

    fn _getNode(hashCode: int, key: K) HashMapNode<K, V> {
        index: int = hashCode % _table.length;
        headNode: HashMapNode<K, V> = _table[index];

        node := headNode;
        while node != null {
            //print(node.key);
            //print(node.key.eq(key));
            if node.key.hash() == hashCode && node.key.eq(key) {
                return node;
            }
            node = node.next;
        }
        return null;
    }

    fn _expand() void {

    }

    fn _collapse() void {

    }

}

print := system.out.println;
