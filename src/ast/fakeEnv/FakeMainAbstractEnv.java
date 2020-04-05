package ast.fakeEnv;

public abstract class FakeMainAbstractEnv extends FakeEnv {
    protected FakeMainAbstractEnv(FakeEnv outer) {
        super(outer);
    }
}
