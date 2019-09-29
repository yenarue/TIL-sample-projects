import numpy as np
import net as Network

x = np.random.randn(10, 2)
model = Network.TwoLayerNet(2, 4, 3)
s = model.predict(x)
print(s)