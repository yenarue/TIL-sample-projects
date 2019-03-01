# coding: utf-8
import sys, os
sys.path.append(os.pardir)  # 부모 디렉터리의 파일을 가져올 수 있도록 설정
import numpy as np
import common.active_function as active_function
import common.loss_function as loss_function
import common.gradient as gradient


class simpleNet:
    def __init__(self):
        self.W = np.random.randn(2,3)   # 정규분포로 초기화

    def predict(self, x):
        return np.dot(x, self.W)

    def loss(self, x, t):
        z = self.predict(x)
        y = active_function.softmax(z)
        loss = loss_function.cross_entropy_error(y, t)

        return loss

x = np.array([0.6, 0.9])    # 입력 데이터
t = np.array([0, 0, 1])     # 정답 레이블

net = simpleNet()

# 가중치 매개변수
print(net.W) # [[-0.60456848 -0.19225631 -0.85626496]
             #  [ 0.28959083  1.23033185  0.0823353 ]]
# 예측값
print(net.predict(x))   # [-0.10210934  0.99194488 -0.43965721]
# 예측 최대값의 인덱스
print(np.argmax(net.predict(x)))    # 1
# 손실값
print(net.loss(x, t))   # 1.885083063775067


f = lambda w: net.loss(x, t)
dW = gradient.numerical_gradient(f, net.W)

print(dW)
# [[ 0.12766289  0.38124696 -0.50890986]
#  [ 0.19149434  0.57187044 -0.76336478]]