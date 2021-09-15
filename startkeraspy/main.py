import tensorflow as tf
import tensorflow.keras as kr
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

print(tf.__version__)

mnist = kr.datasets.fashion_mnist

type(mnist)
(x_train, y_train), (x_test, y_test) = mnist.load_data()
x_train.shape, y_train.shape
np.max(x_train)
np.mean(x_train)
len(y_train)
