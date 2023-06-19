import matplotlib.pyplot as plt

ROWS = 2
COLS = 5
MODE = "simplified"
WRAP = "true" 

fig = plt.figure(figsize=(10,6))
    
axs = []    

for i in range(1, ROWS * COLS + 1):
    data = open('data/GardenOfEden/' + MODE + '/' + str(i) + '_' + WRAP + '.csv','r').read()
    lines = data.split('\n')

    collections = []
    
    for line in lines:
        if len(line) > 1:
            pts = line.split(',')
            collections.append(list(map(lambda x: float(x)//(0.01*pow(4,i)), pts)))

    axs.append(fig.add_subplot(ROWS, COLS, i))
    axs[i-1].imshow(collections, origin='lower', cmap=plt.colormaps['viridis'])
    axs[i-1].set_title('Width = ' + str(i), fontsize=10)

fig.tight_layout()
plt.suptitle("Percentage of Garden of Edens\nMode: " + MODE + "   Wrap Around: " + WRAP + "\n",fontsize=16,y=1)
plt.show()