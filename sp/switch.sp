fn main() int {

    i := 0;

    cond {
        case i > 8 {
            System.println("8");
        } case i > 4 {
            System.println("4");
            fallthrough;
        } case i > 2 {
            System.println("2");
            //fallthrough;
        } default {
            System.println("None");
        }
    }

    return 0;
}