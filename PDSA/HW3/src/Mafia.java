import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays; // Used to print the arrays
import java.util.Collection;
import java.util.Stack;

import edu.princeton.cs.algs4.*;

import com.google.gson.*;

class test{
    public static void main(String[] args){
        Mafia sol = new Mafia();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(args[0])){
            JsonArray all = gson.fromJson(reader, JsonArray.class);
            for(JsonElement caseInList : all){
                JsonArray a = caseInList.getAsJsonArray();
                int q_cnt = 0, wa = 0,ac = 0;
                for (JsonElement o : a) {
                    q_cnt++;
                    JsonObject person = o.getAsJsonObject();
                    JsonArray arg_lvl = person.getAsJsonArray("level");
                    JsonArray arg_rng = person.getAsJsonArray("range");
                    JsonArray arg_ans = person.getAsJsonArray("answer");
                    int LVL[] = new int[arg_lvl.size()];
                    int RNG[] = new int[arg_lvl.size()];
                    int Answer[] = new int[arg_ans.size()];
                    int Answer_W[] = new int[arg_ans.size()];
                    for(int i=0;i<arg_ans.size();i++){
                        Answer[i]=(arg_ans.get(i).getAsInt());
                        if(i<arg_lvl.size()){
                            LVL[i]=(arg_lvl.get(i).getAsInt());
                            RNG[i]=(arg_rng.get(i).getAsInt());
                        }
                    }
                    Answer_W = sol.result(LVL,RNG);
                    for(int i=0;i<arg_ans.size();i++){
                        if(Answer_W[i]==Answer[i]){
                            if(i==arg_ans.size()-1){
                                System.out.println(q_cnt+": AC");
                            }
                        }else {
                            wa++;
                            System.out.println(q_cnt+": WA");
                            break;
                        }
                    }

                }
                System.out.println("Score: "+(q_cnt-wa)+"/"+q_cnt);

            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class member{
    int Level;
    int Range;
    int Index;
    member(int _level,int _range, int i){
        Level=_level;
        Range=_range;
        Index=i;
    }
}

class Mafia{

    public int[] result(int[] levels, int[] ranges) {
        // Given the traits of each member and output 
        // the leftmost and rightmost index of member
        // can be attacked by each member.
        int[] result = new int[levels.length*2];
        Stack<member> stack = new Stack<>();
        for(int i=0;i<levels.length;i++){
            member m = new member(levels[i],ranges[i],i);
            int count_l = m.Range;
            while(!stack.isEmpty() && stack.peek().Level<m.Level && count_l>0){
                stack.pop();
                count_l--;
            }
            if(stack.isEmpty()){
                result[i*2]=Math.max(0, i-m.Range);
            }else{
                if(m.Index-m.Range<stack.peek().Index+1){
                    result[i*2]=stack.peek().Index+1;
                }
                else{
                    result[i*2]=m.Index-m.Range;
                }
            }
            stack.push(m);
        }
        stack.clear();
        for(int i=levels.length-1;i>=0;i--){
            member m = new member(levels[i],ranges[i],i);
            int count_r = m.Range;
            while(!stack.isEmpty() && stack.peek().Level<m.Level && count_r>0){
                stack.pop();
                count_r--;
            }
            if(stack.isEmpty()){
                result[i*2+1]=Math.min(levels.length-1, i+m.Range);
            }else{
                if(m.Index+m.Range>stack.peek().Index-1){
                    result[i*2+1]=stack.peek().Index-1;
                }
                else{
                    result[i*2+1]=m.Index+m.Range;
                }
            }
            stack.push(m);
        }
        return result; 
        // complete the code by returning an int[]
        // flatten the results since we only need an 1-dimentional array.
    }

    public static void main(String[] args) {
        Mafia sol = new Mafia();
        System.out.println(Arrays.toString(
            sol.result(new int[] {11, 13, 11, 7, 15},
                         new int[] { 1,  8,  1, 7,  2})));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}

// class Mafia{
    //     int range_l;
    //     int level_l;
    //     int range_r;
    //     int level_r;
    //     int left_limit_l;
    //     int right_limit_l;
    //     int left_limit_r;
    //     int right_limit_r;
    
    //     public int[] result(int[] levels, int[] ranges) {
    //         // Given the traits of each member and output 
    //         // the leftmost and rightmost index of member
    //         // can be attacked by each member.
    //         int[] result = new int[levels.length*2];
    //         int[] left_end = new int[levels.length];
    //         int[] right_end = new int[levels.length];
    //         Arrays.fill(left_end, 0);
    //         Arrays.fill(right_end, levels.length-1);
    //         int middle;
    //         middle = levels.length/2;
    //         for (int i = 0; i < middle; i++) {
    //             result[i*2] = i;
    //             result[i*2+1] = i;
    //             result[(levels.length-1-i)*2] = levels.length-1-i;
    //             result[(levels.length-1-i)*2+1] = levels.length-1-i;
    //             range_l = ranges[i];
    //             level_l = levels[i];
    //             range_r = ranges[levels.length-1-i];
    //             level_r = levels[levels.length-1-i];
    //             left_limit_l = Math.max(left_end[i],i-range_l);
    //             right_limit_l = Math.min(right_end[i],i+range_l);
    //             left_limit_r = Math.max(left_end[levels.length-1-i],levels.length-1-i-range_r);
    //             right_limit_r = Math.min(right_end[levels.length-1-i],levels.length-1-i+range_r);
    
    //             // check the left and right limit of the member can attack
    //             for(int j = i-1; j >= left_limit_l; j--) {
    //                 if(level_l>levels[j]){
    //                     result[i*2] = j;
    //                 }
    //                 else{
    //                     break;
    //                 }
    //             }
    //             for(int k = i+1; k <= right_limit_l; k++) {
    //                 if(level_l>levels[k]){
    //                     result[i*2+1] = k;
    //                     left_end[k] = i+1;
    //                 }
    //                 else{
    //                     if(level_l==levels[k]){
    //                         left_end[k] = i+1;
    //                     }
    //                     break;
    //                 }
    //             }
    //             for(int j = levels.length-1-i-1; j >= left_limit_r; j--) {
    //                 if(level_r>levels[j]){
    //                     result[(levels.length-1-i)*2] = j;
    //                     right_end[j] = levels.length-1-i-1;
    //                 }
    //                 else{
    //                     if(level_r==levels[j]){
    //                         right_end[j] = levels.length-1-i-1;
    //                     }
    //                     break;
    //                 }
    //             }
    //             for(int k = levels.length-1-i+1; k <= right_limit_r; k++) {
    //                 if(level_r>levels[k]){
    //                     result[(levels.length-1-i)*2+1] = k;
    //                 }
    //                 else{
    //                     break;
    //                 }
    //             }
    //         }
    //         if(levels.length%2!=0){
    //             result[middle*2] = middle;
    //             result[middle*2+1] = middle;
    //             int left_limit;
    //             int right_limit;
    //             left_limit = Math.max(left_end[middle],middle-ranges[middle]);
    //             right_limit = Math.min(right_end[middle],middle+ranges[middle]);
    //             for(int j = middle-1; j >= left_limit; j--) {
    //                 if(levels[middle]>levels[j]){
    //                     result[middle*2] = j;
    //                 }
    //                 else{
    //                     break;
    //                 }
    //             }
    //             for(int k = middle+1; k <= right_limit; k++) {
    //                 if(levels[middle]>levels[k]){
    //                     result[middle*2+1] = k;
    //                 }
    //                 else{
    //                     break;
    //                 }
    //             }
    //         }
    //         return result; 
    //         // complete the code by returning an int[]
    //         // flatten the results since we only need an 1-dimentional array.
    //     }
    
    //     public static void main(String[] args) {
    //         Mafia sol = new Mafia();
    //         System.out.println(Arrays.toString(
    //             sol.result(new int[] {11, 13, 11, 7, 15},
    //                          new int[] { 1,  8,  1, 7,  2})));
    //         // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
    //         //      => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    //     }
    // }