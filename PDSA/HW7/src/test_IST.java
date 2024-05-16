import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;


import com.google.gson.*;

import edu.princeton.cs.algs4.In;

class IntervalST<Key extends Comparable<Key>, Value>{
    private Node root;
    
    private class Node {
        private Key lo, hi, max;
        private Value val;
        private int size;
        private Node left, right;
        
        public Node(Key lo, Key hi, Value val) {
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
    
    public void put(Key lo, Key hi, Value val)
    {
        // insert a new interval here.
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // val   : the value stored in the tree.
        // If an interval is identical to an existing node, then the value of that node is updated accordingly.
        root = put(root, lo, hi, val);
    }
    private Node put(Node x, Key lo, Key hi, Value val) {
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
    public List<Value> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
        // System.out.println("intersects: "+lo+" "+hi);
        LinkedList<Value> ans = new LinkedList<>();
        Node x = root;
        searchAll(x, lo, hi, ans);
        return ans;
    }
    public boolean searchAll(Node x, Key lo, Key hi,LinkedList<Value> ans) {
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
    public static void main(String[]args)
    {
        // Example
        IntervalST<Integer, String> IST = new IntervalST<>();
        IST.put(2,5,"badminton");
        IST.put(1,5,"PDSA HW7");
        IST.put(3,5,"Lunch");
        IST.put(3,6,"Workout");
        IST.put(3,7,"Do nothing");
        IST.delete(2,5); // delete "badminton"
        System.out.println(IST.intersects(1,2));
        
        IST.put(8,8,"Dinner");
        System.out.println(IST.intersects(6,10));
        
        IST.put(3,7,"Do something"); // If an interval is identical to an existing node, then the value of that node is updated accordingly
        System.out.println(IST.intersects(7,7));
        
        IST.delete(3,7); // delete "Do something"
        System.out.println(IST.intersects(7,7));
    }
}

class OutputFormat{
    List<String> answer;
    String func;
    String[] args;
}

class test{
    static boolean deepEquals(List<String> a, List<String> b)
    {
        return Arrays.deepEquals(a.toArray(), b.toArray());
    }
    static boolean run_and_check(OutputFormat[] data, IntervalST <Integer,String> IST)
    {
        for(OutputFormat cmd : data)
        {
            if(cmd.func.equals("intersects"))
            {
                int lo = Integer.parseInt(cmd.args[0]);
                int hi = Integer.parseInt(cmd.args[1]);
                
                List<String> student_answer = IST.intersects(lo, hi);
                Collections.sort(cmd.answer);
                Collections.sort(student_answer);
                if(!deepEquals(student_answer, cmd.answer))
                {
                    System.out.println("Error: intersects("+lo+","+hi+")");
                    System.out.println("Expected: "+cmd.answer);
                    System.out.println("Your answer: "+student_answer);
                    return false;
                }
            }
            else if(cmd.func.equals("put"))
            {
                IST.put(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]), cmd.args[2]);
            }
            else if(cmd.func.equals("delete"))
            {
                IST.delete(Integer.parseInt(cmd.args[0]), Integer.parseInt(cmd.args[1]));
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[][] datas;
        OutputFormat[] data;
        int num_ac = 0;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[][].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                
                System.out.print("Sample"+i+": ");
                if(run_and_check(data, new IntervalST<>()))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("");
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
