import matplotlib.pyplot as plt

MODEL = "weaving"
BC = "periodic"
CONFIGURATION_TYPE = "twins"
widths = [2, 3, 4]

fig = plt.figure(figsize=(12,6))
    
axs = []         

for i in range(len(widths)):
    w = widths[i]
    data = open('data/' + CONFIGURATION_TYPE + '/' + MODEL + '/' + BC + '/' + str(w) + '_-1.csv','r').read()
    lines = data.split('\n')

    collections = []
    
    for line in lines:
        if len(line) > 1:
            pts = line.split(',')
            collections.append(list(map(lambda x: float(x), pts)))

    axs.append(fig.add_subplot(1, len(widths), i+1))
    axs[i].imshow(collections, origin='lower', cmap=plt.colormaps['BuPu'], norm="linear")
    axs[i].set_title('Width = ' + str(w), fontsize=25)

fig.tight_layout()
plt.show()  