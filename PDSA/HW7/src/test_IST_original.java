import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Interval1D;

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
        if(root == null)
        {
            root = new Node(lo, hi, val);
            return;
        }
        else{
            Node x = root;
            while(x != null)
            {
                if(x.lo.compareTo(lo) > 0)
                {
                    if(x.left == null)
                    {
                        if(x.max.compareTo(hi) < 0)
                        {
                            x.max = hi;
                        }
                        x.left = new Node(lo, hi, val);
                        return;
                    }
                    else
                    {
                        if(x.max.compareTo(hi) < 0)
                        {
                            x.max = hi;
                        }
                        x = x.left;
                    }
                }
                else if(x.lo.compareTo(lo) < 0)
                {
                    if(x.right == null)
                    {
                        if(x.max.compareTo(hi) < 0)
                        {
                            x.max = hi;
                        }
                        x.right = new Node(lo, hi, val);
                        return;
                    }
                    else
                    {
                        if(x.max.compareTo(hi) < 0)
                        {
                            x.max = hi;
                        }
                        x = x.right;
                    }
                }
                else if(x.lo.compareTo(lo) == 0)
                {
                    if(x.hi.compareTo(hi) > 0)
                    {
                        if(x.left == null)
                        {
                            x.left = new Node(lo, hi, val);
                            return;
                        }
                        else
                        {
                            x = x.left;
                        }
                    }
                    else if(x.hi.compareTo(hi) < 0)
                    {
                        if(x.right == null)
                        {
                            x.max = hi;
                            x.right = new Node(lo, hi, val);
                            return;
                        }
                        else
                        {
                            x.max = hi;
                            x = x.right;
                        }
                    }
                    else if(x.hi.compareTo(hi) == 0)
                    {
                        x.val = val;
                        return;
                    }
                }

            }
        }
    }
    
    public void delete(Key lo, Key hi)
    {
        // remove an interval of [lo,hi]
        // do nothing if interval not found.
        Node x = root;
        Node parent = null;
        while(x != null)
        {
            if(x.lo.compareTo(lo) > 0)
            {
                parent = x;
                x = x.left;
            }
            else if(x.lo.compareTo(lo) < 0)
            {
                parent = x;
                x = x.right;
            }
            else if(x.lo.compareTo(lo) == 0)
            {
                if(x.hi.compareTo(hi) > 0)
                {
                    parent = x;
                    x = x.left;
                }
                else if(x.hi.compareTo(hi) < 0)
                {
                    parent = x;
                    x = x.right;
                }
                else if(x.hi.compareTo(hi) == 0)
                {
                    if(x.left == null && x.right == null && parent == null)
                    {
                        root = null;
                        return;
                    }
                    else if(x.left == null && x.right == null && parent != null)
                    {
                        if(parent.left == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.left = null;
                        }
                        else if(parent.right == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.right = null;
                        }
                        return;
                    }
                    else if(x.left == null && parent == null)
                    {
                        root.hi = x.right.hi;
                        root.lo = x.right.lo;
                        root.val = x.right.val;
                        root.max = x.right.max;
                        return;
                    }
                    else if(x.right == null && parent == null)
                    {
                        root.hi = x.left.hi;
                        root.lo = x.left.lo;
                        root.val = x.left.val;
                        root.max = x.left.max;
                        return;
                    }
                    else if(x.left == null && parent != null)
                    {
                        if(parent.left == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.left = x.right;
                        }
                        else if(parent.right == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.right = x.right;
                        }
                        return;
                    }
                    else if(x.right == null && parent != null)
                    {
                        if(parent.left == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.left = x.left;
                        }
                        else if(parent.right == x)
                        {
                            if(parent.max == x.hi){
                                parent.max = parent.hi;
                            }
                            parent.right = x.left;
                        }
                        return;
                    }
                    else
                    {
                        Node temp = x.right;
                        Node temp_parent = x;
                        while(temp.left != null)
                        {
                            temp_parent = temp;
                            temp = temp.left;
                        }
                        if(x.max == x.hi){
                            Key max1 = x.left.max;
                            Key max2 = x.right.max;
                            Key max3 = temp.max;
                            Key max = max1;
                            if(max.compareTo(max2) < 0){
                                max = max2;
                            }
                            if(max.compareTo(max3) < 0){
                                max = max3;
                            }
                            x.max = max;
                        }
                        x.lo = temp.lo;
                        x.hi = temp.hi;
                        x.val = temp.val;
                        if(temp.right != null)
                        {
                            temp.lo = temp.right.lo;
                            temp.hi = temp.right.hi;
                            temp.val = temp.right.val;
                            temp.max = temp.right.max;
                            temp.left = temp.right.left;
                            temp.right = temp.right.right;
                        }
                        else
                        {
                            temp_parent.left = null;
                            temp_parent.max = temp_parent.hi;
                        }
                        return;
                    }
                }
            }
        }

    }
    
    public List<Value> intersects(Key lo, Key hi)
    {
        // return the values of all intervals within the tree which intersect with [lo,hi].
        // lo    : the starting point of the interval. lo included
        // hi    : the ending point of the interval. hi included
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
            found2 = searchAll(x.left, lo, hi, ans);}
        if (found2 || x.left == null || x.left.max.compareTo(lo) < 0){
            // System.out.println("go right");
            found3 = searchAll(x.right, lo, hi, ans);}
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
