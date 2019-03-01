# 수치 미분
def bad_numerical_diff(f, x):
    h = 10e-50  # 반올림 오차 발생 (걍 0으로 나올 수 있음)
    return (f(x+h) - f(x)) / h  # 차분으로 인한 오차 발생

def numerical_diff(f, x):
    h = 1e-4    # 10^-4 : 적당한 h 값
    return (f(x+h) - f(x-h)) / (2 * h)  # 차분 오차를 줄이기 위한 방법

def __function_1(x):
    return 0.01*x**2 + 0.1*x

def tangent_line(f, x):
    d = numerical_diff(f, x)
    print(d)
    y = f(x) - d*x
    return lambda t: d*t + y

import numpy as np
import matplotlib.pylab as plt

x = np.arange(0.0, 20.0, 0.1)
y = __function_1(x)
plt.xlabel("x")
plt.ylabel("f(x)")

tf = tangent_line(__function_1, 5)
differentiated_y = tf(x)

plt.plot(x, y)
plt.plot(x, differentiated_y)
plt.show()

tf = tangent_line(__function_1, 10)
differentiated_y = tf(x)

plt.plot(x, y)
plt.plot(x, differentiated_y)
plt.show()