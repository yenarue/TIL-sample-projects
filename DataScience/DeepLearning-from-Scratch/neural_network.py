import numpy as np
from common import active_function


def init_network():
    network = {}
    network['W1'] = np.array([[0.1, 0.3, 0.5], [0.2, 0.4, 0.6]])
    network['b1'] = np.array([0.1, 0.2, 0.3])
    network['W2'] = np.array([[0.1, 0.4], [0.2, 0.5], [0.3, 0.6]])
    network['b2'] = np.array([0.1, 0.2])
    network['W3'] = np.array([[0.1, 0.3], [0.2, 0.4]])
    network['b3'] = np.array([0.1, 0.2])

    return network

def forward(network, x):
    W1, W2, W3 = network['W1'], network['W2'], network['W3']
    b1, b2, b3 = network['b1'], network['b2'], network['b3']

    a1 = np.dot(x, W1) + b1
    z1 = active_function.sigmoid(a1)
    a2 = np.dot(z1, W2) + b2
    z2 = active_function.sigmoid(a2)
    a3 = np.dot(z2, W3) + b3
    y = active_function.identity_function(a3)

    return y

def main():
    print("Hello, Neural Network!")

    network = init_network()
    x = np.array([1.0, 0.5])
    y = forward(network, x)
    print(y)

    # X = np.array([1.0, 0.5])
    # W1 = np.array([[0.1, 0.3, 0.5], [0.2, 0.4, 0.6]])
    # B1 = np.array([0.1, 0.2, 0.3])
    #
    # print(W1.shape)
    # print(X.shape)
    # print(B1.shape)
    #
    # ## 0 -> 1
    # A1 = np.dot(X, W1) + B1
    # Z1 = active_function.sigmoid(A1)
    #
    # print(A1)
    # print(Z1)
    #
    # ## 1 -> 2
    # W2 = np.array([[0.1, 0.4], [0.2, 0.5], [0.3, 0.6]])
    # B2 = np.array([0.1, 0.2])
    #
    # print(Z1.shape)
    # print(W2.shape)
    # print(B2.shape)
    #
    # A2 = np.dot(Z1, W2) + B2
    # Z2 = active_function.sigmoid(A2)
    #
    # print(A2)
    # print(Z2)
    #
    # ## 2 -> 출력층
    # W3 = np.array([[0.1, 0.3], [0.2, 0.4]])
    # B3 = np.array([0.1, 0.2])
    #
    # print(Z2.shape)
    # print(W3.shape)
    # print(B3.shape)
    #
    # A3 = np.dot(Z2, W3) + B3
    # Y = active_function.identity_function(A3)
    #
    # print(A3)
    # print(Y)

if __name__ == "__main__":
    main()