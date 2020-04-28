class Object {
    fn hashCode() int {
        return 0;
    }

    fn toString() String {
        return "Object";
    }
}

class String {
    const chars: char[];
    const length: int;

    fn init(chs: char[]) void {
        chars = chs;
        length = chs.length;
    }
}
