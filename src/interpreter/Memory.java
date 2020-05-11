package interpreter;

import interpreter.primitives.Pointer;
import interpreter.splObjects.Function;
import interpreter.splObjects.SplCallable;
import interpreter.splObjects.SplObject;

import java.util.Arrays;

public class Memory {

    public static final int INTERVAL = 1;
    private static final int defaultHeapSize = 256;
    private int heapSize;

    private SplObject[] heap;

    private AvailableList available;

    public Memory() {
        heapSize = defaultHeapSize;
        heap = new SplObject[heapSize];

        initAvailable();
    }

    private void initAvailable() {
        available = new AvailableList(heapSize);
    }

    public Pointer allocate(int size) {
        int ptr;
        if (size == 1) {
            ptr = available.firstAva();
        } else {
            ptr = available.findAva(size);
        }
        return new Pointer(ptr);
    }

    public void set(Pointer ptr, SplObject obj) {
        heap[ptr.getPtr()] = obj;
    }

    public void set(int addr, SplObject obj) {
        heap[addr] = obj;
    }

    public SplObject get(Pointer ptr) {
        return heap[ptr.getPtr()];
    }

    public SplObject get(int addr) {
        return heap[addr];
    }

    public Pointer allocateFunction(SplCallable function) {
        Pointer ptr = allocate(1);
        set(ptr, function);
        return ptr;
    }

    public Pointer allocateObject(SplObject object) {
        Pointer ptr = allocate(1);
        set(ptr, object);
        return ptr;
    }

    public void printMemory() {
        System.out.println(Arrays.toString(heap));
    }

    public static void main(String[] args) {
        AvailableList availableList = new AvailableList(16);
        System.out.println(availableList.findAva(3));
        System.out.println(availableList);
        availableList.addAva(0, 1);
        System.out.println(availableList);
        System.out.println(availableList.findAva(2));
        System.out.println(availableList);
    }

    private static class AvailableList {

        private LnkNode head;

        AvailableList(int size) {
            LnkNode last = null;
            for (int i = size - 1; i >= 0; --i) {
                LnkNode node = new LnkNode();
                node.next = last;
                node.value = i;
                last = node;
            }
            head = last;
        }

        int firstAva() {
            LnkNode node = head.next;
            head.next = node.next;
            return node.value;
        }

        void addAva(int ptr, int intervalsCount) {
            LnkNode h = head;
            while (h.next.value < ptr) {
                h = h.next;
            }
            LnkNode insertHead = h.next;
            for (int i = intervalsCount - 1; i >= 0; --i) {
                LnkNode n = new LnkNode();
                n.next = insertHead;
                n.value = ptr + i * INTERVAL;
                insertHead = n;
            }
            h.next = insertHead;

        }

        int findAva(int size) {
            LnkNode h = head;
            while (h.next != null) {
                int i = 0;
                LnkNode cur = h.next;
                for (; i < size - 1; ++i) {
                    LnkNode next = cur.next;
                    if (next == null || next.value != cur.value + INTERVAL) break;
                    cur = next;
                }
                if (i == size - 1) {
                    LnkNode foundNode = h.next;
                    int found = foundNode.value;
                    h.next = cur.next;
                    return found;
                } else {
                    h = cur;
                }
            }
            return -1;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("Ava[");
            for (LnkNode n = head; n != null; n = n.next) {
                stringBuilder.append(n.value).append("->");
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        private static class LnkNode {
            LnkNode next;
            int value;
        }
    }
}

