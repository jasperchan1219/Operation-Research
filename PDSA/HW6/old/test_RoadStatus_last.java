import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;

class OutputFormat{
    int[] answer;
    String func;
    int[] args;
}

class Event implements Comparable<Event>
{
    private int time;
    private int id;
    private int num_of_cars;
    boolean islight = false;

    public Event(int time, int id, int num_of_cars)
    {
        this.time = time;
        this.id = id;
        this.num_of_cars = num_of_cars;
        this.islight = false;
    }
    public Event(int time)
    {
        this.time = time;
        this.islight = true;
    }
    public int compareTo(Event that)
    {
        if(this.time < that.time)
            return -1;
        else if(this.time > that.time)
            return 1;
        else
            if(this.islight && !that.islight)
                return -1;
            else if(!this.islight && that.islight)
                return 1;
            else
                return 0;
    }
    public int get_time()
    {
        return time;
    }
    public int[] get_cars()
    {
        return new int[]{id, num_of_cars};
    }
    public boolean islight()
    {
        return islight;
    }
}



class RoadStatus
{
    private int current = 0;
    private int lasttime = 0;
    private boolean[] trafficLight={false,false,false};
    private int[] roads={0,0,0};
    private MinPQ<Event> event=new MinPQ<Event>();
    private int dt = 0;
    private boolean firstadd=true;

    public int[] roadStatus(int time)
    {
        // check if there are any event added at current time
        while(!event.isEmpty()){
            if(event.min().get_time() > time) break;
            Event e = event.delMin();
            current = e.get_time();
            dt = current-lasttime;
            if(e.islight())
            {
                System.out.println("current: "+current);
                System.out.println("event_type: light");
                if(dt > 0){
                    for(int i = 0; i<3;++i){
                        if(trafficLight[i] && roads[i] > 0)
                        {
                            roads[i] -= (dt-1);
                            roads[i] = Math.max(0, roads[i]);
                        }
                    }
                }
                dt = 1;
                int most_cars = Math.max(roads[0], Math.max(roads[1], roads[2]));
                boolean first = true;
                for(int i = 0; i<3;++i)
                {
                    if(roads[i] == most_cars && first && roads[i] > 0)
                    {
                        trafficLight[i] = true;
                        first = false;
                        event.insert(new Event(current+roads[i]));
                        System.out.println("next light: "+(current+roads[i]));
                    }
                    else
                        trafficLight[i] = false;
                }
            }
            else
            {
                System.out.println("current: "+current);
                System.out.println("event_type: add");
                System.out.println("id: "+e.get_cars()[0]);
                if(roads[0]==0 && roads[1]==0 && roads[2]==0 && !firstadd)
                    event.insert(new Event(current+1));
                roads[e.get_cars()[0]] += e.get_cars()[1];
                if(firstadd){
                    event.insert(new Event(current+1));
                    firstadd = false;
                }
            }
            if(dt > 0){
                for(int i = 0; i<3;++i){
                    if(trafficLight[i] && roads[i] > 0)
                    {
                        roads[i] -= dt;
                        roads[i] = Math.max(0, roads[i]);
                    }
                }
            }
            lasttime = current;
            System.out.println("roads: "+Arrays.toString(roads));
            System.out.println("trafficLight: "+Arrays.toString(trafficLight));
        }
        if(lasttime<time)
        {
            dt = time - lasttime;
            for(int i = 0; i<3;++i){
                if(trafficLight[i] && roads[i] > 0)
                {
                    roads[i] -= dt;
                }
            }
            lasttime = time;
        }
        return roads;
        //    return the number of cars in each road.
        //    new int[]{num_of_car_0,num_of_car_1,num_of_car_2}
        // return new int[]{0};
    }
    public void addCar(int time, int id, int num_of_cars)
    {
        //add a car to the queue of a specific id.
        //time: the time when the car arrives at the junction.
        //id: the id of the road where the car to be added.
        //num_of_cars: the number of cars to be added.
        event.insert(new Event(time, id, num_of_cars));

    }
    RoadStatus()
    {
    }
    public static void main(String[] args)
    {
        RoadStatus sol = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        
        sol.addCar(0,0,10);
        System.out.println("0: "+Arrays.toString(sol.roadStatus(0)));
        System.out.println("1: "+Arrays.toString(sol.roadStatus(1)));
        sol.addCar(2,0,1);
        System.out.println("2: "+Arrays.toString(sol.roadStatus(2)));
        sol.addCar(3,1,1);
        System.out.println("3: "+Arrays.toString(sol.roadStatus(3)));
        System.out.println("4: "+Arrays.toString(sol.roadStatus(4)));
        sol.addCar(5,1,1);
        for(int i = 5; i<15;++i)
            System.out.println(i+": "+Arrays.toString(sol.roadStatus(i)));
        sol.addCar(15, 1, 10);
        System.out.println("15: "+Arrays.toString(sol.roadStatus(15)));
        System.out.println("16: "+Arrays.toString(sol.roadStatus(16)));
        // check below for full output explaination
    }
}

class test{
    static boolean run_and_check(OutputFormat[] data, RoadStatus roadStat)
    {
        for(OutputFormat cmd : data)
        {
            if(cmd.func.equals("addCar"))
            {
                roadStat.addCar(cmd.args[0], cmd.args[1], cmd.args[2]);
                System.out.println("add: "+Arrays.toString(cmd.args));
            }
            else if(cmd.func.equals("roadStatus"))
            {
                int[] arr = roadStat.roadStatus(cmd.args[0]);
                System.out.println("Input: "+cmd.args[0]);
                System.out.println("Expected: "+Arrays.toString(cmd.answer));
                System.out.println("Output: "+Arrays.toString(arr));
                if(!Arrays.equals(arr,cmd.answer))
                    return false;
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
            for(int i = 0; i<datas.length-1;++i)
            {
                data = datas[i];
                
                System.out.print("Sample"+i+": ");
                if(run_and_check(data, new RoadStatus()))
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
