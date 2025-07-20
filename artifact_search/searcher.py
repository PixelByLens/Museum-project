import math
import jieba
from collections import defaultdict, Counter


class InvertedIndexTFIDF:
    def __init__(self, documents):
        """
        初始化函数，构建倒排索引和文档的 TF-IDF 向量
        :param documents: 文本列表，每个元素代表一篇文档
        """
        self.documents = documents
        self.N = len(documents)
        self.index = defaultdict(set)  # 倒排索引：词 -> 包含该词的文档编号集合
        self.df = defaultdict(int)  # 词的文档频率
        self.idf = {}  # 词的逆文档频率
        self.doc_tfidf = {}  # 每个文档的 TF-IDF 向量（词->权重）
        self.doc_norm = {}  # 每个文档向量的 L2 范数

        self._build_index_and_tfidf()

    def _build_index_and_tfidf(self):
        # 遍历所有文档，更新倒排索引和 df
        for doc_id, doc in enumerate(self.documents):
            # 使用 jieba 分词
            words = jieba.lcut(doc)
            unique_words = set(words)
            for word in unique_words:
                self.index[word].add(doc_id)
                self.df[word] += 1

        # 计算每个词的 idf，采用公式: idf = log((N+1)/(df+1)) + 1
        for word, freq in self.df.items():
            self.idf[word] = math.log((self.N + 1) / (freq + 1)) + 1

        # 计算每篇文档的 TF-IDF 向量
        for doc_id, doc in enumerate(self.documents):
            words = jieba.lcut(doc)
            tf_counter = Counter(words)
            doc_len = len(words)
            tfidf_vector = {}
            for word, count in tf_counter.items():
                # 这里 TF 采用词频/文档长度
                tf = count / doc_len
                tfidf = tf * self.idf[word]
                tfidf_vector[word] = tfidf
            self.doc_tfidf[doc_id] = tfidf_vector
            # 计算 L2 范数
            norm = math.sqrt(sum(w ** 2 for w in tfidf_vector.values()))
            self.doc_norm[doc_id] = norm

    def _compute_query_vector(self, query):
        """
        计算查询文本的 TF-IDF 向量
        :param query: 查询文本
        :return: 查询向量（词->权重）和向量的 L2 范数
        """
        words = jieba.lcut(query)
        tf_counter = Counter(words)
        query_len = len(words)
        query_vector = {}
        for word, count in tf_counter.items():
            # 如果词在文档集中不存在，这里赋予一个默认 idf 值（比如 log((N+1)/1)+1）
            idf = self.idf.get(word, math.log((self.N + 1) / 1) + 1)
            tf = count / query_len
            query_vector[word] = tf * idf
        norm = math.sqrt(sum(w ** 2 for w in query_vector.values()))
        return query_vector, norm

    def search(self, query, topN=5):
        """
        根据查询文本搜索最相似的文档
        :param query: 查询文本
        :param topN: 返回最相似的 topN 个文档，默认返回 5 个
        :return: 返回一个列表，每个元素是 (文档编号, 相似度分值) 的元组，按相似度从高到低排序
        """
        query_vector, query_norm = self._compute_query_vector(query)
        if query_norm == 0:
            return []

        # 利用倒排索引，找到包含查询词的候选文档
        candidate_docs = set()
        for word in query_vector:
            candidate_docs.update(self.index.get(word, set()))

        scores = {}
        for doc_id in candidate_docs:
            doc_vector = self.doc_tfidf[doc_id]
            doc_norm = self.doc_norm[doc_id]
            if doc_norm == 0:
                continue
            # 计算向量点积（只遍历查询中出现的词）
            dot_product = sum(query_vector.get(word, 0) * doc_vector.get(word, 0) for word in query_vector)
            cosine_sim = dot_product / (query_norm * doc_norm)
            scores[doc_id] = cosine_sim

        # 返回相似度最高的 topN 个文档
        top_results = sorted(scores.items(), key=lambda x: x[1], reverse=True)[:topN]

        return top_results


# 示例使用
if __name__ == "__main__":
    docs = [
        "今天天气很好，我们去公园散步",
        "公园里有很多花，非常美丽",
        "昨天我去了动物园，看到了很多可爱的动物",
        "今天的新闻报道了公园的活动",
        "动物园里的动物种类繁多，吸引了很多游客"
    ]

    indexer = InvertedIndexTFIDF(docs)
    query = "公园 花"
    results = indexer.search(query, topN=3)

    print("查询：", query)
    for doc_id, score in results:
        print(f"文档 {doc_id} (相似度: {score:.4f}): {docs[doc_id]}")
