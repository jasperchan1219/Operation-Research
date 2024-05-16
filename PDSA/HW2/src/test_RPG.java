import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import com.google.gson.*;

class OutputFormat{
    int[] defence;
    int[] attack;
    int k;
    int answer;
}

class test_RPG{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        int num_ac = 0;
        int user_ans;
        OutputFormat data;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                user_ans = new RPG(data.defence, data.attack).maxDamage(data.k);
                System.out.print("Sample"+i+": ");
                if(data.answer == user_ans)
                {
                    System.out.println("AC");
                    num_ac++;
                }
                else
                {
                    System.out.println("WA");
                    System.out.println("Data_atk:  " + Arrays.toString(data.attack));
                    System.out.println("Data_dfc:  " + Arrays.toString(data.defence));
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + user_ans);
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
class RPG {
    int[] defence;
    int[] attack;
    int[] damage;

    public RPG(int[] defence, int[] attack){
        // Initialize some variables
        this.defence = defence;
        this.attack = attack;
        this.damage = new int[attack.length];

        
    }    
    public int maxDamage(int n){ 
        // return the highest total damage after n rounds.
        // max_damage[i][0] represents the maximum damage achievable at round i without boosting when the i-1 round
        // max_damage[i][1] represents the maximum damage achievable at round i with boosting when the i-1 round
        int[][] max_damage = new int[n][2];
        // int[] action = new int[n];

        // Base case: First round damage without boosting
        max_damage[0][0] = Math.max(0, attack[0] - defence[0]);
        max_damage[0][1] = 0;

        for (int i = 1; i < n; i++) {
            // Without boosting: Cumulative damage is the sum of previous rounds' damage plus current attack
            max_damage[i][0] = Math.max(max_damage[i-1][0],max_damage[i-1][1]) + Math.max(0, attack[i] - defence[i]);

            // With boosting: Cumulative damage is the sum of damage until two rounds ago plus current attack (doubled)
            max_damage[i][1] = (i - 2 >= 0 ? Math.max(max_damage[i-2][0], max_damage[i-2][1]) : 0) + Math.max(0, 2 * attack[i] - defence[i]);
            
        }

        // // Decide the action for each round
        // // 1: boost, 2: attack
        // action[n-1] = 2;
        // for (int i = n - 1; i > 0; i--) {
        //     if (max_damage[i][1] > max_damage[i][0]) {
        //         action[i-1] = 1;
        //         if (i - 2 >= 0) action[i-2] = 2;
        //         i--;
        //     }
        //     else {
        //         action[i-1] = 2;
        //     }
        // }
        // System.out.println("action: " + Arrays.toString(action));

        // Return the maximum cumulative damage achievable after n rounds
        return Math.max(max_damage[n - 1][0], max_damage[n - 1][1]);
    }
    
    public static void main(String[] args) {
        RPG sol = new RPG(new int []{5,4,1,7,98,2},new int []{200,200,200,200,200,200});
        System.out.println(sol.maxDamage(6));
        //1: boost, 2: attack, 3: boost, 4: attack, 5: boost, 6: attack
        //maxDamage: 1187

    } 
}