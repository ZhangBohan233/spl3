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
