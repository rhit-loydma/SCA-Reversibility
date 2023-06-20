import matplotlib.pyplot as plt

ROWS = 2
COLS = 3
MODE = "simplified"
BC = "none"

fig = plt.figure(figsize=(7,6))
    
axs = []    

for i in range(1, ROWS * COLS + 1):
    data = open('data/Twins/' + MODE + '/' + str(i) + '_' + BC + '.csv','r').read()
    lines = data.split('\n')

    collections = []
    
    for line in lines:
        if len(line) > 1:
            pts = line.split(',')
            collections.append(list(map(lambda x: float(x)//(0.04*pow(4,i)), pts)))

    axs.append(fig.add_subplot(ROWS, COLS, i))
    axs[i-1].imshow(collections, origin='lower', cmap=plt.colormaps['BuPu'], vmin=0, vmax=100)
    axs[i-1].set_title('Width = ' + str(i), fontsize=10)

#BC = "infinite width"
fig.tight_layout()
#plt.suptitle("Twins\nMode: " + MODE + "   Boundary Condition: " + BC + "\n",fontsize=16,y=1)
plt.suptitle("Twins\nMode: " + MODE + "   Boundary Condition: reflect   Parity: false\n",fontsize=16,y=1)
plt.show()