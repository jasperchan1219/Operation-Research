import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	
    public static void test(String[] args){
        ImageSegmentation s;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(args[0])){
            JSONArray all = (JSONArray) jsonParser.parse(reader);
            int count = 0;
            for(Object CaseInList : all){
                count++;
                JSONObject aCase = (JSONObject) CaseInList;
                JSONArray dataArray = (JSONArray) aCase.get("data");

                // JSONObject data = (JSONObject) aCase.get("data");
                // JSONArray dataArray = (JSONArray) data.get("data");

                int testSize = 0; int waSize = 0;
                System.out.print("Case ");
                System.out.println(count);
                for (Object dataObject : dataArray) {
                    JSONObject dataDetails = (JSONObject) dataObject;
                    int N = ((Long) dataDetails.get("N")).intValue();
                    JSONArray imageArray = (JSONArray) dataDetails.get("image");

                    int[][] image = new int[imageArray.size()][];
                    for (int i = 0; i < imageArray.size(); i++) {
                        JSONArray row = (JSONArray) imageArray.get(i);
                        image[i] = new int[row.size()];
                        for (int j = 0; j < row.size(); j++) {
                            image[i][j] = ((Long) row.get(j)).intValue();
                        }
                    }
                    // System.out.println("N: " + N);
                    // System.out.println("Image: " + Arrays.deepToString(image));

                    s = new ImageSegmentation(N, image);

                    int distinctSegments = ((Long) dataDetails.get("DistinctSegments")).intValue();

                    JSONArray largestSegmentArray = (JSONArray) dataDetails.get("LargestSegment");
                    int largestColor = ((Long) largestSegmentArray.get(0)).intValue();
                    int largestSize = ((Long) largestSegmentArray.get(1)).intValue();

                    int ans1 = s.countDistinctSegments();
                    int ans2 = s.findLargestSegment()[0];
                    int ans3 = s.findLargestSegment()[1];

                    testSize++;
                    if(ans1==distinctSegments && ans2==largestColor && ans3==largestSize){
                        // System.out.println("AC");

                    }else{
                        waSize++;
                        // System.out.println("WA");
                    }
                }
                System.out.println("Score: " + (testSize-waSize) + " / " + testSize + " ");

            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static int[] JSONArraytoIntArray(JSONArray x){
        int sizeLim = x.size();
        int MyInt[] = new int[sizeLim];
        for(int i=0;i<sizeLim;i++){
            MyInt[i]= Integer.parseInt(x.get(i).toString());
        }
        return MyInt;
    }

    public static void main(String[] args) {
        test(args);
    }
}