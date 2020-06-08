# Issues

### Environment

Access definition env from instances.
```
a: int = 5;
class C {
}
c: C = new C();
System.println(c.a);  // this currently prints 5
```

~~Function parameters name scope.~~  Fixed.
```
class C {
    a: int;
    fn init(a: int) {  // this currently causes error
        ...
    }    
}
```

Inheritance problem
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
