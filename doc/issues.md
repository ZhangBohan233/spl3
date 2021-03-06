# Issues

### Environment

**ISSUE E01** \
Access definition env from instances.
```
a: int = 5;
class C {
}
c: C = new C();
System.println(c.a);  // this currently prints 5
```

~~**ISSUE E02**~~ **FIXED** \
Function parameters name scope.
```
class C {
    a: int;
    fn init(a: int) {  // this currently causes error
        ...
    }    
}
```

### Class and Inheritance

~~**ISSUE C01**~~ **FIXED** \
Call method overridden in child class from method in superclass
```
class A {
    fn printThis() void {
        system.out.println(this);
    }

    fn toString() String {
        return "an A";
    }
}

class B extends A {
    fn toString() String {
        return "a B";
    }
}
...
(new B()).printThis();  // expect "a B" but currently prints "an A"

```

**ISSUE C02** \
Operator `instanceof` with template classes and interfaces
```
c := new List<String>();
system.out.println(c instanceof Collection<Integer>);  // this prints true, which is not reasonable enough
```
Probable fixes:
1. Change the implementation
2. Remove support of `c instanceof Type<T>`

### Parser

**ISSUE P01** \
Indexing node with dot
```
something.array[index];  // currently causes error
```

**ISSUE P02** \
Unknown parse error of indexing node
```
someFunc((something.array)[index]);
```
