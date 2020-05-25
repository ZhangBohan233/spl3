class Node {
    value: int;
}

fn main() int {
    a: Object = new Node();
    a.value = 3;

    c: int = (a as Node).value;

    b: float = 4.45;

    return c;
}