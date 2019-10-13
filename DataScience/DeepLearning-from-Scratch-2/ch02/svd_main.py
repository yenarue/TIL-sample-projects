import numpy as np
import matplotlib.pyplot as plt
from common.util import preprocess, create_co_matrix, ppmi

text = 'You say goodbye and I say hello.'
corpus, word_to_id, id_to_word = preprocess(text)
vocab_size = len(word_to_id)
C = create_co_matrix(corpus, vocab_size)
W = ppmi(C)

# numpy의 선형대수 관련 클래스에 있는 SVD
U, S, V = np.linalg.svd(W)

print("동시 발생 행렬")
print(C[0])
print("PPMI")
print(W[0]) # 희소벡터
print("SVD")
print(U[0]) # 밀집벡터
print(U[0, :2]) # 차원 감소를 위해서는 상위 2개의 벡터만 뽑아내도 된다.

# 상위 2개의 벡터만 뽑아서 plot에 그려보자
for word, word_id in word_to_id.items():
    print(word + "\tU[word_id, 0] : " + str(U[word_id, 0]) + "\tU[word_id, 1] : " + str(U[word_id, 1]))
    plt.annotate(word, (U[word_id, 0], U[word_id, 1]))

plt.scatter(U[:, 0], U[:, 1], alpha=0.5)
plt.show()