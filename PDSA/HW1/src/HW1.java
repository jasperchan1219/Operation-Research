import java.util.Arrays;
import edu.princeton.cs.algs4.Stack;

class ImageSegmentation {
    
    private int segmentCount;
    private int largestColor;
    private int largestSize;
    private int[][] image;
    private int[] parent;
    private int[] size;

    public ImageSegmentation(int N, int[][] inputImage) {
        // Initialize a N-by-N image
        image = inputImage;
        int rows = N;
        int cols = N;
        parent = new int[rows * cols];
        for(int i = 0; i < rows * cols; i++) {
            parent[i] = i;
        }
        size = new int[rows * cols];
        Arrays.fill(size, 1);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // check neighboring pixels
                if (i > 0 && image[i][j] == image[i - 1][j] && image[i][j] != 0) {
                    union(parent[i * cols + j], parent[(i-1) * cols + j]);
                }
                if (j > 0 && image[i][j] == image[i][j - 1] && image[i][j] != 0) {
                    union(parent[i * cols + j], parent[i * cols + (j-1)]);
                }
            }
        }
    }

    public int countDistinctSegments() {
        // Count the number of distinct segments in the image.
        // Pick all elements one by one
        for (int i = 0; i < size.length; i++) {
            if (size[i] > 1) {
                segmentCount++;
            }
            else if(size[i] == 1 && image[i / image.length][i % image.length] != 0){
                segmentCount++;
            }
        }
        return segmentCount;
    }

    public int[] findLargestSegment() {
        // Find the largest connected segment and return an array
        // containing the number of pixels and the color of the segment.
        largestSize = largest(size);
        for (int i = 0; i < size.length; i++) {
            if (size[i] == largestSize) {
                if (largestColor == 0 || image[i / image.length][i % image.length] < largestColor) {
                    largestColor = image[i / image.length][i % image.length];
                }
            }
        }
        return new int[]{largestSize, largestColor};
    }

    public int largest(int arr[]) 
    { 
        int i;
        // Initialize maximum element 
        int max = arr[0]; 
        // Traverse array elements from second and 
        // compare every element with current max 
        for (i = 1; i < arr.length; i++) 
            if (arr[i] > max) 
                max = arr[i];
        return max; 
    }
    public int find(int i) {
        while (i != parent[i]) {
        parent[i] = parent[parent[i]];
        i = parent[i];
        }
        return i;
    }
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);
        if(i == j) return;
        if (size[i] < size[j]) {
            parent[i] = j;
            size[j] += size[i];
            size[i] = 0;
        }
        else {
            parent[j] = i;
            size[i] += size[j];
            size[j] = 0;
        }
    }
    // private object mergeSegment (object XXX, ...){ 
        // Maybe you can use user-defined function to
        // facilitate you implement mergeSegment method. 
    // }

    public static void main(String args[]) {

        // Example 1:
        int[][] inputImage1 = {
            {0, 0, 0},
            {0, 1, 1},
            {0, 0, 1}
        };
        System.out.println("Example 1:");

        ImageSegmentation s = new ImageSegmentation(3, inputImage1);
        System.out.println(Arrays.toString(s.parent));
        System.out.println(Arrays.toString(s.size));
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        int[] largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);


        // Example 2:
        int[][] inputImage2 = {
               {0, 0, 0, 3, 0},
               {0, 2, 3, 3, 0},
               {1, 2, 2, 0, 0},
               {1, 2, 2, 1, 1},
               {0, 0, 1, 1, 1}
        };

        System.out.println("\nExample 2:");

        s = new ImageSegmentation(5, inputImage2);
        System.out.println(Arrays.toString(s.parent));
        System.out.println(Arrays.toString(s.size));
        System.out.println("Number of Distinct Segments: " + s.countDistinctSegments());

        largest = s.findLargestSegment();
        System.out.println("Size of the Largest Segment: " + largest[0]);
        System.out.println("Color of the Largest Segment: " + largest[1]);

    }

}