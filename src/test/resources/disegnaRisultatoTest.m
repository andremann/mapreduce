%k2
figure
scatter(X(:,1),X(:,2),10,'.')%prima caricare x.txt
hold on
w0=[ 0.4999999693918793 ]
mu0=[ 0.953851535045274 2.0260912321417406  ]
sigmaSqr0=[ 1.9938675457820922 0.49809913386671134  ]

w1=[ 0.5000000306081216 ]
mu1=[ -2.961724935348392 -4.972669896703879  ]
sigmaSqr1=[ 1.009971661322714 0.9897419344427973  ]

mu = [mu0 ;mu1];%valori ricopiati
s0=[sigmaSqr0(1) 0;0  sigmaSqr0(2)]
s1=[sigmaSqr1(1) 0;0 sigmaSqr1(2)]
sigma = cat(3, s0,s1);
p = [w0,w1];
gmmk2 = gmdistribution(mu,sigma,p);
h = ezcontour(@(x,y)pdf(gmmk2,[x y]),[-8 6],[-8 6]);

figure
ezsurf(@(x,y)pdf(gmmk2,[x y]),[-10 10],[-10 10])