import matplotlib.pyplot as plt
import numpy as np

MODE = "injective"
MODEL = "simplified"

fig = plt.figure(figsize=(6,6))
   

data = open('data/Properties/' + MODEL + "_" + MODE + '.csv','r').read()
lines = data.split('\n')

collections = []

for line in lines:
    if len(line) > 1:
        pts = line.split(',')
        collections.append(list(map(lambda x: float(x), pts)))

ax = fig.add_subplot(1, 1, 1)
ax.imshow(collections, origin='lower', cmap=plt.colormaps['Greys'])
ax.set(xlabel='Crossing Rule', ylabel='Turning Rule')
plt.xticks(np.arange(0, 16, 1.0))
plt.yticks(np.arange(0, 16, 1.0))

fig.tight_layout()
# plt.suptitle("Surjective Mode: " + MODE,fontsize=16,y=1)
plt.show()