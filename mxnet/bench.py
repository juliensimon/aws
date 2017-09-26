import time,gc
import numpy as np
import pandas as pd
import cudamat as cm
import mxnet as mx

size = 10000

np.random.seed(0)
m1 = np.random.randn(size,size)
m2 = np.random.randn(size,size)
t1 = time.time()
m3 = m1.dot(m2)
t2 = time.time()
print "Numpy: %s " % (str)(t2-t1)

df1 = pd.DataFrame(m1)
df2 = pd.DataFrame(m2)
t1 = time.time()
df3 = df1.dot(df2)
t2 = time.time()
print "Pandas: %s " % (str)(t2-t1)

m1 = m2 = df1 = df2 = None
gc.collect()

cm.cublas_init()
cm.CUDAMatrix.init_random()
cu1 = cm.empty((size, size)).fill_with_randn()
cu2 = cm.empty((size, size)).fill_with_randn()
t1= time.time()
cu1.dot(cu2)
t2= time.time()
print "Cudamat: %s " % (str)(t2-t1)
cm.cublas_shutdown()

cu1 = cu2 = None
gc.collect()

nd1 = mx.nd.uniform(low=0, high=1, shape=(size,size), ctx="gpu(0)")
nd2 = mx.nd.uniform(low=0, high=1, shape=(size,size), ctx="gpu(0)")
t1 = time.time()
nd3 = mx.nd.dot(nd1, nd2)
t2 = time.time()
print "MXNet: %s " % (str)(t2-t1)

