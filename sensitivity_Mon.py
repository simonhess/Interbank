import os
import pandas as pd
import datetime
import shutil

parent_dir = os.getcwd()
# if there is a data folder, delete it and its content
try: 
    shutil.rmtree('data')
except:
    pass
	
# Te test the sensitivity to a monetary policy shock: 
variable = 'reservesInterestRateStrategy.shockAdd'
low = 0
steps = 0.001
high = 0.005

experiments = {}
for i in range(int((high - low) / steps)):
    experiments['experiment{}'.format(i+1)] = low + steps*(i)

# make a data folder and go into it
dataFolder = 'data'
os.makedirs(dataFolder)
os.chdir(dataFolder)
# add property file
# add a folder for every experiment 
for key in experiments:
	foldername = key
	os.makedirs(foldername)
	file = open('{}/experiment.properties'.format(foldername),'w') 
	file.write('#{}\n'.format(str(datetime.datetime.now()))) 
	file.write('fileNamePrefix.value={}/{}/\n'.format(dataFolder, foldername))
	file.write('reservesInterestRateStrategy.shockAdd={}\n'.format(experiments[key])) 
	file.close()