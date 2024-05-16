import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gson.*;
import edu.princeton.cs.algs4.MinPQ;

class OutputFormat{
    Integer[][] scores;
    List<int[]> answer;
}

class Exam {
    public static List<int[]> getPassedList(Integer[][] scores)
    {
        int numSubjects = scores.length;
        int numStudents = scores[0].length;
        int num_admit = (int)Math.ceil(numStudents * 0.2);
        Vector<Integer> nominate = new Vector<>();
        for(int i=0; i<scores.length; i++)
        {
            MinPQ<Integer> pq = new MinPQ<>();
            for(int j=0; j<scores[i].length; j++)
            {
                pq.insert(scores[i][j]);
                if(pq.size() > num_admit)
                {
                    pq.delMin();
                }
            }
            if(i==0){
                for(int j=0; j<scores[i].length; j++){
                    if(scores[i][j] >= pq.min()){
                        nominate.add(j);
                        }
                    }
            }
            else{
                for(int j=0; j<nominate.size(); j++){
                    if(scores[i][nominate.get(j)] < pq.min()){
                        nominate.remove(j);
                        j--;
                    }
                }
            }
        }
        List<int[]> ans = new ArrayList<>();
        for(int i=0; i<nominate.size(); i++)
        {
            int id = nominate.get(i);
            int total = 0;
            for(int j=0; j<numSubjects; j++)
            {
                total += scores[j][id];
            }
            ans.add(new int[]{id, total});
        }
        ans.sort((a, b) -> b[1] - a[1]);
        return ans;

        
        //input:
        //    scores: int[subject][id] 
        //    eg. scores[0][0] -> subject: 0, ID: 0
        //        scores[1][5] -> subject: 1, ID: 5

        //return:
        //    return a List of {ID, totalScore} 
        //    sorted in descending order of the total score
    }
    public static void main(String[] args) {
        List<int[]> ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
                {67,82,98,32,65,76,87,12,43,75,25},
                {42,90,80,12,76,58,95,30,67,78,10}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 11 students * 0.2 = 2.2 -> Top 3 students 
            // Output -> [6, 182][2, 178][1, 172]
        
        System.out.println(); // For typesetting
        
        ans = getPassedList(new Integer[][]
            {
                // ID:[0, 1, 2, 3, 4, 5]
                {67,82,64,32,65,76},
                {42,90,80,12,76,58}
            }
        );
        for(int[] student : ans)
            System.out.print(Arrays.toString(student));
            // 6 students * 0.2 = 1.2 -> Top 2 students 
            // Output -> [1, 172]
    } 
}

class test_Exam{
    static boolean deepEquals(List<int[]> answer,List<int[]> answer2)
    {
        if(answer.size() != answer2.size())
            return false;
        for(int i = 0; i< answer.size(); ++i)
        {
            int[] a = answer.get(i);
            int[] b = answer2.get(i);
            if(!Arrays.equals(a, b))
            {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        int num_ac = 0;
        List<int[]> user_ans;
        OutputFormat data;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                user_ans = Exam.getPassedList(data.scores);
                System.out.print("Sample"+i+": ");

                if(deepEquals(user_ans, data.answer))
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data:      " + Arrays.deepToString(data.scores));
                    System.out.println("Test_ans:  " + Arrays.deepToString(data.answer.toArray()));
                    System.out.println("User_ans:  " + Arrays.deepToString(user_ans.toArray()));
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
