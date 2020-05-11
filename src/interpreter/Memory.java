package interpreter;

import interpreter.env.Environment;
import interpreter.primitives.Pointer;
import interpreter.splObjects.*;
import interpreter.types.ArrayType;
import interpreter.types.PointerType;
import interpreter.types.Type;
import interpreter.types.TypeValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Memory {

    public static final int INTERVAL = 1;
    private static final int DEFAULT_HEAP_SIZE = 256;
    private int heapSize;
    private int stackSize;

    private final SplObject[] heap;

    private AvailableList available;

    public Memory() {
        heapSize = DEFAULT_HEAP_SIZE;
        heap = new SplObject[heapSize];

        initAvailable();
    }

    public void increaseStack() {
        stackSize++;
    }

    public void decreaseStack() {
        stackSize--;
    }

    private void initAvailable() {
        available = new AvailableList(heapSize);
    }

    public Pointer allocate(int size, Environment env) {
        int ptr = innerAllocate(size);
        if (ptr == -1) {
            gc(env);
            ptr = innerAllocate(size);
            if (ptr == -1)
                throw new MemoryError("No memory available. ");
        }
        return new Pointer(ptr);
    }

    private int innerAllocate(int size) {
        int ptr;
        if (size == 1) {
            ptr = available.firstAva();
        } else {
            ptr = available.findAva(size);
        }
        return ptr;
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

    public void free(Pointer ptr, int length) {
//        System.out.println(ptr);
//        System.out.println(available);
        available.addAva(ptr.getPtr(), length);
        set(ptr, null);
//        System.out.println(available);
    }

    public void gc(Environment env) {
        markFalseMem();
        markTrueEnv(env);
        garbageCollect();
    }

    private void garbageCollect() {
        int occupied = 0;
        for (int p = 1; p < heapSize; ++p) {
            SplObject obj = get(p);
            if (obj != null) {
                if (obj.gcCount > 0) occupied++;
                else set(p, null);
            }
        }
        System.out.println(occupied);
        System.out.println(heapSize - occupied);
    }

    private void markFalseMem() {
        for (SplObject obj : heap) {
            if (obj != null) obj.gcCount = 0;
        }
    }

    private void markTrueEnv(Environment env) {
        if (env == null) return;

        Set<TypeValue> attr = env.attributes();
        for (TypeValue tv : attr) {
            if (!tv.getType().isPrimitive()) {
                Pointer ptr = (Pointer) tv.getValue();
                SplObject obj = get(ptr);
                PointerType type = (PointerType) tv.getType();
                markTrueSplObj(type, obj, ptr.getPtr());
            }
        }
        markTrueEnv(env.outer);
    }

    private void markTrueSplObj(PointerType type, SplObject obj, int objAddr) {
        if (obj == null) return;
        obj.gcCount += 1;
        if (obj.gcCount > 1) return;  // already collected
//        System.out.println(obj);

        switch (type.getPointerType()) {
            case PointerType.ARRAY_TYPE:
                int arrBegin = objAddr + 1;
                SplArray array = (SplArray) obj;
                Type valueType = ((ArrayType) type).getEleType();
                if (!valueType.isPrimitive()) {
                    PointerType valuePtrType = (PointerType) valueType;
                    for (int i = 0; i < array.length; i++) {
                        int p = arrBegin + i;
                        ReadOnlyPrimitiveWrapper ele = (ReadOnlyPrimitiveWrapper) get(p);
                        if (ele != null) {
                            ele.gcCount += 1;
                            SplObject pointed = get((Pointer) ele.value);
                            markTrueSplObj(valuePtrType, pointed, p);
                        }
                    }
                } else {
                    for (int i = 0; i < array.length; i++) {
                        int p = arrBegin + i;
                        ReadOnlyPrimitiveWrapper ele = (ReadOnlyPrimitiveWrapper) get(p);
                        if (ele != null) {
                            ele.gcCount += 1;
                        }
                    }
                }
                break;
            case PointerType.MODULE_TYPE:
                SplModule module = (SplModule) obj;
                markTrueEnv(module.getEnv());
                break;
            case PointerType.CLASS_TYPE:
                if (obj instanceof Instance) {
                    Instance instance = (Instance) obj;
                    markTrueEnv(instance.getEnv());
                }
                break;
        }
    }

    public Pointer allocateFunction(SplCallable function, Environment env) {
        Pointer ptr = allocate(1, env);
        set(ptr, function);
        return ptr;
    }

    public Pointer allocateObject(SplObject object, Environment env) {
        Pointer ptr = allocate(1, env);
        set(ptr, object);
        return ptr;
    }

    public void printMemory() {
        System.out.println(Arrays.toString(heap));
    }

    public String memoryView() {
        return Arrays.toString(heap);
    }

    public String availableView() {
        return available.size() + ": " + available.toString();
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

        private final LnkNode head;

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
            if (node == null) return -1;
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
            if (insertHead.value >= insertHead.next.value) {
                throw new MemoryError("Heap memory collision. ");
            }
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

        /**
         * @return count of available memory slots, does not include the head.
         */
        public int size() {
            int s = -1;
            for (LnkNode n = head; n != null; n = n.next) {
                s++;
            }
            return s;
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

    public static class MemoryError extends SplException {
        MemoryError(String msg) {
            super(msg);
        }
    }

}
