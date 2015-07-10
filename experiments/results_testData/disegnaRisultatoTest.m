%%la X era stata così ottenuta:
%% MU1 = [1 2];
%%SIGMA1 = [2 0; 0 .5];
%%MU2 = [-3 -5];
%%SIGMA2 = [1 0; 0 1];
%%X = [mvnrnd(MU1,SIGMA1,1000);mvnrnd(MU2,SIGMA2,1000)];
%%

X= importdata('C:\Users\Lucia\SkyDrive\UNI\cloudComp\project\mapreduce\experiments\testData\x.txt');
%k2
figure
scatter(X(:,1),X(:,2),10,'.')%prima caricare x.txt
hold on
%risultati ottenuti con hadoop
w0=[ 0.49999983949030646 ]
mu0=[ 0.9538520550625607 2.026091721184474  ]
sigmaSqr0=[ 1.9938668254619378 0.49809827269378637  ]

w1=[ 0.500000160509694 ]
mu1=[ -2.9617244380865895 -4.972668567446729  ]
sigmaSqr1=[ 1.009972548161528 0.9897485483450659  ]

mu = [mu0 ;mu1];%valori ricopiati
s0=[sigmaSqr0(1) 0;0  sigmaSqr0(2)]
s1=[sigmaSqr1(1) 0;0 sigmaSqr1(2)]
sigma = cat(3, s0,s1);
p = [w0,w1];
gmmk2 = gmdistribution(mu,sigma,p);
h = ezcontour(@(x,y)pdf(gmmk2,[x y]),[-8 6],[-8 6]);

figure
ezsurf(@(x,y)pdf(gmmk2,[x y]),[-10 10],[-10 10])