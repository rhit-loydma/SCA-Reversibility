import matplotlib.pyplot as plt

MODEL = "bracelet"
BC = "null"
CONFIGURATION_TYPE = "orphans"
widths = [1, 2, 3, 4]

fig = plt.figure(figsize=(12,3))
    
axs = []         

for i in range(len(widths)):
    w = widths[i]
    data = open('data/' + CONFIGURATION_TYPE + '/' + MODEL + '/' + BC + '/' + str(w) + '.csv','r').read()
    lines = data.split('\n')

    collections = []
    
    for line in lines:
        if len(line) > 1:
            pts = line.split(',')
            collections.append(list(map(lambda x: float(x), pts)))

    axs.append(fig.add_subplot(1, len(widths), i+1))
    axs[i].imshow(collections, origin='lower', cmap=plt.colormaps['BuPu'], norm="linear")
    axs[i].set_title('Width = ' + str(w), fontsize=20)

fig.tight_layout()
plt.show()  