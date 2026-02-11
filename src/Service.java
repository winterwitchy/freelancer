public class Service { // data fields for the services
    
    public String name;
    public int T; 
    public int C; 
    public int R; 
    public int E; 
    public int A; 
    public int[] importance;
    public MaxHeap heap;

    public Service(String name, int T, int C, int R, int E, int A) {
        this.name = name;
        this.T = T;
        this.C = C;
        this.R = R;
        this.E = E;
        this.A = A;
        this.heap = new MaxHeap();
        switch (name) {                                     // importance shows the indexes of primary and secondary skills to improve at successful tasks
            case "paint":
                importance = new int[] {4, 3, 0};
                break;
            case "web_dev":
                importance = new int[] {0, 4, 2};
                break;
            case "graphic_design":
                importance = new int[] {2, 1, 4};
                break;
            case "data_entry":
                importance = new int[] {3, 4, 0};
                break;
            case "tutoring":
                importance = new int[] {1, 3, 0};
                break;
            case "cleaning":
                importance = new int[] {3, 4, 1};
                break;
            case "writing":
                importance = new int[] {4, 2, 1};
                break;
            case "photography":
                importance = new int[] {2, 4, 0};
                break;
            case "plumbing":
                importance = new int[] {3, 0, 4};
                break;
            case "electrical":
                importance = new int[] {3, 4, 0};
                break;
        }
    }
}
