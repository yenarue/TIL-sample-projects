import numpy as np

def preprocess(text):
    text = text.lower()
    text = text.replace('.', ' .')
    words = text.split(' ')

    word_to_id = {}
    id_to_word = {}
    for word in words:
        if word not in word_to_id:
            new_id = len(word_to_id)
            word_to_id[word] = new_id
            id_to_word[new_id] = word

    corpus = np.array([word_to_id[w] for w in words])

    return corpus, word_to_id, id_to_word


def create_co_matrix(corpus, vocab_size, window_size=1):
    corpus_size = len(corpus)
    # 0으로 채워진 2차원의 배열로 초기화한다
    co_matrix = np.zeros((vocab_size, vocab_size), dtype=np.int32)

    # 각 단어에 대한
    for idx, word_id in enumerate(corpus):
        # 주변 단어 카운팅
        for i in range(1, window_size + 1):
            left_idx = idx - i
            right_idx = idx + i

            if left_idx >= 0:  # 바운더리 체크
                left_word_id = corpus[left_idx]
                co_matrix[word_id, left_word_id] += 1

            if right_idx < corpus_size:  # 바운더리 체크
                right_word_id = corpus[right_idx]
                co_matrix[word_id, right_word_id] += 1

    return co_matrix

def cos_similarity(x, y, eps=1e-8):
    normalizedX = x / (np.sqrt(np.sum(x ** 2)) + eps)
    normalizedY = y / (np.sqrt(np.sum(y ** 2)) + eps)
    return np.dot(normalizedX, normalizedY)