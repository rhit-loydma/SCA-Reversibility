# SCA-Reversibility
## Running Experiments
Users can set up experiments by modifying the parameters stored in [config.properties](config.properties). The options for the parameters are described below:

### Base Options
| Parameter | Options | Description | 
|---|---|---|
| Type | pattern, predecessors, balance, surjective, injective, GoEs, orphans, twins | The type of experiment to run |
| Mode | bracelet, macrame, totalistic, multicolored | The model to use for the experiment |
| Turning Rule | int, ALL, BIT-BALANCED, or FROM-FILE | The indiviudal or range of turning rules to use for the experiment. FROM-FILE uses rule numbers stored on seperate lines in [rulesToSearch.txt](rulesToSearch.txt)
| Crossing Rule | int, ALL, BIT-BALANCED, or FROM-FILE | The indiviudal or range of crossing rules to use for the experiment. FROM-FILE uses rule numbers stored on seperate lines in [rulesToSearch.txt](rulesToSearch.txt)
| Boundary Condition | periodic, null, reflect, copy, second-order | The boundary condition to use for the experiment |

### Logging and Output
| Logging Mode | Description | 
|---|---|
| none | No data is logged to file |
| matrix | Data is stored in a csv matrix of every turing and crossing rule combiantion |
| list | Data is stored by listing combinations of turning and crossing rules that meet a condition specified by the experiment type |
| heatmap | Data is stored in a csv matric based on the number of 1's in the turning and crossing rules. This data is set up to be used easily with the data visualization tools for generating heatmaps. |

The output level specifes how much information is printed to the console. Each level prints all the information of lower levels additionally. 

| Output Level | Information Displayed | 
|---|---|
| 1 | Pattern generated. System time of at each width for longer computations. |
| 2 | The turning rule. If a rule is balanced, surjective, or injective. |
| 3 | The crossing rule. If a rule is not balanced, surjective, or injective. |
| 4 | The transition table. The number of twins, Garden of Edens, orphans, or predecessors |
| 5 | The list of twins, Garden of Edens, orphans, or predecessors |

### Patterns and Predecessors
| Parameter | Options | Description | 
|---|---|---|
| Starting String | [string of characters representing cells with no seperations] | The starting configuration for generating patterns or listing predecessors. The options for cells in each of the models are described below.
| Height | [int > 0]| The number of iterations to generate in the pattern experiment type |

| Model | Character states | 
|---|---|
| Friendship Bracelet | B F R L |
| Macrame | B F R L b f r l N |
| Totalistic and Expanded Multicolored | B F R L b f r l W A S D w a s d |

### Garden of Edens, Orphans, and Twins
| Parameter | Options | Description| 
|---|---|---|
| Counting Method | aboveOne, aboveStates, or [int > 1] | The counting method to use for twins. Methods are decribed in more detail below |
| Start Width | [int > 1] | The smallest configuration width to search through |
| End Width | [int > Start Width] | The largest configuration width to search through |

| Twins Counting Method | Description | 
|---|---|
| aboveOne | Counts configurations as twins when more than one configuration maps to the same configuration. Useful for when the predecessor has the same number of cells as the origina configuration |
| aboveStates | Counts configurations as twins when more configurations than the number of states map to the same configuration. Useful for when the predecessor size is larger by 1.
| [int > 1] | Counts when exactly n states map to the same configuration. For example, setting this to 2 would count only when exactly 2 configurations map to the same thing. Setting this to 3 would count the number of triplets. |

## Data Visualization
The current data visualization code for this project is stored [here](data-visualization/heatmap.py). There are several parameters that the use can modify to generate heatmaps using the data stored in files from the experiments. This tool will not work with the list logging mode.

| Parameter | Options | Description| 
|---|---|---|
| Model | bracelet, macrame, totalistic, multicolored | The model used in the experiment |
| BC | periodic, null, reflect, copy, second-order | The boundary condition used in the experiment |
| Configuration Type | GoEs, twins, orphans | The configuration type to analyze |
| Widths | [list of ints > 1] | The widths to generate heatmaps for |

## Adding New Models
### Rules and States
To add a new rule, users must implement an instance of the abstract class Rule. To do this, users must implement three abstract methods:

- setRuleCounts(): This sets the number of crossing rules (stored in maxC) and the number of turning rules (stored in maxT) for the model.
- populateStates(): this adds every state in the model (represented with a character) to the ArrayList states.
- generateRuleMap(): this adds to map, a HashMap mapping strings to characters, the mapping from every possible neighborhood (represented as a String) to its output state (a character).

Example implementations are stored in [src/Rules](src/Rules). Every model implemented must use a neighborhood radius of 0.5 to work with the rest of the tools.

### Boundary Conditions

To add a new boundary condition, users must implement an instance of the abstract class Row. To do this, users must implment two abstract methods:

- getSucessor(): this returns the next iteration in the cellular automata model as a Row. This is typically done by iterating through the char array storing the cells and using the Rule associated with the row. 
- checkPredecessors(ArrayList\<String> a): This takes in the string representation predecessor rows generated using the null boundary conditions and validates and modifies them according to the desired boundary condition.

Example implementations are stored in [src/BoundaryConditions](src/BoundaryConditions). Once again, every boundary condition must assume a neighborhood radius of 0.5 to work with the rest of the tools.
