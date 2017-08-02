# GNC: Gene Network Coherence with direct and indirect relationships

GNC is a Cytoscape app designed to analyze gene networks coherence using direct and indirect relationships.

## Installing GNC

GNC is a Cytoscape app so the first thing is to install Cytoscape. You can download it [here](http://www.cytoscape.org/download.php).

Then, you can get GNC through Cytoscape's App Manager or from the Cytoscape Store (http://apps.cytoscape.org/apps/gnc).

If everything goes well, you will get a new item in the `Apps` menu called `GNC`.


## Using GNC

### Configuring and launching GNC
GNC consider your current selected network as the network to analyzed so you need to have a network and/or a network view open in Cytoscape.

Open GNC from the `Apps` menu and you'll be presented with the configuration dialog.

![GNC Results](https://raw.githubusercontent.com/juanjoDiaz/gnc/readme-files/GNCConfig.png "GNC config")

Select one of the preloaded databases or load  your own custom biological databases which must be a “.txt” file formatted as:

```
Node1;Node2
Node1;Node3
...
Node1;Node N
...
Node N;Node M
```
It should be noted that, when selecting a custom database you can select from the right side of the dialog the delimiter you want to use for your file.

Sit back and relax.

### Evaluating GNC results

Once GNC analysis is completed, the results are displayed in Cytoscape.

![GNC Results](https://raw.githubusercontent.com/juanjoDiaz/gnc/readme-files/GNCApp.png "GNC Results")

The Results panel (right) shows global information about the network and allows to download the coherence matrix calculated

The Table panel (bottom) contains three tables:

* Network table: includes the same information that is displayed in the Result panel
* Node table: Each node is marked by whether is present or not in the biological database
* Edge table: Each edge includes the biological coherence between the nodes at its ends

The network view (center) is styled according to the information above:

* Nodes are:
	* Blue if present in the biological network
	* Pink if not
* Edges are:
	* Pink if one of the nodes at its ends isn't present in the biological network
	* Green if the direct relation between the nodes at its ends is confirmed by the biological database (GNC=1)
	* Grey scale otherwise (A darker color represents higher biological coherence)

## Other information

### Where to get biological-databases?

GNC allows the user to use any biological database in the form of a network as gold-standard for the analysis. The database to use will depends on the particular experiment.

GNC comes with few databases preloaded that you can use. However, this example database might become obsolete quite rapidly and only cover two organisms (Homo Sapiens and Saccharomyces Cerevisiae)
For more up-to-date versions or to find databases for other organisms, you can also download more from:
* [GeneMANIA](http://pages.genemania.org/data/)
* [BioGRID](https://thebiogrid.org/download.php)
* [YeastNet](http://www.inetbio.org/yeastnet/downloadnetwork.php)
* [Saccharomyces genome database](http://www.yeastgenome.org/download-data)

You might need to process the downloaded files to comply with the format described above.

Please note that this are just some example sources and there are many more.

## Reference
Gómez-Vela, Francisco, José Antonio Lagares, and Norberto Díaz-Díaz. "Gene network coherence based on prior knowledge using direct and indirect relationships." Computational biology and chemistry 56 (2015): 142-151.
  

## COPYRIGHT
* <a href="mailto:jjdiamon@alumno.upo.es">Juan José Díaz Montaña</a> (<a href="mailto:jjdiamon@alumno.upo.es">jjdiamon@alumno.upo.es</a>)
* <a href="http://www.upo.es/eps/fgomez/">Francisco Gómez Vela</a> (<a href="mailto:fgomez@upo.es">fgomez@upo.es</a>) 

Copyright © 2016 Universidad Pablo de Olavide, Spain.
