import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.UF;

class OutputFormat2{
    double[][] box;
    double iou;
    double[][] answer;
}

class Rectangle{
    double x1;
    double x2;
    double y1;
    double y2;
    int id;
    public Rectangle(double x1, double x2, double y1, double y2, int id){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.id = id;
    }
    public double getArea(){
        return (x2-x1)*(y2-y1);
    }
    public double getIOU(Rectangle other){
        double x1 = Math.max(this.x1, other.x1);
        double x2 = Math.min(this.x2, other.x2);
        double y1 = Math.max(this.y1, other.y1);
        double y2 = Math.min(this.y2, other.y2);
        double intersection = Math.max(0, x2-x1)*Math.max(0, y2-y1);
        return intersection/(this.getArea()+other.getArea()-intersection);
    }
    public Rectangle merge(Rectangle other){
        double x1 = Math.min(this.x1, other.x1);
        double x2 = Math.max(this.x2, other.x2);
        double y1 = Math.min(this.y1, other.y1);
        double y2 = Math.max(this.y2, other.y2);
        return new Rectangle(x1, x2, y1, y2, this.id);
    }
    public double getWidth(){
        return x2-x1;
    }
    public double getHeight(){
        return y2-y1;
    }
    public double[] getBox(){
        return new double[]{x1, y1, x2-x1, y2-y1};
    }
}

