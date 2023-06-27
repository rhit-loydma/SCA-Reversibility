import matplotlib.pyplot as plt
from math import comb

ROWS = 1
COLS = 2
BC = "wrap"

fig = plt.figure(figsize=(10,6))
    
axs = []    

for i in range(1, ROWS * COLS + 1):
    data = open('data/GardenOfEden/expanded/' + str(i) + '_' + BC + '.csv','r').read()
    lines = data.split('\n')

    collections = [[0 for i in range(10)] for j in range(10)]
    
    for j in range(0, len(lines)):
        line = lines[j]
        if len(line) > 1:
            turning = str(bin(j))[2:]
            t = turning.count("1")
            a = comb(9, t)
            pts = line.split(',')
            for k in range(0, len(pts)):
                crossing =  str(bin(k))[2:]
                c = crossing.count("1")
                b = comb(9, c)
                collections[t][c] += float(pts[k]) / (a*b)

    axs.append(fig.add_subplot(ROWS, COLS, i))
    axs[i-1].imshow(collections, origin='lower', cmap=plt.colormaps['BuPu'])
    axs[i-1].set_title('Width = ' + str(i), fontsize=10)

fig.tight_layout()
plt.suptitle("Garden of Edens\nMode: Exapnded   Boundary Condition: " + BC + "\n",fontsize=16,y=1)
# plt.suptitle("Garden of Edens\nMode: " + MODE + "   Boundary Condition: reflect   Parity: true\n",fontsize=16,y=1)
plt.show()