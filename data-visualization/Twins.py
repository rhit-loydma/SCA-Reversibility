import matplotlib.pyplot as plt

ROWS = 2
COLS = 3
BC = "none"
METHOD = "surjective"

fig = plt.figure(figsize=(9,6))
    
axs = []    

for i in range(2, ROWS * COLS + 1):
    data = open('data/Twins/simplified/' + str(i) + '_' + BC + '_' + METHOD + '.csv','r').read()
    lines = data.split('\n')

    collections = []
    
    for line in lines:
        if len(line) > 1:
            pts = line.split(',')
            collections.append(list(map(lambda x: float(x)//(0.04*pow(4,i)), pts)))
            collections.append(list(map(lambda x: float(x), pts)))

    axs.append(fig.add_subplot(ROWS, COLS, i-1))
    axs[i-2].imshow(collections, origin='lower', cmap=plt.colormaps['BuPu'], vmin = 0, vmax = 100)
    axs[i-2].set_title('Width = ' + str(i), fontsize=10)

fig.tight_layout()
#plt.suptitle("Twins\nMode: " + MODE + "  Boundary Condition: " + BC + "  Counting Method: " + METHOD + "\n",fontsize=14,y=1)
# plt.suptitle("Twins\nMode: " + MODE + "   Boundary Condition: reflect   Parity: false" + "  Counting Method: " + METHOD + "\n",fontsize=14,y=1)
plt.show()