class Event implements Comparable<Event>{
    double x;
    Rectangle rect;
    boolean isStart;
    public Event(double x, Rectangle rect, boolean isStart){
        this.x = x;
        this.rect = rect;
        this.isStart = isStart;
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
    double[][] ans;
    public double[][] mergeBox()
    {
        IntervalST<Double, Rectangle> ist = new IntervalST<>();
        UF uf = new UF(bbs.length);
        while(!pq.isEmpty()){
            Event cur = pq.delMin();
            if(cur.isStart){
                for(Rectangle rect : ist.intersects(cur.rect.y1, cur.rect.y2)){
                    if(cur.rect.getIOU(rect) >= iou_thresh){
                        uf.union(cur.rect.id, rect.id);
                    }
                }
                ist.put(cur.rect.y1, cur.rect.y2, cur.rect);
            }
            else{
                ist.delete(cur.rect.y1, cur.rect.y2);
            }
        }
        Map<Integer, List<Rectangle>> map = new HashMap<>();
        for(int i = 0; i < bbs.length; i++){
            int root = uf.find(i);
            if(!map.containsKey(root)){
                map.put(root, new ArrayList<>());
            }
            map.get(root).add(new Rectangle(bbs[i][0], bbs[i][0]+bbs[i][2], bbs[i][1], bbs[i][1]+bbs[i][3], i));
        }
        ans = new double[map.size()][4];
        int idx = 0;
        for(List<Rectangle> rects : map.values()){
            Rectangle merged = rects.get(0);
            for(int i = 1; i < rects.size(); i++){
                merged = merged.merge(rects.get(i));
            }
            ans[idx++] = merged.getBox();
        }

        
        //sort the bounding boxes by the up_left_x, if the up_left_x is the same, sort by up_left_y
        Arrays.sort(ans, (a, b) -> {
            if(a[0] < b[0]) return -1;
            if(a[0] > b[0]) return 1;
            if(a[1] < b[1]) return -1;
            if(a[1] > b[1]) return 1;
            if(a[0]+a[2] < b[0]+b[2]) return -1;
            if(a[0]+a[2] > b[0]+b[2]) return 1;
            if(a[1]+a[3] < b[1]+b[3]) return -1;
            if(a[1]+a[3] > b[1]+b[3]) return 1;
            return 0;
        });

        return ans;
        
        //return merged bounding boxes just as input in the format of 
        //[up_left_x,up_left_y,width,height]
    }
    public ImageMerge(double[][] bbs, double iou_thresh){
        this.bbs = bbs;
        this.iou_thresh = iou_thresh;
        pq = new MinPQ<>();
        int id = 0;
        for(double[] bb : bbs){
            Rectangle rect = new Rectangle(bb[0], bb[0]+bb[2], bb[1], bb[1]+bb[3], id);
            pq.insert(new Event(bb[0], rect, true));
            pq.insert(new Event(bb[0]+bb[2], rect, false));
            id++;
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

class IntervalST<Key extends Comparable<Key>, Rectangle>{
    private Node root;
    
    private class Node {
        private Key lo, hi, max;
        private Rectangle val;
        private int size;
        private Node left, right;
        
        public Node(Key lo, Key hi, Rectangle val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            this.size = 1;
            this.max = hi;
        }
    }

    public IntervalST()
    {
        // initializes the tree if required.
        LinkedList<Node> ist = new LinkedList<>();
        root = null;
        ist.add(root);
    }
    
    public void put(Key lo, Key hi, Rectangle cur)
    {
        // insert a new interval here.
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // val   : the value stored in the tree.
        // If an interval is identical to an existing node, then the value of that node is updated accordingly.
        root = put(root, lo, hi, cur);
    }
    private Node put(Node x, Key lo, Key hi, Rectangle val) {
        if (x == null) return new Node(lo, hi, val);
        int cmp = lo.compareTo(x.lo);
        if      (cmp < 0) {
            x.left  = put(x.left, lo, hi, val);
        }
        else if (cmp > 0){
            x.right = put(x.right, lo, hi, val);
        }
        else {
            if(x.hi.compareTo(hi) < 0){
                x.right = put(x.right, lo, hi, val);
            }
            else if(x.hi.compareTo(hi) > 0){
                x.left = put(x.left, lo, hi, val);
            }
            else{
                x.lo = lo;
                x.hi = hi;
                x.val = val;
            }
        }
        x.size = 1 + size(x.left) + size(x.right);
        checkmax(x);
        return x;
    }
    private void checkmax(Node x){
        if(x.left != null && x.right != null){
            Key max = x.left.max.compareTo(x.right.max)>0?x.left.max:x.right.max;
            x.max = x.hi.compareTo(max)>0?x.hi:max;
        }
        else if(x.left != null){
            x.max = x.hi.compareTo(x.left.max)>0?x.hi:x.left.max;
        }
        else if(x.right != null){
            x.max = x.hi.compareTo(x.right.max)>0?x.hi:x.right.max;
        }
        else{
            x.max = x.hi;
        }
    }
    public int size(){
        return size(root);
    }
    private int size(Node x){
        if (x == null) return 0;
        return x.size;
    }
    
    public void delete(Key lo, Key hi)
    { root = delete(root, lo, hi);  }

    private Node delete(Node x, Key lo, Key hi) {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        if (x == null) return null;
        int cmp = lo.compareTo(x.lo);
        if (cmp < 0){
            x.left  = delete(x.left,  lo, hi);
        }
        else if (cmp > 0){
            x.right = delete(x.right, lo, hi);
        }
        else {
            if(x.hi.compareTo(hi) < 0){
                x.right = delete(x.right, lo, hi);
            }
            else if(x.hi.compareTo(hi) > 0){
                x.left = delete(x.left, lo, hi);
            }
            else{
                if (x.right == null){
                    return x.left;}
                if (x.left  == null){
                    return x.right;}
                else{
                    Node t = x;
                    x = min(t.right);
                    x.right = deleteMin(t.right);
                    x.left = t.left;
                }
            }
        }
        x.size = size(x.left) + size(x.right) + 1;
        checkmax(x);
        return x;
        
    }
    private Node min(Node x) {
        if (x.left == null) return x;
        else{
            return min(x.left);
        }
    }
    public void deleteMin(){
        root = deleteMin(root);
    }
    private Node deleteMin(Node x)
    {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }
    public List<Rectangle> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // System.out.println("intersects: "+lo+" "+hi);
        LinkedList<Rectangle> ans = new LinkedList<>();
        Node x = root;
        searchAll(x, lo, hi, ans);
        return ans;
    }
    public boolean searchAll(Node x, Key lo, Key hi,LinkedList<Rectangle> ans) {
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        if (x == null) return false;
        if (x.lo.compareTo(hi) <= 0 && x.hi.compareTo(lo) >= 0) {
            // System.out.println("find: "+x.val);
            ans.add(x.val);
            found1 = true;
        }
        if (x.left != null && x.left.max.compareTo(lo) >= 0){
            // System.out.println("go left");
            found2 = searchAll(x.left, lo, hi, ans);
        }
        if (found2 || x.left == null || x.left.max.compareTo(lo) < 0){
            // System.out.println("go right");
            found3 = searchAll(x.right, lo, hi, ans);
        }
        return found1 || found2 || found3;
    }
    public boolean isContain(Key lo, Key hi){
        return isContain(root, lo, hi);
    }
    private boolean isContain(Node x, Key lo, Key hi){
        if (x == null) return false;
        if (x.lo.compareTo(lo) <= 0 && x.hi.compareTo(hi) >= 0) return true;
        if (x.lo.compareTo(lo) > 0) return isContain(x.left, lo, hi);
        if (x.hi.compareTo(hi) < 0) return isContain(x.right, lo, hi);
        return false;
    }
    public static void main(String[]args)
    {
    }
}