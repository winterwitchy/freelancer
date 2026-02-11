import java.util.ArrayList;
public class MaxHeap {
    private ArrayList<Freelancer> heap;

    public MaxHeap() {
        heap = new ArrayList<>();
    }


    public void insert(Freelancer freelancer) {
        heap.add(freelancer);
        percolateUp(heap.size() - 1);
    }

    public void cleanMonth() {                      // monthly datas are omitted
        for (int i = 0; i < heap.size(); i++) {
            Freelancer freeL = heap.get(i);
            if (freeL != null) {
                freeL.mcancelled = 0;
                freeL.mcompleted = 0;
            }
        }
    }

    public void swap(int i, int j) {
        Freelancer temp = heap.get(i);
        heap.set(i,  heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public Freelancer extractMax() {
        if (heap.size() == 0) {
            return null;
        }
        Freelancer max = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        percolateDown(0);
        return max;
    }

    public void percolateDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int largest = index;
        if (leftChild < heap.size() && heap.get(leftChild).compareTo(heap.get(largest)) > 0) {largest = leftChild;}
        if (rightChild < heap.size() && heap.get(rightChild).compareTo(heap.get(largest)) > 0) {largest = rightChild;}
        if (largest != index) {
            swap(index, largest);
            percolateDown(largest);
        }
    }

    public ArrayList<Freelancer> getTopK(int k, ArrayList<String> blacklistedFreelancers) {
        if (isEmpty() || k <= 0) {
            return new ArrayList<>();
        }

        ArrayList<Freelancer> topK = new ArrayList<>();
        ArrayList<Freelancer> tempHeap = new ArrayList<>();

        int eligible = 0;
        int i = 0;
        while (!isEmpty() && eligible < k) {
            Freelancer max = extractMax();
            if (max == null) break;

            if (!max.employed && !blacklistedFreelancers.contains(max.id)) {
                if (i != 0) {tempHeap.add(max);}
                i++;
                topK.add(max);
                eligible++;
            } else {
                tempHeap.add(max);
            }
        }

        for (Freelancer freelancer : tempHeap) {
            insert(freelancer);
        }
        return topK;
    }

    public void percolateUp(int index) {
        int parentIndex = (index - 1) / 2;
        if (index > 0 && heap.get(index).compareTo(heap.get(parentIndex)) > 0) {
            swap(index, parentIndex);
            percolateUp(parentIndex);
        }
    }

    public void remove(Freelancer freeL) {
        int index = heap.indexOf(freeL);
        if (index == -1) {return;}
        int end = heap.size() - 1;

        if (index == -1) {
            return;
        }
        
        if (index == end) {
            heap.remove(end);
            return;
        }
        swap(index, end);
        heap.remove(end);

        int parentIndex = (index - 1) / 2;

        if (index > 0 && heap.get(index).compareTo(heap.get(parentIndex)) > 0) {
            percolateUp(index);
        } else {
        percolateDown(index);
        }
    }
}