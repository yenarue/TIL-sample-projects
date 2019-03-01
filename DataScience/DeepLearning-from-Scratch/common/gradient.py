import numpy as np

# 기울기
def numerical_gradient(f, x):
    h = 1e-4  # 0.0001
    grad = np.zeros_like(x)

    it = np.nditer(x, flags=['multi_index'], op_flags=['readwrite'])
    while not it.finished:
        idx = it.multi_index
        tmp_val = x[idx]
        x[idx] = float(tmp_val) + h
        fxh1 = f(x)  # f(x+h)

        x[idx] = tmp_val - h
        fxh2 = f(x)  # f(x-h)
        grad[idx] = (fxh1 - fxh2) / (2 * h)

        x[idx] = tmp_val  # 값 복원
        it.iternext()

    return grad

# 다차원 배열을 지원하지 않는 수치 경사법
# def numerical_gradient(f, x):
#     h = 1e-4
#     grad = np.zeros_like(x) # x와 형상이 같은 배열을 형성
#
#     for idx in range(x.size):
#         tmp_val = x[idx]
#         x[idx] = tmp_val + h
#         fxh1 = f(x)
#
#         # f(x-h) 계산
#         x[idx] = tmp_val - h
#         fxh2 = f(x)
#
#         grad[idx] = (fxh1 - fxh2) / (2*h)
#         x[idx] = tmp_val    # 값 복원
#
#     return grad

def __function_2(x):
    if x.ndim == 1:
        return np.sum(x**2)
    else:
        return np.sum(x**2, axis=1)

def tangent_line(f, x):
    d = numerical_gradient(f, x)
    print(d)
    y = f(x) - d*x
    return lambda t: d*t + y

if __name__ == '__main__':
    import matplotlib.pylab as plt

    print(numerical_gradient(__function_2, np.array([3.0, 4.0])))
    print(numerical_gradient(__function_2, np.array([3.0, 0.0])))
    print(numerical_gradient(__function_2, np.array([0.0, 2.0])))

    x0 = np.arange(-2, 2.5, 0.25)
    x1 = np.arange(-2, 2.5, 0.25)
    X, Y = np.meshgrid(x0, x1)

    X = X.flatten()
    Y = Y.flatten()

    grad = numerical_gradient(__function_2, np.array([X, Y]))

    plt.figure()
    plt.quiver(X, Y, -grad[0], -grad[1], angles="xy", color="#666666")  # ,headwidth=10,scale=40,color="#444444")
    plt.xlim([-2, 2])
    plt.ylim([-2, 2])
    plt.xlabel('x0')
    plt.ylabel('x1')
    plt.grid()
    plt.legend()
    plt.draw()
    plt.show()