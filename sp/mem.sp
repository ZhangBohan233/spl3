fn main() int {
    a: String = "asd";


    b: String;
    for i: int = 0; i < 5; i = i + 1 {
        b = "bb";
    }
    System.memoryView();
    System.gc();
    System.memoryView();
    System.println(System.id(b));

    return b.length;
}