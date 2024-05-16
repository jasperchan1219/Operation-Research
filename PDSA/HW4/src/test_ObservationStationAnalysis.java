import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.GrahamScan;
import edu.princeton.cs.algs4.Point2D;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.*;

class ObservationStationAnalysis {
    // replace your ObservationStationAnalysis here
    ArrayList<Point2D> stations;
    ArrayList<Point2D> convex;
    Point2D p;
    double[][] distances;
    final int originalSize;

    public ObservationStationAnalysis(ArrayList<Point2D> stations) {
        // you can do something in Constructor or not
        this.stations = stations;
        findConvexHull(stations);
        this.distances = new double[convex.size()][convex.size()];
        this.originalSize = convex.size();
    }
    public void findConvexHull(ArrayList<Point2D> stations){
        Point2D[] points = new Point2D[stations.size()];
        for(int i = 0; i < stations.size(); i++) {
            points[i] = stations.get(i);
        }
        GrahamScan graham = new GrahamScan(points);
        convex = new ArrayList<Point2D>();
        for(Point2D point: graham.hull()){
            convex.add(point);
        }
        this.p = this.convex.get(0);
    }

    public Point2D[] findFarthestStations() {
        Point2D[] farthest = new Point2D[]{stations.get(0), stations.get(1)};
        double maxDistance = 0;

        for(int i = 0; i < convex.size(); i++) {
            for(int j = i + 1; j < convex.size(); j++) {
                if(i<originalSize && j<originalSize){
                    if(distances[i][j] != 0){
                        if(distances[i][j] > maxDistance){
                            maxDistance = distances[i][j];
                            farthest[0] = convex.get(i);
                            farthest[1] = convex.get(j);
                        }
                        continue;
                    }
                }
                double distance = convex.get(i).distanceTo(convex.get(j));
                if(distance > maxDistance) {
                    maxDistance = distance;
                    farthest[0] = convex.get(i);
                    farthest[1] = convex.get(j);
                }
            }
        }
        if(farthest[0].r() > farthest[1].r() && farthest[0].y() != farthest[1].y()){
            Point2D temp = farthest[0];
            farthest[0] = farthest[1];
            farthest[1] = temp;
        }
        else if (farthest[0].r() == farthest[1].r() && farthest[0].y() > farthest[1].y()){
            Point2D temp = farthest[0];
            farthest[0] = farthest[1];
            farthest[1] = temp;
        }
        return farthest; // it should be sorted (ascendingly) by polar radius; please sort (ascendingly) by y coordinate if there are ties in polar radius.

    }

    public double coverageArea() {
        double area = 0.0;
        // calculate the area surrounded by the existing stations
        for(int i=1; i<convex.size()-1; i++){
            area += Math.abs(Point2D.area2(p, convex.get(i), convex.get(i+1))/2);
        }
        return area;
    }

    public void addNewStation(Point2D newStation) {
        if(!convex.contains(newStation)){
            convex.add(newStation);
            findConvexHull(convex);
        }
    }
    
    public static void main(String[] args) throws Exception {

        ArrayList<Point2D> stationCoordinates = new ArrayList<>();
        stationCoordinates.add(new Point2D(0, 0));
        stationCoordinates.add(new Point2D(2, 0));
        stationCoordinates.add(new Point2D(3, 2));
        stationCoordinates.add(new Point2D(2, 6));
        stationCoordinates.add(new Point2D(0, 4));
        stationCoordinates.add(new Point2D(1, 1));
        stationCoordinates.add(new Point2D(2, 2));

        ObservationStationAnalysis Analysis = new ObservationStationAnalysis(stationCoordinates);
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
        
        System.out.println("Add Station (10, 3): ");
        Analysis.addNewStation(new Point2D(10, 3));
        
        System.out.println("Farthest Station A: "+Analysis.findFarthestStations()[0]);
        System.out.println("Farthest Station B: "+Analysis.findFarthestStations()[1]);
        System.out.println("Coverage Area: "+Analysis.coverageArea());
    }
}


class OutputFormat{
    ArrayList<Point2D> stations;
    ObservationStationAnalysis OSA;
    Point2D[] farthest;
    double area;
    Point2D[] farthestNew;
    double areaNew;
    ArrayList<Point2D> newStations;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}


class test_ObservationStationAnalysis{
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        int num_ac = 0;
        int i = 1;

        try {
            // TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            TestCase[] testCases = gson.fromJson(new FileReader(args[0]), TestCase[].class);
            for (TestCase testCase : testCases) {
                System.out.println("Sample"+i+": ");
                i++;
                for (OutputFormat data : testCase.data) {
                    ObservationStationAnalysis OSA = new ObservationStationAnalysis(data.stations);
                    Point2D[] farthest;
                    double area;
                    Point2D[] farthestNew;
                    double areaNew;

                    farthest = OSA.findFarthestStations();
                    area = OSA.coverageArea();


                    if(data.newStations!=null){
                        for(Point2D newStation: data.newStations){
                            OSA.addNewStation(newStation);
                        }
                        farthestNew = OSA.findFarthestStations();
                        areaNew = OSA.coverageArea();
                    }else{
                        farthestNew = farthest;
                        areaNew = area;
                    }

                    
                    if(farthest[0].equals(data.farthest[0]) && farthest[1].equals(data.farthest[1]) &&  Math.abs(area-data.area) < 0.0001 
                    && farthestNew[0].equals(data.farthestNew[0]) && farthestNew[1].equals(data.farthestNew[1]) && Math.abs(areaNew-data.areaNew) < 0.0001)
                    {
                        System.out.println("AC");
                        num_ac++;
                    }
                    else
                    {
                        System.out.println("WA");
                        System.out.println("Ans-farthest: " + Arrays.toString(data.farthest));
                        System.out.println("Your-farthest:  " + Arrays.toString(farthest));
                        System.out.println("Ans-area:  " + data.area);
                        System.out.println("Your-area:  " + area);

                        System.out.println("Ans-farthestNew: " + Arrays.toString(data.farthestNew));
                        System.out.println("Your-farthestNew:  " + Arrays.toString(farthestNew));
                        System.out.println("Ans-areaNew:  " + data.areaNew);
                        System.out.println("Your-areaNew:  " + areaNew);
                        System.out.println("");
                    }
                }
                System.out.println("Score: "+num_ac+"/ 8");
                }
            
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
}