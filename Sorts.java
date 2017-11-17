import java.util.*;
import java.lang.Math;

public class Sorts {

    private static class Sorter {

        private static final int NUMB_ITERATIONS = 3;          // Number of times to time each sort

        private Sorter() {

        }

        private void sort(int[] arr, String method) {
            switch (method) {
                case "insertion": InsertionSort(arr); break;
                case "radix": RadixSort(arr); break;
                case "quick": QuickSort(arr); break;
                case "merge": MergeSort(arr); break;
                case "optimal": OptimalSort(arr); break;
            }
        }
    }

    private static void InsertionSort(int[] arr) {
        /* wrapper for actual insertion sort function*/
        insertionsort(arr, 1, arr.length);
    }

    private static void insertionsort(int[] arr, int lo, int hi) {
        /* insertionSort algorithm, used for quicksort optimization below*/
        for (int i = lo; i < hi; i++) {
            // ASSERT a[0..j-1] is sorted

            int key = arr[i];
            int j = i-1;

            // Move elements of arr[0..i-1], that are
            // greater than key, to one position ahead
            // of their current position
            while ( (j > -1) && ( arr [j] > key ) ) {
                arr [j+1] = arr[j];
                j--;
            }
            arr[j+1] = key;
        }
    }

    private static void MergeSort(int[] arr) {
        int len = arr.length;
        int[] tmp = new int[len];
        mergesort(arr, tmp, 0, len - 1);
    }

    private static void mergesort(int[] arr, int[] tmp, int low, int hi) {

        if (low < hi) {     // if array is unsorted
            int center = (low + hi) / 2;
            mergesort(arr, tmp, low, center);            // sort left side
            mergesort(arr, tmp, center + 1, hi);    // sort right side
            merge(arr, tmp, low, center, hi);           // merge together
        }
    }

    private static void merge(int[] arr, int[] tmp, int low, int mid, int hi) {

        // copy array into tmp
        for (int i=low; i <= hi; i++) {
            tmp[i] = arr[i];
        }

        int i = low;
        int k = i;
        int j = mid + 1;

        // copy smallest values from either Left or Right subarray back into original array

        while (i <= mid && j <= hi) {       // while both subarrays are not empty
            if (tmp[i] <= tmp[j]) {         // copy from left half of array
                arr[k++] = tmp[i++];
            } else {                        // copy from right half of array
                arr[k++] = tmp[j++];
            }
        }

        while (i <= mid) {                  // copy leftover elements back into original array
            arr[k] = tmp[i];
            k++;
            i++;
        }
    }

    private static void QuickSort(int[] arr) {
        quicksort(arr, 0, arr.length - 1);
    }

    private static void quicksort(int[] arr, int lo, int hi) {

        int pivIdx = partition(arr, lo, hi);

        if (lo < pivIdx - 1) quicksort(arr, lo, pivIdx - 1);

        if (pivIdx < hi) quicksort(arr, pivIdx, hi);

    }

