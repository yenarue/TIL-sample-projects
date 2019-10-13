import numpy as np

c = np.array([[1, 0, 0, 0, 0, 0, 0]])   # 입력
W = np.random.randn(7, 3)               # 가중치
h = np.matmul(c, W)                     # 중간 노드
print(h)

# 우리가 직접 구현했던 matmul을 이용할수도 있다ㅎㅎ
from layers import MatMul
layer = MatMul(W)
h = layer.forward(c)
print(h)