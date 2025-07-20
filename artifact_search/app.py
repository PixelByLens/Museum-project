# coding=utf-8
from flask import Flask, jsonify, request
from functools import wraps
from transformers import BertTokenizer
from text_representation import BertModel
from searcher import InvertedIndexTFIDF
import torch
import torch.nn.functional as F
from flask_cors import CORS  # 新增的导入
import pymysql
from pymysql.cursors import DictCursor

model = BertModel.from_pretrained('./bert-base-chinese')
tok = BertTokenizer.from_pretrained('./bert-base-chinese')

# 原hardcode数据部分替换为：
def load_artifacts_from_db():
    try:
        connection = pymysql.connect(
            host='47.99.186.81',
            user='root',
            password='root',
            database='video-transformer',
            port=3306,
            charset='utf8mb4',
            cursorclass=DictCursor
        )
        
        with connection.cursor() as cursor:
            sql = "SELECT id, name, description FROM t_artifacts"
            cursor.execute(sql)
            results = cursor.fetchall()
            
            # 格式化为原有结构：name----description
            return [f"{row['id']}----{row['description']}" for row in results]
            
    except Exception as e:
        print(f"数据库连接失败: {str(e)}")
        return []  # 返回空列表或保持原有默认数据
    finally:
        if connection:
            connection.close()

artifacts = load_artifacts_from_db()

# artifacts = [
#     '春秋越王勾践剑----春秋越王勾践剑 [14]，春秋晚期越国青铜器，中国一级文物，1965年湖北省荆州市荆州区望山一号墓出土，现收藏于湖北省博物馆 [1]。\n全长55.7厘米，剑柄8.4厘米，剑宽4.6厘米，总重875克。\n剑首外翻卷成圆箍形，内铸有间隔仅0.2毫米的11道同心圆，剑身上布满规则的黑色菱形暗格花纹；正面近柄处铭刻有“越王鸠（勾）浅（践）自作用剑”的鸟篆铭文；剑格正面嵌有蓝色琉璃，背面镶有绿松石 [13][1]。\n被誉为“天下第一剑”，对研究越国历史、青铜铸造工艺和古文字具有重要价值 [2][3]。',
#     '秦始皇陵铜车马----秦始皇陵铜车马 [15]，战国晚期秦国青铜器，中国一级文物，1974年陕西省临潼区秦始皇陵出土，现收藏于陕西历史博物馆 [1]。\n整体长约320厘米，宽约280厘米，高约150厘米，总重约3200千克。\n车体与马匹造型精致生动，局部雕刻有飞禽走兽纹饰，展现秦朝铸造工艺的雄浑气势与精密技术 [4]。\n该文物不仅反映了秦国军事力量的强盛，更为研究秦代工艺和帝国制度提供了珍贵实物资料 [5]。',
#     '唐代三彩骆驼----唐代三彩骆驼 [12]，唐代陶瓷器，中国一级文物，1987年陕西长安遗址出土，现收藏于陕西历史博物馆 [1]。\n主体长约60厘米，肩高约40厘米，底座直径约25厘米。\n采用三彩釉色烧制，色彩层次丰富，线条流畅自然，生动再现了唐代丝绸之路贸易的繁荣景象 [6]，体现了唐朝对外文化交流与工艺美术的高度成就 [7]。',
#     '宋代官窑青瓷瓶----宋代官窑青瓷瓶 [10]，宋代官窑瓷器，中国一级文物，2001年浙江临海发现，现收藏于杭州博物馆 [1]。\n瓷瓶高约28厘米，口径10厘米，胎质细腻，釉色温润如玉，瓶身釉面略带窑变斑驳纹理，造型端庄，装饰简约雅致，展现了宋代官窑瓷器精湛的制作技艺和内敛审美风格 [8]。',
#     '明代宣德炉----明代宣德炉 [9]，明代青铜器，中国一级文物，1992年北京密云地区出土，现收藏于故宫博物院 [1]。\n炉体直径约32厘米，高约22厘米，构造严谨；表面雕刻有细致的云纹和龙纹图案，纹饰繁复而富有节奏感，展现了明代铸造工艺的高超技艺和艺术魅力 [9]。\n此炉在宗教仪式中曾担任重要角色，对研究明代宗教文化与青铜铸造技术具有重要意义 [10]。',
#     '清代玉如意----清代玉如意 [8]，清代玉器，中国一级文物，1980年辽宁沈阳出土，现收藏于沈阳故宫博物院 [1]。\n全长约18厘米，宽约6厘米，选用优质天然玉石，质地温润细腻；器身雕刻有精美的祥云和卷草纹样，寓意吉祥如意，象征长寿和富贵，体现了清代玉雕艺术的高超水平 [11]。',
#     '汉代金缕玉衣----汉代金缕玉衣 [11]，汉代陪葬品，中国一级文物，1978年陕西咸阳出土，现收藏于陕西历史博物馆 [1]。\n整体尺寸约150厘米×80厘米，由金丝与玉片编织而成；金缕排列整齐，玉片嵌于金丝之间，展现了汉代丧葬礼仪的严谨制度和玉器与金工技术的完美结合 [12]。\n该文物为研究汉代社会制度和工艺技术提供了独特的实物证据 [13]。',
#     '新石器时代陶罐----新石器时代陶罐 [7]，史前陶器，中国一级文物，1985年河南郑州出土，现收藏于河南博物院 [1]。\n高约35厘米，口径约20厘米，采用当地黏土手工捏制；表面未经高温釉化，保留原始陶土纹理，造型简约厚重，反映了新石器时代人类对生活必需品的实用与审美追求 [14]。\n其出土为研究史前文化和古人生活提供了重要实物依据 [15]。',
#     '元代青花瓷瓶----元代青花瓷瓶 [6]，元代瓷器，中国一级文物，1999年江苏南京发现，现收藏于南京博物院 [1]。\n瓷瓶高约40厘米，口径约15厘米，瓶身装饰以蓝白相间的青花纹饰；图案清新雅致，层次分明，胎质坚实，釉面光滑细腻，展现了元代瓷器在造型和绘画技艺上的创新成果 [16]，为中外艺术交流史上重要的实物见证 [17]。',
#     '清代康熙青花瓷花瓶----清代康熙青花瓷花瓶 [5]，清代瓷器，中国一级文物，2005年山东曲阜出土，现收藏于曲阜博物馆 [1]。\n花瓶高约50厘米，直径约25厘米，器型端庄大气；瓶身绘有细腻的花鸟图案，青花与白釉交相辉映，色彩对比鲜明，展示了康熙年间青花瓷器的艺术精华，反映了清代宫廷对外文化交流与审美追求 [18]。']