    private static int partition(int[] arr, int lo, int hi) {

        // experiment with different pivot choosing methods
        int i = lo;
        int j = hi;
        int tmp;
        int pivot = arr[(lo + hi) / 2];     // pivot index

        while (i <= j) {
            while (arr[i] < pivot) i++;     // increment i up to pivot
            while (arr[j] > pivot) j--;     // decrement j down to pivot
            if (i <= j) {
                // swap arr[i] and arr[j]
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }

    private static void RadixSort(int[] arr) {

        /* This is similar to counting sort */

        int digitPlace = 1;
        int len = arr.length;
        int result[] = new int[len]; // resulting array
        // Find the max number for highest number of digits
        int maxNumb = Arrays.stream(arr).max().getAsInt();

        // run loop until it reaches the largest digit place
        while (maxNumb / digitPlace > 0) {

            int count[] = new int[10];

            // Store the count of each digit (these act like keys) in count[]
            for (int i = 0; i < len; i++)
                count[(arr[i] / digitPlace) % 10]++;

            //  Update count[i] to contain actual position of current digit in result[]
            for (int i = 1; i < 10; i++)
                count[i] += count[i-1];

            // Build the resulting array
            for (int i = len-1; i >= 0; i--) {
                result[count[(arr[i] / digitPlace) % 10] - 1] = arr[i];
                count[(arr[i] / digitPlace) % 10]--;
            }

            // Update main array to contains sorted numbers (according to current digit place)
            for (int i = 0; i < len; i++)
                arr[i] = result[i];

            // Increment digit place
            digitPlace *= 10;
        }

    }

    private static void OptimalSort(int[] arr) {
        /* Use a combination of QuickSort and Mergesort */
        int threshold = 9;
        optimalsort(arr, 0, arr.length - 1, threshold);
    }

    private static void optimalsort(int[] a, int lo, int hi, int thresh) {

        /* TODO: Use a stack instead of recursion */

        if (hi-lo < thresh) {
            insertionsort(a, lo, hi+1);
        }

        else {

            int pivIdx = partition(a, lo, hi);

            if (lo < pivIdx - 1) optimalsort(a, lo, pivIdx - 1, thresh);

            if (pivIdx < hi) optimalsort(a, pivIdx, hi, thresh);
        }

    }

    private static int[] randArray(int length, int maxNumb) {
        int i=0;
        int[] arr = new int[length];
        long seed = new Random().nextLong();
        Random randGen = new Random();
        while(i < length) {
            arr[i++] = Math.abs(1 + randGen.nextInt() % maxNumb);
        }
        return arr;
    }

    private static boolean isSorted(int[] arr) {
        for (int i=0; i < arr.length-1; i++) {
            if (arr[i] > arr[i+1]) return false;
        }
        return true;
    }

    public static void main(String[] args) {

        int arrayLen = 500000;
        int maxNumb = 2159957;

        Sorter sorter = new Sorter();
        List<String> methods = new ArrayList<String>(Arrays.asList("radix", "quick", "optimal", "merge")); //"insertion", "merge", "quick"));

        methods.forEach((String mthd) -> {

            int iters = Sorter.NUMB_ITERATIONS;
            List<Double> timings = new ArrayList<>(iters);

            int[] arr = new int[0];

            while(iters > 0) {

                arr = randArray(arrayLen, maxNumb);
                // int[] arr = new int[]{1,4,6,8,11,3,2,5,7,9,10,0};

                if (iters == Sorter.NUMB_ITERATIONS) {
                    System.out.println(String.format("Sorting array of length %d with %s sort", arr.length, mthd));
                    System.out.print("Array is sorted:");
                }

                long start = System.currentTimeMillis();
                sorter.sort(arr, mthd);
                long end = System.currentTimeMillis();

                assert isSorted(arr);

                System.out.print(" " + isSorted(arr));

                double msec = ((end - start));       // convert Long to Double automatically

                timings.add(msec);

                iters--;
            }

            double avg = 0.0;
            for (double t : timings) {
                avg += t;
            }

//            System.out.println(String.format("Dividing %s by %d", Double.toString(avg), timings.size()));

            double best = Collections.min(timings);

            avg /= (double)(timings.size());

            String unit1 = "ms";
            String unit2 = "ms";

            if (avg > 1000.0) {
                avg /= 1000.0;
                unit1 = "s";
            }

            if (best > 1000.0) {
                best /= 1000.0;
                unit2 = "s";
            }

            System.out.println(String.format("\nAverage sorting time: %.4f%s", avg, unit1));
            System.out.println(String.format("Best of %d; %.4f%s", Sorter.NUMB_ITERATIONS, best, unit2));

            int buffer = 100;
            String arrStr = Arrays.toString(arr);

            System.out.println("Resulting array:");

            if (arrStr.length() / 2 < buffer) System.out.println(arrStr);

            else {
                System.out.print(
                        arrStr.substring(0, buffer) + "..."
                                + arrStr.substring(arrStr.lastIndexOf(","), arrStr.length()));
            }
            System.out.println("\n");
        });

    }
}
