package klargesttest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

    // implementations

    /**
     * Sorts the whole list and gives the k largest elements
     */
    static <T extends Comparable<? super T>> Collection<T> kLargestSort(Collection<T> list, int k) {
        List<T> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        int n = sorted.size();
        return sorted.subList(n - k, n);
    }

    /**
     * Keeps the k largest encountered elements in a heap.
     */
    static <T extends Comparable<? super T>> Collection<T> kLargestHeap(Collection<T> list, int k) {
        PriorityQueue<T> q = new PriorityQueue<>(k);
        for (T v: list) {
            if (q.size() < k) {
                q.offer(v);
            } else if (q.peek().compareTo(v) < 0) {
                q.poll();
                q.offer(v);
            }
        }
        return q;
    }

    /**
     * Keeps the k largest encountered elements in a heap. Size checking refactored out.
     */
    static <T extends Comparable<? super T>> Collection<T> kLargestHeapIterator(Collection<T> list, int k) {
        PriorityQueue<T> q = new PriorityQueue<>(k);
        Iterator<T> it = list.iterator();
        for (int i = 0; i < k && it.hasNext(); ++i) {
            q.add(it.next());
        }
        while (it.hasNext()) {
            T v = it.next();
            if (q.peek().compareTo(v) < 0) {
                q.poll();
                q.offer(v);
            }
        }
        return q;
    }

    /**
     * Keeps the k largest encountered elements in an unsorted list.
     */
    static <T extends Comparable<? super T>> Collection<T> kLargestListUnsorted(Collection<T> list, int k) {
        List<T> kLargest = new ArrayList<>(k);
        int minIndex = 0;
        Iterator<T> it = list.iterator();
        if (it.hasNext()) {
            kLargest.add(it.next());
        }
        for (int i = 1; i < k && it.hasNext(); ++i) {
            T v = it.next();
            if (kLargest.get(minIndex).compareTo(v) > 0) {
                minIndex = i;
            }
            kLargest.add(v);
        }
        while (it.hasNext()) {
            T v = it.next();
            if (kLargest.get(minIndex).compareTo(v) < 0) {
                kLargest.set(minIndex, v);
                // find new minimum
                minIndex = 0;
                for (int i = 1; i < k; ++i) {
                    if (kLargest.get(minIndex).compareTo(kLargest.get(i)) > 0) {
                        minIndex = i;
                    }
                }
            }
        }
        return kLargest;
    }

    /**
     * Keeps the k largest encountered elements in a sorted list.
     */
    static <T extends Comparable<? super T>> Collection<T> kLargestListSorted(Collection<T> list, int k) {
        List<T> kLargest = new ArrayList<>(k);
        Iterator<T> it = list.iterator();
        if (it.hasNext()) {
            kLargest.add(it.next());
        }
        for (int i = 1; i < k && it.hasNext(); ++i) {
            T v = it.next();
            kLargest.add(v);
            // swap from largest
            for (int j = i - 1; j >= 0; --j) {
                T t = kLargest.get(j);
                if (t.compareTo(v) < 0) {
                    break;
                }
                kLargest.set(j + 1, t);
                kLargest.set(j, v);
            }
        }
        while (it.hasNext()) {
            T v = it.next();
            if (kLargest.get(0).compareTo(v) > 0) {
                continue;
            }
            kLargest.set(0, v);
            // swap from smallest
            for (int i = 1; i < k; ++i) {
                T t = kLargest.get(i);
                if (t.compareTo(v) > 0) {
                    break;
                }
                kLargest.set(i - 1, t);
                kLargest.set(i, v);
            }
        }
        return kLargest;
    }

    // helpers

    static String format(String fmt, Object... args) {
        return String.format(Locale.US, fmt, args);
    }

    static IllegalStateException illegalState(String fmt, Object... args) {
        String msg = format(fmt, args);
        return new IllegalStateException(msg);
    }

    static void println(String fmt, Object... args) {
        String msg = format(fmt, args);
        System.out.println(msg);
    }

    static <T extends Comparable<? super T>> void testSameElements(Collection<T> expected, Collection<T> actual) {
        if (expected.size() != actual.size()) {
            throw illegalState("expected size <%d>, was <%d>", expected.size(), actual.size());
        }
        List<T> ex = new ArrayList<>(expected);
        List<T> ac = new ArrayList<>(actual);
        Collections.sort(ex);
        Collections.sort(ac);
        for (int i = 0; i < ex.size(); ++i) {
            T e = ex.get(i);
            T a = ac.get(i);
            if (!e.equals(a)) {
                throw illegalState("expected element at %d to be %s, was %s", i, e, a);
            }
        }
    }

    static List<Integer> randomList(int max, int size) {
        List<Integer> list = new ArrayList<>(size);
        Random random = new Random(0);
        for (int i = 0; i < size; ++i) {
            Integer v = random.nextInt(max);
            list.add(v);
        }
        return list;
    }

    static double elapsedSeconds(long t0, long t1) {
        double dt = t1 - t0;
        double t = dt * 1e-3;
        return t;
    }

    // testers

    static void kLargestSortPerformance(int warmups, int iterations, List<Integer> list, int k) {
        int n = list.size();
        for (int i = 0; i < warmups; ++i) {
            kLargestSort(list, k);
        }
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            kLargestSort(list, k);
        }
        long t1 = System.currentTimeMillis();
        println("%d iterations of kLargestSort with n=%d, k=%d executed in %.2f seconds",
                iterations, n, k, elapsedSeconds(t0, t1));
    }

    static void kLargestHeapPerformance(int warmups, int iterations, List<Integer> list, int k) {
        int n = list.size();
        for (int i = 0; i < warmups; ++i) {
            kLargestHeap(list, k);
        }
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            kLargestHeap(list, k);
        }
        long t1 = System.currentTimeMillis();
        println("%d iterations of kLargestHeap with n=%d, k=%d executed in %.2f seconds",
                iterations, n, k, elapsedSeconds(t0, t1));
    }

    static void kLargestHeapIteratorPerformance(int warmups, int iterations, List<Integer> list, int k) {
        int n = list.size();
        for (int i = 0; i < warmups; ++i) {
            kLargestHeapIterator(list, k);
        }
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            kLargestHeapIterator(list, k);
        }
        long t1 = System.currentTimeMillis();
        println("%d iterations of kLargestHeapIterator with n=%d, k=%d executed in %.2f seconds",
                iterations, n, k, elapsedSeconds(t0, t1));
    }

    static void kLargestListUnsortedPerformance(int warmups, int iterations, List<Integer> list, int k) {
        int n = list.size();
        for (int i = 0; i < warmups; ++i) {
            kLargestListUnsorted(list, k);
        }
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            kLargestListUnsorted(list, k);
        }
        long t1 = System.currentTimeMillis();
        println("%d iterations of kLargestListUnsorted with n=%d, k=%d executed in %.2f seconds",
                iterations, n, k, elapsedSeconds(t0, t1));
    }

    static void kLargestListSortedPerformance(int warmups, int iterations, List<Integer> list, int k) {
        int n = list.size();
        for (int i = 0; i < warmups; ++i) {
            kLargestListSorted(list, k);
        }
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < iterations; ++i) {
            kLargestListSorted(list, k);
        }
        long t1 = System.currentTimeMillis();
        println("%d iterations of kLargestListSorted with n=%d, k=%d executed in %.2f seconds",
                iterations, n, k, elapsedSeconds(t0, t1));
    }

    // main

    public static void main(String[] args) {
        int n = 1000;
        int k = 10;
        List<Integer> list = randomList(100, n);
        // test correctness
        testSameElements(kLargestSort(list, k), kLargestHeap(list, k));
        testSameElements(kLargestSort(list, k), kLargestHeapIterator(list, k));
        testSameElements(kLargestSort(list, k), kLargestListUnsorted(list, k));
        testSameElements(kLargestSort(list, k), kLargestListSorted(list, k));
        // test performance
        int warmups = 100;
        int iterations = 10000;
        println("--- test all ---");
        kLargestSortPerformance(warmups, iterations, list, k);
        kLargestHeapPerformance(warmups, iterations, list, k);
        kLargestHeapIteratorPerformance(warmups, iterations, list, k);
        kLargestListUnsortedPerformance(warmups, iterations, list, k);
        kLargestListSortedPerformance(warmups, iterations, list, k);
        // test non sorting performance
        iterations = 1000000;
        println("--- test non sorting ---");
        kLargestHeapPerformance(warmups, iterations, list, k);
        kLargestHeapIteratorPerformance(warmups, iterations, list, k);
        kLargestListUnsortedPerformance(warmups, iterations, list, k);
        kLargestListSortedPerformance(warmups, iterations, list, k);
        println("--- test non sorting large k ---");
        iterations = 100000;
        k = 100;
        kLargestHeapIteratorPerformance(warmups, iterations, list, k);
        kLargestListUnsortedPerformance(warmups, iterations, list, k);
        println("--- done ---");
    }
}
