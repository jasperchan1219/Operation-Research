import matplotlib.pyplot as plt
import pandas as pd

def plot_location_of_each_station(data, result_path, route):
    
    result_path = result_path + '/route_with_weight.png'
    
    # 繪製臺大各站點位置
    for i in range(len(data)):
        if 'YouBike2.0_臺大明達館北側(員工宿舍)' in data[i]['sna']:
            plt.scatter(data[i]['lng'], data[i]['lat'], c='r', s=5)
        else:
            plt.scatter(data[i]['lng'], data[i]['lat'], c='b', s=5)
    # draw the route
    for i in range(len(route)):
        lng_start = data[route.iloc[i,0]-1]['lng']
        lat_start = data[route.iloc[i,0]-1]['lat']
        lng_end = data[route.iloc[i,1]-1]['lng']
        lat_end = data[route.iloc[i,1]-1]['lat']
        print(lng_start, lat_start, lng_end, lat_end)
        plt.plot([lng_start, lng_end], [lat_start, lat_end], c='g', linewidth=0.5)
        
    plt.title('Route_with_weight')
    plt.xlabel('Latitude')
    plt.ylabel('Longitude')
    plt.savefig(result_path)
    pass