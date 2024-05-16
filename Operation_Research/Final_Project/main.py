from utils import *
from visualize import *
import numpy as np
import pandas as pd
from TSP.TSP_no_weight import *
from TSP.TSP_with_weight import *
    
if __name__ == "__main__":
    # 設定檔案路徑
    file_path = '/Users/jasper/Operation Research/Final_Project/data'
    visualize_result_path = '/Users/jasper/Operation Research/Final_Project/visualize_result'
    route = pd.read_csv('/Users/jasper/Operation Research/Final_Project/route_with_weight.csv')

    # 取得所有檔案路徑
    file_list = iter_all_file(file_path)

    # 取臺大站點並計算車輛數
    schoolStationList = update_data(file_list, school_list=None)

    # 取平均車輛數
    schoolStationList = [{**d, 'bemp': d['bemp'] / len(file_list)} for d in schoolStationList]
    # 儲存臺大站點資訊
    import json
    with open('/Users/jasper/Operation Research/Final_Project/schoolStationList.json', 'w', encoding='utf-8') as f:
        json.dump(schoolStationList, f, ensure_ascii=False, indent=4)
    # 計算站點距離
    distance_matrix = compute_distance(schoolStationList)
    # 儲存距離矩陣
    distance_matrix = pd.DataFrame(distance_matrix)
    distance_matrix.to_csv('/Users/jasper/Operation Research/Final_Project/distance_matrix.csv', index=False)
    # TSP optimization
    TSP_no_weight()
    TSP_with_weight()
    # 繪製臺大各站點位置
    plot_location_of_each_station(schoolStationList, visualize_result_path, route)
    pass

'''
YouBike2.0_臺大醫學院附設癌醫中心
YouBike2.0_臺大環研大樓
YouBike2.0_臺大永齡生醫工程館
YouBike2.0_臺大男七舍前
YouBike2.0_臺大男一舍前
YouBike2.0_臺大男六舍前
YouBike2.0_臺大動物醫院前
YouBike2.0_臺大土木研究大樓前
YouBike2.0_臺大萬才館前
YouBike2.0_臺大國青大樓宿舍前
YouBike2.0_臺大社科院圖書館前
YouBike2.0_臺大法人語言訓練中心前
YouBike2.0_臺大綜合體育館停車場前
YouBike2.0_臺大資訊大樓
YouBike2.0_捷運臺大醫院站(4號出口)
YouBike2.0_捷運臺大醫院站(1號出口)
YouBike2.0_臺大醫院兒童醫院
YouBike2.0_臺大水源舍區A棟
YouBike2.0_臺大卓越研究大樓
YouBike2.0_臺大水源修齊會館
YouBike2.0_臺大檔案展示館
YouBike2.0_臺大水源舍區B棟
YouBike2.0_臺大男八舍東側
YouBike2.0_臺大禮賢樓東南側
YouBike2.0_臺大農業陳列館北側
YouBike2.0_臺大管理學院二館北側
YouBike2.0_臺大土木系館
YouBike2.0_臺大大一女舍北側
YouBike2.0_臺大女九舍西南側
YouBike2.0_臺大小福樓東側
YouBike2.0_臺大立體機車停車場
YouBike2.0_臺大工綜館南側
YouBike2.0_臺大天文數學館南側
YouBike2.0_臺大心理系館南側
YouBike2.0_臺大樂學館東側
YouBike2.0_臺大農化新館西側
YouBike2.0_臺大五號館西側
YouBike2.0_臺大舊體育館西側
YouBike2.0_臺大共同教室北側
YouBike2.0_臺大共同教室東南側
YouBike2.0_臺大鹿鳴堂東側
YouBike2.0_臺大公館停車場西北側
YouBike2.0_臺大第二行政大樓南側
YouBike2.0_臺大明達館機車停車場
YouBike2.0_臺大二號館
YouBike2.0_臺大凝態館南側
YouBike2.0_臺大社科院西側
YouBike2.0_臺大社會系館南側
YouBike2.0_臺大思亮館東南側
YouBike2.0_臺大椰林小舖
YouBike2.0_臺大計資中心南側
YouBike2.0_臺大原分所北側
YouBike2.0_臺大生命科學館西北側
YouBike2.0_臺大第一活動中心西南側
YouBike2.0_臺大博理館西側
YouBike2.0_臺大博雅館西側
YouBike2.0_臺大森林館北側
YouBike2.0_臺大一號館
YouBike2.0_臺大小小福西南側
YouBike2.0_臺大教研館北側
YouBike2.0_臺大四號館東北側
YouBike2.0_臺大新生教室南側
YouBike2.0_臺大鄭江樓北側
YouBike2.0_臺大電機二館東南側
YouBike2.0_臺大圖資系館北側
YouBike2.0_臺大總圖書館西南側
YouBike2.0_臺大黑森林西側
YouBike2.0_臺大獸醫館南側
YouBike2.0_臺大新體育館東南側
YouBike2.0_臺大明達館北側(員工宿舍)'''