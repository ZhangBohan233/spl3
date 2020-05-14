fn main() int {
    a: String = "asd";

    c: String = "afasd";
    System.free(c.chars);
    System.free(c);
    System.memoryView();

    b: String;
    for i: int = 0; i < 5; i = i + 1 {
        b = "bb";
    }
    System.memoryView();
    System.gc();
    System.memoryView();
    System.println(System.id(b));

    return a.length;
}