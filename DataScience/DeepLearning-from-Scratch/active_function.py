import numpy as np
import matplotlib.pylab as plt

def step_function_without_numpy(x):
    if x > 0:
        return 1
    else:
        return 0

def step_function(x):
    return np.array(x > 0, dtype=np.int)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def relu(x):
    return np.maximum(0, x)

def identity_function(x):
    return x

def softmax_with_overflow(a):
    exp_a = np.exp(a)
    sum_exp_a = np.sum(exp_a)
    y = exp_a / sum_exp_a

    return y

def softmax(a):
    c = np.max(a)
    exp_a = np.exp(a - c)
    sum_exp_a = np.sum(exp_a)
    y = exp_a / sum_exp_a

    return y

# def main():
#     print("Hello, Neural Network!")
#     x = np.arange(-5.0, 5.0, 0.1)
#     y = step_function(x)
#     plt.plot(x, y)
#     plt.ylim(-0.1, 1.1) # y 축의 범위 지정
#     plt.show()
#
#     y = sigmoid(x)
#     plt.plot(x, y)
#     plt.ylim(-0.1, 1.1)
#     plt.show()
#
#     y = relu(x)
#     plt.plot(x, y)
#     plt.ylim(-0.1, 6.0)
#     plt.show()
#
# if __name__ == "__main__":
#     main()