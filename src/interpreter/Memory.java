package interpreter;

import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.SplObject;

import java.util.Arrays;
import java.util.LinkedList;

public class Memory {

    private static final int defaultHeapSize = 256;
    private int heapSize;

    private SplObject[] heap;

    private LinkedList<Integer> available = new LinkedList<>();

    public Memory() {
        heapSize = defaultHeapSize;
        heap = new SplObject[heapSize];

        initAvailable();
    }

    private void initAvailable() {
        for (int i = 0; i < heapSize; ++i) {
            available.addLast(i);
        }
    }

    public Pointer allocate(int size) {
        // TODO
        Integer ptr = available.removeFirst();
        return new Pointer(ptr);
    }

    public void set(Pointer ptr, SplObject obj) {
        heap[ptr.getPtr()] = obj;
    }

    public SplObject get(Pointer ptr) {
        return heap[ptr.getPtr()];
    }

    public Pointer allocateFunction(Function function) {
        Pointer ptr = allocate(1);
        set(ptr, function);
        return ptr;
    }

    public void printMemory() {
        System.out.println(Arrays.toString(heap));
    }
}