indexer = InvertedIndexTFIDF(artifacts)



text = '新石器时代陶罐 [7]，史前陶器，中国一级文物，1985年河南郑州出土，现收藏于河南博物院'
result = indexer.search(query=text, topN=5)
candidates = [artifacts[x[0]] for x in result]
print(candidates)
# Tokenize the query and candidate texts
inputs_query = tok([list(text)], is_split_into_words=True, return_tensors='pt')
inputs_candi = tok([list(x) for x in candidates], is_split_into_words=True, return_tensors='pt', padding=True, max_length=512)

# Get embeddings from the model
rep_query = model(**inputs_query).pooler_output  # (1, hidden_dim)
rep_candi = model(**inputs_candi).pooler_output  # (5, hidden_dim)

# Compute cosine similarity
cos_sim = F.cosine_similarity(rep_query, rep_candi)  # (5,)

# Sort scores and candidates in descending order
sorted_indices = torch.argsort(cos_sim, descending=True)  # 获取排序索引
sorted_scores = cos_sim[sorted_indices]  # 按排序索引重新排序分数
sorted_candidates = [candidates[i] for i in sorted_indices]  # 按索引排序候选文本

# Print or return the sorted results
print("Sorted Cosine Similarity Scores:", sorted_scores.tolist())
print("Sorted Candidates:", sorted_candidates)
#

app = Flask(__name__)
CORS(app, resources={r"/match": {"origins": "*"}})  # 新增的CORS配置

def validate_params(required_params, optional_params={}):
    """
    Decorator that validates the presence and type of parameters in the request.
    """

    def decorator(f):
        @wraps(f)
        def wrapper(*args, **kwargs):
            params = request.json or {}

            # Check required parameters
            for param, param_type in required_params.items():
                if param not in params:
                    return jsonify({'error': f'Missing required parameter: {param}'}), 400
                if not isinstance(params[param], param_type):
                    return jsonify({'error': f'Invalid type for parameter {param}. Expected {param_type.__name__}.'}), 400

            # Check optional parameters
            if optional_params:
                for param, param_type in optional_params.items():
                    if param in params and not isinstance(params[param], param_type):
                        return jsonify({'error': f'Invalid type for parameter {param}. Expected {param_type.__name__}.'}), 400

            # Check for extra parameters
            extra_params = set(params.keys()) - set(required_params.keys()) - set(optional_params.keys())
            if extra_params:
                return jsonify({'error': f'Unexpected parameters: {", ".join(extra_params)}'}), 400

            # Call the decorated function with the validated parameters
            return f(params, *args, **kwargs)

        return wrapper
    return decorator


@app.route('/match', methods=['POST'])
@validate_params(required_params={'text': str}, optional_params={})
def predict(params):

    try:
        text = params['text']
        result = indexer.search(query=text, topN=5)
        candidates = [artifacts[x[0]] for x in result]
        print(candidates)
        # Tokenize the query and candidate texts
        inputs_query = tok([list(text)], is_split_into_words=True, return_tensors='pt')
        inputs_candi = tok([list(x) for x in candidates], is_split_into_words=True, return_tensors='pt', padding=True,
                           max_length=512)

        # Get embeddings from the model
        rep_query = model(**inputs_query).pooler_output  # (1, hidden_dim)
        rep_candi = model(**inputs_candi).pooler_output  # (5, hidden_dim)

        # Compute cosine similarity
        cos_sim = F.cosine_similarity(rep_query, rep_candi)  # (5,)

        # Sort scores and candidates in descending order
        sorted_indices = torch.argsort(cos_sim, descending=True)  # 获取排序索引
        sorted_scores = cos_sim[sorted_indices]  # 按排序索引重新排序分数
        sorted_candidates = [candidates[i] for i in sorted_indices]  # 按索引排序候选文本

        # Print or return the sorted results
        print("Sorted Cosine Similarity Scores:", sorted_scores.tolist())
        print("Sorted Candidates:", sorted_candidates)

    except Exception as e:
        return jsonify({'message': f'模型预测内部错误{e}', 'code': 40000, 'class': None})

    best_match = candidates[0]
    id, info = best_match.split('----')
    return jsonify({'message': "匹配成功", 'code': 200, 'id': id, 'info':info}), 200

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8686)
