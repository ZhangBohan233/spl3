package interpreter;

import java.util.Arrays;

public class Memory {

    private static final int defaultStackSize = 1024;
    private static final int defaultHeapSize = 256;
    private int stackSize;
    private int heapSize;

    /**
     * Stack pointer.
     */
    private int sp;

    /**
     * Frame pointer.
     */
    private int fp;

    private byte[] stack;
    private SplObject[] heap;

    public Memory() {
        stackSize = defaultStackSize;
        heapSize = defaultHeapSize;
        stack = new byte[stackSize];
        heap = new SplObject[heapSize];
    }

    public void printMemory() {
        System.out.println(Arrays.toString(stack));
    }
}
