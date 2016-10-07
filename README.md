# GNC: Gene Network Coherence with direct and indirect relationships

GNC is a Cytoscape app designed to analyze gene networks coherence using direct and indirect relationships.

## Using GNC

First you need to install the app throug the app manager. If everything goes well, you will get a new item in the `Apps` menu called `GNC`.

GNC consider your current selected network as the network to analyzed so you need to have a network or a network view open in Cytoscape.

Open GNC from the `Apps` menu and you'll be presented the configuration dialog. Select one of the preloaded databases or load  your own custom biological databases which must be a “.txt” file formatted as:

```
Node1,Node2
Node1,Node3
...
Node1,Node N
...
Node N, Node M
```

Sit back and be patient. Although this program is optimized, usually it takes a long time, specially if you use files bigger than 1 MB.

Make sure to give enough memory to the Java Virtual Machine (at least 2048MB).

## Reference
Gómez-Vela, Francisco, José Antonio Lagares, and Norberto Díaz-Díaz. "Gene network coherence based on prior knowledge using direct and indirect relationships." Computational biology and chemistry 56 (2015): 142-151.
  

## COPYRIGHT
* <a href="mailto:jjdiamon@alumno.upo.es">Juan José Díaz Montaña</a> (<a href="mailto:jjdiamon@alumno.upo.es">jjdiamon@alumno.upo.es</a>)
* <a href="http://www.upo.es/eps/fgomez/">Francisco Gómez Vela</a> (<a href="mailto:fgomez@upo.es">fgomez@upo.es</a>) 

Copyright © 2016 Universidad Pablo de Olavide, Spain.
