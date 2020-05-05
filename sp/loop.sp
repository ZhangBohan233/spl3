fn main() int {


    for j: int = 0, i: int = 0; j < 10; j = j + 1, i = i + 1 {

        if j == 3 {
            return j;
        }
        System.println(j);
    }

    return 1;
}