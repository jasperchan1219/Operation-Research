import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.UF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

class OutputFormat2{
    double[][] box;
    double iou;
    double[][] answer;
}



class Event implements Comparable<Event>{
    double x;
    double[] box;
    boolean isStart;
    int id;
    public Event(double x, double[] box, boolean isStart, int id){
        this.x = x;
        this.box = box;
        this.isStart = isStart;
        this.id = id;
    }
    public int compareTo(Event other){
        if(this.x < other.x) return -1;
        if(this.x > other.x) return 1;
        if(this.isStart && !other.isStart) return -1;
        if(!this.isStart && other.isStart) return 1;
        return 0;
    }
}

class ImageMerge {
    MinPQ<Event> pq;
    double[][] bbs;
    double iou_thresh;
    List<Integer> results;
    double[][] ans;
    public double[][] mergeBox()
    {
        IntervalST<Double, Integer> ist = new IntervalST<>();
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(bbs.length);
        while(!pq.isEmpty()){
            Event cur = pq.delMin();
            double[] box = cur.box;
            int id = cur.id;
            if(cur.isStart){
                List<Integer> intersect = ist.intersects(box[1], box[1]+box[3]);
                ist.put(box[1], box[1]+box[3], id);
                if(!intersect.isEmpty()){
                    for(int i : intersect){
                        //check iou
                        if(get_iou(bbs[id],bbs[i]) >= iou_thresh){
                            uf.union(id, i);
                        }
                    }
                }
            }
            else{
                ist.delete(box[1], box[1]+box[3]);
            }
        }
        Map<Integer, Set<double[]>> map = new HashMap<>();
        for(int i = 0; i < bbs.length; i++){
            int root = uf.find(i);
            if(!map.containsKey(root)){
                map.put(root, new HashSet<>());
            }
            map.get(root).add(bbs[i]);
        }
        List<double[]> results = new ArrayList<>();
        for(Set<double[]> set : map.values()){
            double x1 = Double.MAX_VALUE;
            double y1 = Double.MAX_VALUE;
            double x2 = Double.MIN_VALUE;
            double y2 = Double.MIN_VALUE;
            for(double[] box : set){
                x1 = Math.min(x1, box[0]);
                y1 = Math.min(y1, box[1]);
                x2 = Math.max(x2, box[0]+box[2]);
                y2 = Math.max(y2, box[1]+box[3]);
            }
            results.add(new double[]{x1, y1, x2-x1, y2-y1});
        }
        // Sort the bounding boxes by the x1, y1, width, height
        results.sort(new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                if(o1[0] < o2[0]) return -1;
                if(o1[0] > o2[0]) return 1;
                if(o1[1] < o2[1]) return -1;
                if(o1[1] > o2[1]) return 1;
                if(o1[2] < o2[2]) return -1;
                if(o1[2] > o2[2]) return 1;
                if(o1[3] < o2[3]) return -1;
                if(o1[3] > o2[3]) return 1;
                return 0;
            }
        });

        ans = new double[results.size()][4];
        for(int i = 0; i < results.size(); i++){
            ans[i] = results.get(i);
        }
        

        return ans;
        
        //return merged bounding boxes just as input in the format of 
        //[up_left_x,up_left_y,width,height]
    }
    public ImageMerge(double[][] bbs, double iou_thresh){
        this.bbs = bbs;
        this.iou_thresh = iou_thresh;
        pq = new MinPQ<>();
        for(int i = 0; i < bbs.length; i++){
            double[] box = bbs[i];
            pq.insert(new Event(box[0], box, true, i));
            pq.insert(new Event(box[0]+box[2], box, false, i));
        }
        //bbs(bounding boxes): [up_left_x,up_left_y,width,height]
        //iou_threshold:          [0.0,1.0]
    }
    public static void draw(double[][] bbs)
    {
        // ** NO NEED TO MODIFY THIS FUNCTION, WE WON'T CALL THIS **
        // ** DEBUG ONLY, USE THIS FUNCTION TO DRAW THE BOX OUT** 
        StdDraw.setCanvasSize(960,540);
        for(double[] box : bbs)
        {
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public double get_iou(double[] box1, double[] box2){
        double x1 = Math.max(box1[0], box2[0]);
        double y1 = Math.max(box1[1], box2[1]);
        double x2 = Math.min(box1[0]+box1[2], box2[0]+box2[2]);
        double y2 = Math.min(box1[1]+box1[3], box2[1]+box2[3]);
        double intersection = Math.max(0, x2-x1) * Math.max(0, y2-y1);
        double area1 = box1[2] * box1[3];
        double area2 = box2[2] * box2[3];
        double union = area1 + area2 - intersection;
        return intersection/union;
    }
    public static void main(String[] args) {
        ImageMerge sol = new ImageMerge(
                new double[][]{
                        {0.02,0.01,0.1,0.05},{0.0,0.0,0.1,0.05},{0.04,0.02,0.1,0.05},{0.06,0.03,0.1,0.05},{0.08,0.04,0.1,0.05},
                        {0.24,0.01,0.1,0.05},{0.20,0.0,0.1,0.05},{0.28,0.02,0.1,0.05},{0.32,0.03,0.1,0.05},{0.36,0.04,0.1,0.05},
                },
                0.5
        );
        double[][] temp = sol.mergeBox();
        ImageMerge.draw(temp);
    } 
}


class test{
    private static boolean deepEquals(double[][] test_ans, double[][] user_ans)
    {
        if(test_ans.length != user_ans.length)
            return false;
        for(int i = 0; i < user_ans.length; ++i)
        {
            if(user_ans[i].length != test_ans[i].length)
                return false;
            for(int j = 0; j < user_ans[i].length; ++j)
            {
                if(Math.abs(test_ans[i][j]-user_ans[i][j]) > 0.00000000001)
                    return false;
            }
        }
        return true;
    }
    public static void draw(double[][] user, double[][] test)
    {
        StdDraw.setCanvasSize(960,540);
        for(double[] box : user)
        {
            StdDraw.setPenColor(StdDraw.BLACK);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
        for(double[] box : test)
        {
            StdDraw.setPenColor(StdDraw.BOOK_RED);
            double half_width = (box[2]/2.0);
            double half_height = (box[3]/2.0);
            double center_x = box[0]+ half_width;
            double center_y = box[1] + half_height;
            //StdDraw use y = 0 at the bottom, 1-center_y to flip
            
            StdDraw.rectangle(center_x, 1-center_y, half_width,half_height);
        }
    }
    public static void main(String[] args) throws InterruptedException
    {
        Gson gson = new Gson();
        OutputFormat2[] datas;
        OutputFormat2 data;
        int num_ac = 0;

        double[][] user_ans;
        ImageMerge sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat2[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new ImageMerge(data.box, data.iou);
                user_ans = sol.mergeBox();
                System.out.print("Sample"+i+": ");
                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + "\n    iou: "+data.iou + "\n" +
                            "    box: "+Arrays.deepToString(data.box));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans));
                    System.out.println("");
                    draw(user_ans,data.answer);
                    Thread.sleep(5000);
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class IntervalST<Key extends Comparable<Key>, Value>{
    private Node root;

    private class Node {
        private Key lo, hi, max;
        private Value val;
        private Node left, right;
        
        public Node(Key lo, Key hi, Value val) {
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            this.max = hi;
            // initializes the node if required.
        }
    }

    public IntervalST()
    {
        // initializes the tree if required.
    }
    
    public void put(Key lo, Key hi, Value val)
    {
        // insert a new interval here.
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // val   : the value stored in the tree.
        // use lo as bst key.
        root = put(root, lo, hi, val);
    }

    private Node put(Node x, Key lo, Key hi, Value val)
    {
        if(x == null) return new Node(lo, hi, val);
        int compare = lo.compareTo(x.lo);
        if (compare < 0)
            x.left  = put(x.left,  lo, hi, val);
        else if (compare > 0)
            x.right = put(x.right, lo, hi, val);
        else if (compare == 0){
            int compare2 = hi.compareTo(x.hi);
            if (compare2 < 0)
                x.left = put(x.left, lo, hi, val);
            else if (compare2 > 0)
                x.right = put(x.right, lo, hi, val);
            else
                x.val = val;   
        }
        if (x.max.compareTo(hi) < 0)
            x.max = hi;
        return x;
    }
    
    public void delete(Key lo, Key hi)
    {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        root = delete(root, lo, hi);
    }

    private Node delete(Node x, Key lo, Key hi)
    {
        if (x == null) return null;
        int compare = lo.compareTo(x.lo);
        if (compare < 0)
            x.left = delete(x.left, lo, hi);
        else if (compare > 0)
            x.right = delete(x.right, lo, hi);
        else if (compare == 0){
            int compare2 = hi.compareTo(x.hi);
            if (compare2 < 0)
                x.left = delete(x.left, lo, hi);
            else if (compare2 > 0)
                x.right = delete(x.right, lo, hi);
            else{
                if (x.right == null) return x.left;
                if (x.left == null) return x.right;
                Node t = x;
                x = min(t.right);
                x.right = deleteMin(t.right);
                x.left = t.left;
            }
        }
        if (x != null){
            x.max = x.hi;
            if (x.left != null && x.left.max.compareTo(x.max) > 0)
                x.max = x.left.max;
            if (x.right != null && x.right.max.compareTo(x.max) > 0)
                x.max = x.right.max;
        }
        return x;
    }

    private Node min(Node x)
    {
        if (x.left == null) return x;
        return min(x.left);
    }

    private Node deleteMin(Node x)
    {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        return x;
    }
    
    public List<Value> intersects(Key lo, Key hi)
    {
        // create a empty list to store the values of all intervals within the tree which intersect with [lo,hi].
        // return the values of all intervals within the tree which intersect with [lo,hi].
        List<Value> ans = new ArrayList<>();
        root = intersects(root, lo, hi, ans);
        return ans;
    }

    private Node intersects(Node x, Key lo, Key hi, List<Value> ans)
    {
        if (x == null) return null;
        if (!(x.hi.compareTo(lo) < 0 || x.lo.compareTo(hi) > 0)) ans.add(x.val);
        if (x.left != null && x.left.max.compareTo(lo) >= 0)
            x.left = intersects(x.left, lo, hi, ans);
        if (x.right != null && x.lo.compareTo(hi) <= 0)
            x.right = intersects(x.right, lo, hi, ans);
        return x;
    }
}