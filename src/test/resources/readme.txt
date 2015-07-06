# Test effettuato a partire da 2000 punti 2-dimensionali distribuiti secondo una mistura di due Gaussiane di parametri
w_1=0.5
mu_1=[1 , 2],
SigmaSqr_1=[2, 0.5], 
w_2=0.5, 
mu_1=[-3, -5], 
SigmaSqr_2=[1, 1]

I parametri stimati dall'algoritmo sono:

w1=[ 0.49999983949030646 ]
mu1=[ 0.9538520550625607 2.026091721184474  ]
sigmaSqr1=[ 1.9938668254619378 0.49809827269378637  ]

w2=[ 0.500000160509694 ]
mu2=[ -2.9617244380865895 -4.972668567446729  ]
sigmaSqr2=[ 1.009972548161528 0.9897485483450659  ]

I  risultati sono stati plottati in matlab usando il file "disegnaRisultatoTest.m"


###Descrizione File input###

dimensions.txt #file contenete le dimensioni dei dati; prima riga= k (numero di gaussiane); seconda riga=d (dimensione vettoriale dei dati )
x.txt #file contenente il campione di osservazioni; ciascuna riga è un vettore di dimensione d
param.txt # file contenente i parametri iniziali della mistura di k gaussiane. 
	  # i dati sono così memorizzati:
	  # riga 1: vettore peso prima gaussiana 
	  # riga 2: vettore medie prima gaussiana 
	  # riga 3: vettore varianze seconda gaussiana 
 	  # riga 4: vettore peso seconda gaussiana 
	  # riga 5: vettore medie seconda gaussiana 
	  # riga 6: vettore varianze seconda gaussiana 
	  #ecc..

