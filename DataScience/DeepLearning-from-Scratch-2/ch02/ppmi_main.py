import numpy as np
from common.util import preprocess, create_co_matrix, cos_similarity, ppmi

text = 'You say goodbye and I say hello.'
corpus, word_to_id, id_to_word = preprocess(text)
vocab_size = len(word_to_id)
C = create_co_matrix(corpus, vocab_size)
W = ppmi(C)

np.set_printoptions(precision=3)    # 유효 자릿수 3자리
print('동시 발생 행렬')
print(C)
print('-' * 50)
print('PPMI')
print(W)

# 동시 발생 행렬
# [[0 1 0 0 0 0 0]
#  [1 0 1 0 1 1 0]
#  [0 1 0 1 0 0 0]
#  [0 0 1 0 1 0 0]
#  [0 1 0 1 0 0 0]
#  [0 1 0 0 0 0 1]
#  [0 0 0 0 0 1 0]]
# --------------------------------------------------
# PPMI
# [[0.    1.807 0.    0.    0.    0.    0.   ]
#  [1.807 0.    0.807 0.    0.807 0.807 0.   ]
#  [0.    0.807 0.    1.807 0.    0.    0.   ]
#  [0.    0.    1.807 0.    1.807 0.    0.   ]
#  [0.    0.807 0.    1.807 0.    0.    0.   ]
#  [0.    0.807 0.    0.    0.    0.    2.807]
#  [0.    0.    0.    0.    0.    2.807 0.   ]]
