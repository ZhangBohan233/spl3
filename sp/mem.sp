fn main() int {
    a: String = "asd";

    for i: int = 0; i < 5; i = i + 1 {
        b: String = "bb";
    }
    System.memoryView();
    System.gc();
    System.memoryView();

    return a.length;
}