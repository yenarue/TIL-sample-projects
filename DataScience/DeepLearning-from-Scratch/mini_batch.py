import sys, os
sys.path.append(os.pardir)
import numpy as np
from dataset.mnist import load_mnist

(x_train, t_train), (x_test, t_test) = load_mnist(normalize=True, one_hot_label=True)

print(x_train.shape) # (훈련데이터 개수=60000, 입력데이터 크기(28x28 이미지)=784)
print(t_train.shape) # (훈련데이터 개수=60000, 정답레이블의 라인 수=10)

# 훈련데이터에서 무작위로 10장의 이미지만 빼내기
train_size = x_train.shape[0] # 60000
batch_size = 10
batch_mask = np.random.choice(train_size, batch_size) # 0~60000 랜덤 숫자를 10개 꺼낸다
x_batch = x_train[batch_mask]
t_batch = t_train[batch_mask]

print(x_batch.shape) # (10, 784)
print(t_batch.shape) # (10, 10)

# print(loss_function.cross_entropy_error(x_batch, t_batch))
