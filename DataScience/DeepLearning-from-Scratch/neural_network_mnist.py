# coding: utf-8
import sys, os
sys.path.append(os.pardir)  # 부모 디렉터리의 파일을 가져올 수 있도록 설정
import numpy as np
import pickle
from dataset.mnist import load_mnist
from PIL import Image
import active_function


def img_show(img):
    pil_img = Image.fromarray(np.uint8(img)) # 넘파이로 저장된 이미지 데이터를 PIL용 데이터 객체로 변환한다
    pil_img.show()


def show_number_5():
    # (훈련 이미지, 훈련 레이블), (시험 이미지, 시험 레이블)
    (x_train, t_train), (x_test, t_test) = load_mnist(flatten=True, normalize=False)

    img = x_train[0]
    label = t_train[0]
    print(label)  # 5

    print(img.shape)  # (784,)
    img = img.reshape(28, 28)  # 형상을 원래 이미지의 크기로 변형
    print(img.shape)  # (28, 28)

    img_show(img)

def get_data():
    (x_train, t_train), (x_test, t_test) = load_mnist(normalize=True, flatten=True, one_hot_label=False)
    return x_test, t_test

def init_network():
    with open("./dataset/sample_weight.pkl", "rb") as f:
        network = pickle.load(f)

    return network

def predict(network, x):
    W1, W2, W3 = network['W1'], network['W2'], network['W3']
    b1, b2, b3 = network['b1'], network['b2'], network['b3']

    a1 = np.dot(x, W1) + b1
    z1 = active_function.sigmoid(a1)
    a2 = np.dot(z1, W2) + b2
    z2 = active_function.sigmoid(a2)
    a3 = np.dot(z2, W3) + b3
    y = active_function.softmax(a3)

    return y

def main():
    x, t = get_data()
    network = init_network()

    batch_size = 100
    accuracy_count = 0

    for i in range(0, len(x), batch_size):
        x_batch = x[i:i+batch_size]
        y_batch = predict(network, x_batch)
        p = np.argmax(y_batch, axis=1) # 확률이 가장 높은 원소의 인덱스를 얻는다
        accuracy_count += np.sum(p == t[i:i+batch_size])

    print("Accuracy:" + str(float(accuracy_count) / len(x)))

if __name__ == "__main__":
    main()