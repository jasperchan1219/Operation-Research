import requests
import json
from tqdm import tqdm
import time
import os

# 確認資料夾存在，否則建立資料夾
資料夾路徑 = './data/'
if not os.path.exists(資料夾路徑):
    os.makedirs(資料夾路徑)

# 定義取得資料並儲存成JSON檔的函式
def 取得並儲存資料():
    url = "https://tcgbusfs.blob.core.windows.net/dotapp/youbike/v2/youbike_immediate.json"
    response = requests.get(url)
    
    if response.status_code == 200:
        資料 = response.json()
        # 儲存為JSON檔案
        檔名 = f"youbike_data_{time.strftime('%Y%m%d_%H%M%S')}.json"
        路徑 = os.path.join(資料夾路徑, 檔名)
        with open(路徑, 'w', encoding='utf-8') as 檔案:
            json.dump(資料, 檔案, ensure_ascii=False, indent=2)
        print(f"已儲存資料至 {路徑}")
    else:
        print("無法取得資料")

while True:
    取得並儲存資料()
    for i in tqdm(range(600), desc='等待'):
        time.sleep(1)  # 等待1秒
    
