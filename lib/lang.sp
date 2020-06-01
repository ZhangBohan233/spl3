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

class Error {
    errno: int;
    message: String;

    fn init(errno: int, message: String = null) void {
        this.errno = errno;
        this.message = message;
    }
}
