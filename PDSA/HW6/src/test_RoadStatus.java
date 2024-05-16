import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean isfirst;

    public Event(int time, int id, int num_of_cars, boolean isfirst)
    {
        this.time = time;
        this.id = id;
        this.num_of_cars = num_of_cars;
        this.islight = false;
        this.isfirst = isfirst;
    }
    public Event(int time)
    {
        this.time = time;
        this.islight = true;
    }
    public int compareTo(Event that) {
        if (this.time != that.time) {
            return Integer.compare(this.time, that.time);
        } else {
            if (this.islight != that.islight) {
                return this.islight ? 1 : -1;
            } else {
                return this.isfirst ? -1 : (that.isfirst ? 1 : 0);
            }
        }
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
    public boolean get_isfirst()
    {
        return isfirst;
    }
}

class RoadStatus
{
    private int current = 0;
    private int lasttime = 0;
    private boolean[] trafficLight={false,false,false};
    private int[] roads={0,0,0};
    private MinPQ<Event> event=new MinPQ<Event>();
    private int[] changestandard = {0,0,0};
    private int dt = 0;
    private Map<Integer, Integer> havaadd = new HashMap<>();
    // private boolean lastevent_light=false;
    // private int laststatus = 0;

    public int[] roadStatus(int time)
    {
        while(!event.isEmpty()){
            if(event.min().get_time() > time)
                break;
            Event e = event.delMin();
            current = e.get_time();
            dt = current - lasttime;
            // System.out.println("current: "+current+" lasttime: "+lasttime+" dt: "+dt);
            if(dt > 0)
            {
                for(int i = 0; i < 3; ++i)
                {
                    if(trafficLight[i])
                    {
                        roads[i] -= dt;
                        // System.out.println("roads["+i+"] minus "+dt);
                        roads[i] = Math.max(0, roads[i]);
                    }
                }
            }
            if(!e.islight)
            {
                // if(dt==0 && lastevent_light==true && e.get_order()==0 && current==laststatus){
                //     event.insert(new Event(current));
                // }
                // System.out.println("event type: car");
                if(roads[0]==0 && roads[1]==0 && roads[2]==0 && e.get_cars()[1]>0)
                {
                    // System.out.println("need to reset light")
                    event.insert(new Event(current));
                }
                roads[e.get_cars()[0]] += e.get_cars()[1];
                if(e.get_isfirst()){
                    for(int i = 0; i < 3; ++i)
                    {
                        changestandard[i] = roads[i];
                    }
                }
                // lastevent_light=false;

                // if(firstadd){
                //     event.insert(new Event(current));
                //     firstadd = false;
                // }
                // System.out.println("current: "+current+" id: "+e.get_cars()[0]+" add_cars: "+e.get_cars()[1]);
                // System.out.println("current: "+current+" roads: "+Arrays.toString(roads));
            }
            else
            {
                if(dt>0){
                    changestandard[0] = roads[0];
                    changestandard[1] = roads[1];
                    changestandard[2] = roads[2];
                }
                // System.out.println("event type: light");
                int max = changestandard[0];
                int maxid = 0;
                for(int i = 1; i < 3; ++i)
                {
                    if(changestandard[i] > max)
                    {
                        max = changestandard[i];
                        maxid = i;
                    }
                }
                trafficLight[0] = false;
                trafficLight[1] = false;
                trafficLight[2] = false;
                if(changestandard[maxid]>0){
                    trafficLight[maxid] = true;
                    event.insert(new Event(current + changestandard[maxid]));
                }
                // System.out.println("current: "+current+" next light: "+(current+changestandard[i]));
                // System.out.println("current: "+current+" changestandard: "+Arrays.toString(changestandard));
                // System.out.println("current: "+current+" current light: "+Arrays.toString(trafficLight));
                // lastevent_light=true;
            }
            lasttime = current;
        }
        if(time > lasttime)
            {
                for(int i = 0; i < 3; ++i)
                {
                    if(trafficLight[i])
                    {
                        roads[i] -= (time - lasttime);
                        // System.out.println("roads["+i+"] minus "+(time - lasttime));
                        roads[i] = Math.max(0, roads[i]);
                    }
                }
                if(roads[0]==0 && roads[1]==0 && roads[2]==0)
                {
                    trafficLight[0] = false;
                    trafficLight[1] = false;
                    trafficLight[2] = false;
                }
                lasttime = time;
            }
        // laststatus = time;
        return roads;
        //return
        //    return the number of cars in each road.
        //    new int[]{num_of_car_0,num_of_car_1,num_of_car_2}
        // return new int[]{0};
    }
    public void addCar(int time, int id, int num_of_cars)
    {
        if(havaadd.containsKey(time)){
            havaadd.put(time, 1);
            event.insert(new Event(time, id, num_of_cars, false));}
        else{
            havaadd.put(time, 0);
            event.insert(new Event(time, id, num_of_cars, true));}
        //add a car to the queue of a specific id.
    }
    RoadStatus()
    {
    }
    public static void main(String[] args)
    {
        // Example 1
        System.out.println("Example 1: ");
        RoadStatus sol1 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        sol1.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        sol1.addCar(0, 1, 3);
        System.out.println("0: " + Arrays.toString(sol1.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol1.roadStatus(1)));
        sol1.addCar(2, 0, 4);
        for (int i = 2; i < 12; ++i)
            System.out.println(i + ": " + Arrays.toString(sol1.roadStatus(i)));
        //______________________________________________________________________
        // Example 2
        RoadStatus sol2 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 2: ");
        sol2.addCar(0, 0, 2);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        sol2.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol2.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol2.roadStatus(1)));
        sol2.addCar(2, 1, 2);
        for (int i = 2; i < 7; ++i)
            System.out.println(i + ": " + Arrays.toString(sol2.roadStatus(i)));
        //______________________________________________________________________
        // Example 3
        RoadStatus sol3 = new RoadStatus(); // create a T-junction; all traffic lights are Red at the beginning
        System.out.println("Example 3: ");
        sol3.addCar(0, 0, 1);
        System.out.println("0: " + Arrays.toString(sol3.roadStatus(0)));
        System.out.println("1: " + Arrays.toString(sol3.roadStatus(1)));
        System.out.println("2: " + Arrays.toString(sol3.roadStatus(2)));
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3))); 
        sol3.addCar(3, 1, 1);
        System.out.println("3: " + Arrays.toString(sol3.roadStatus(3)));
        sol3.addCar(4, 0, 2);
        for (int i = 4; i < 10; i++) {
            System.out.println(i + ": " + Arrays.toString(sol3.roadStatus(i)));
        }
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
            }
            else if(cmd.func.equals("roadStatus"))
            {
                int[] arr = roadStat.roadStatus(cmd.args[0]);
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
            for(int i = 0; i<datas.length;++i)
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
