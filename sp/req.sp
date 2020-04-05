import "imp/imp.sp" as imp
import namespace "math"

fn main(): int {
    m: imp.Imp = new imp.Imp(7);
    return m.b;
}