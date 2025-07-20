import requests
data = {'text': '新石器时代陶罐 [7]，史前陶器，中国一级文物，1985年河南郑州出土，现收藏于河南博物院'}
res = requests.post('http://127.0.0.1:8686/match', json=data)
print(res.json())