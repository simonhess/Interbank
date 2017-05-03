import os
import pandas as pd
import datetime
import shutil
import pandas as pd
from SALib.analyze import morris
from SALib.sample.morris import sample
from SALib.test_functions import Sobol_G, Ishigami
from SALib.util import read_param_file
from SALib.plotting.morris import horizontal_bar_plot, covariance_plot, sample_histograms

parent_dir = os.getcwd()
# if there is a data folder, delete it and its content
try: 
    shutil.rmtree('data')
except:
    pass

# use the sys package to sample parameter space in accordance with Morris methods
problem = read_param_file('JMAB_Mon_Morris_Groups.txt')	
param_values = sample(problem, N=12, num_levels=4, grid_jump=2, optimal_trajectories=None)	
morrisParams = pd.DataFrame(param_values, columns=problem['names'])

# roundFloatingPointNumbers
def decreaseDecimalsByThree(x):
    return round(x, 4)

morrisParams = morrisParams.apply(decreaseDecimalsByThree)

# transform several columns to integer values 
columnsThatShouldBeInts = ['capitalFirmPrototype.loanLength', 
                           'capitalFirmPrototype.capitalAmortization', 
                           'capitalFirmPrototype.capitalDuration', 
                           'governmentPrototype.fixedLaborDemand',
						   'capitalFirmPrototype.laborProductivity'
                          ]
for col in columnsThatShouldBeInts:
    morrisParams[col] = morrisParams[col].apply(int)

# Create experiments for every combination of parameters
experiments = {}
for i in range(len(morrisParams)):
    experimentListOfParameters = []
    for column in morrisParams.columns.values:
        experimentListOfParameters.append([column,morrisParams[column][i]])
    experiments['experiment{}'.format(i+1)] = experimentListOfParameters

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
	for k in experiments[key]:
		file.write('{}={}\n'.format( k[0], k[1]))
	#file.write('reservesInterestRateStrategy.shockAdd={}\n'.format(experiments[key])) 
	file.close()