import os
import numpy as np
from common.util import most_similar, create_co_matrix, ppmi
from dataset import ptb

window_size = 2
wordvec_size = 100

corpus, word_to_id, id_to_word = ptb.load_data('train')
vocab_size = len(word_to_id)
file_path = './ptb_ppmi.npy'
if os.path.exists(file_path) :
    W = np.load('./ptb_ppmi.npy')
else :
    print('동시 발생 수 계산 ...')
    C = create_co_matrix(corpus, vocab_size)
    print('PPMI 계산 ...')
    W = ppmi(C)
    np.save('./ptb_ppmi.npy', W)


print('SVD 계산 ...')
try:
    # truncated SVD (빠른 버전)
    from sklearn.utils.extmath import randomized_svd
    U, S, V = randomized_svd(W, n_components=wordvec_size, n_iter=5, random_state=None)
except ImportError:
    # SVD (느린 버전)
    U, S, V = np.linalg.svd(W)

word_vecs = U[:, :wordvec_size]

querys = ['you', 'year', 'car', 'toyota']
for query in querys:
    most_similar(query, word_to_id, id_to_word, word_vecs, top=5)