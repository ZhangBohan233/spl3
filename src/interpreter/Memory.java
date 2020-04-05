package interpreter;

import java.util.Arrays;

public class Memory {

    private static final int defaultHeapSize = 256;
    private int heapSize;

    private SplObject[] heap;

    public Memory() {
        heapSize = defaultHeapSize;
        heap = new SplObject[heapSize];
    }

    public void printMemory() {
        System.out.println(Arrays.toString(heap));
    }
}
