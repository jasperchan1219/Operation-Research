import json
import os
import csv
from geopy.distance import geodesic

# 讀取JSON檔案
def read_file(path):
    with open(path, 'r') as file:
        data = json.load(file)
        return data
    
# 搜索JSON檔案中有“臺大”字樣的站點
def search_data(data, school_list):
    if school_list == None:
        school_list = []
        for i in range(len(data)):
            if '臺大' in data[i]['sna'] and '醫院' not in data[i]['sna'] or '捷運公館' in data[i]['sna']:
                school_list.append(data[i])
    else:
        count = 0
        for i in range(len(data)):
            if '臺大' in data[i]['sna'] and '醫院' not in data[i]['sna'] or '捷運公館' in data[i]['sna']:
                school_list[count]['bemp'] += data[i]['bemp']
                count += 1
    return school_list

def iter_all_file(path):
    file_list = os.listdir(path)
    for i in range(len(file_list)):
        file_list[i] = os.path.join(path, file_list[i])
    return file_list

def update_data(file_list, school_list):
    for i in range(len(file_list)):
        data = read_file(file_list[i])
        school_list = search_data(data, school_list)
    return school_list

def compute_distance(data_list):
    n = len(data_list)
    distances = [[0 for _ in range(n)] for _ in range(n)]  # 初始化為 0 的距離矩陣

    for i in range(n):
        for j in range(n):
            coords_1 = (data_list[i]['lat'], data_list[i]['lng'])
            coords_2 = (data_list[j]['lat'], data_list[j]['lng'])
            distance = geodesic(coords_1, coords_2).meters  # 使用 geopy 計算距離（單位：米）
            distances[i][j] = int(distance)
    return distances