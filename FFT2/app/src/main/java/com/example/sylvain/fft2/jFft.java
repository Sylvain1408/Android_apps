package com.example.sylvain.fft2;

import java.lang.Math;

public class jFft
{
	enum eError { AUCUNE, ERR_SIZE, SIZE_MODIFY, ERR_ENTRY; }
	int FftSize= 2048;	// Taille de la FFT *2 
	int n2pow= 10;
	eError Error= eError.AUCUNE;
	private double T[]= null;
	public double Out[]= null;
	double Irt2= 1.0/Math.sqrt(2.0);
	
	jFft(int n)
	{	
		if(n<=16 && n>=1<<30)
		{	Error= eError.ERR_SIZE;
			return;
		}
		n*=2;	// Taille interne *2 car nombres complexes
		n2pow= (int) (Math.log(n)/Math.log(2));
		FftSize= 1<<n2pow;
		if(FftSize!=n)
		{	Error= eError.SIZE_MODIFY;
    		return;
    	}
		T= new double[FftSize];		// Pair => partie réelle, impair => partie imaginaire
		Out= new double[FftSize/2];	// Pair => partie réelle, impair => partie imaginaire
	}
	
	eError GetError() { return Error; }
	
	int Size() { return (FftSize/2); }
	
	private int Signe(int Num) { return (Num<0) ? -1 : ((Num==0)?0:1); }
	
	// ssProg pour le radix 2 (n2pow est pair)
	void Fr2Tr(int BlocSize)
	{
		for(int k=0,j=BlocSize; k<BlocSize; k++,j++)
		{	double t= T[k]+T[j];
    		T[j]= T[k]-T[j];
    		T[k]= t;
		}
	}
	
	// ssProg pour le radix 4
	void Fr4Tr(int BlocSize, int Granularity)
	{
		int L[]= new int[16];
		L[1] = Granularity / 4;
		for(int k=2; k<16; k++)
		{	switch (Signe(L[k-1]-2))
			{	case -1: L[k-1]= 2;
				case 0 : L[k]= 2; break;
				case 1 : L[k]= L[k-1]/2;
			}
		}

		double piovn = Math.PI / Granularity;
		int ji= 3;
		int jl= 2;
		int jr= 2;
		int BlocSize2= BlocSize+BlocSize;
		int BlocSize3= BlocSize+BlocSize2;
		int BlocSize4= BlocSize+BlocSize3;
		
		for(int j1=2;j1<=L[15];j1+=2)
		for(int j2=j1;j2<=L[14];j2+=L[15])
		for(int j3=j2;j3<=L[13];j3+=L[14])
		for(int j4=j3;j4<=L[12];j4+=L[13])
		for(int j5=j4;j5<=L[11];j5+=L[12])
		for(int j6=j5;j6<=L[10];j6+=L[11])
		for(int j7=j6;j7<=L[9];j7+=L[10])
		for(int j8=j7;j8<=L[8];j8+=L[9])
		for(int j9=j8;j9<=L[7];j9+=L[8])
		for(int j10=j9;j10<=L[6];j10+=L[7])
		for(int j11=j10;j11<=L[5];j11+=L[6])
		for(int j12=j11;j12<=L[4];j12+=L[5])
		for(int j13=j12;j13<=L[3];j13+=L[4])
		for(int j14=j13;j14<=L[2];j14+=L[3])
		for(int jt=j14;jt<=L[1];jt+=L[2])
		{	double th2= jt-2;
			if(th2<=0)
			{	for(int k0=0,k1=BlocSize,k2=BlocSize2,k3=BlocSize3; k0<BlocSize; k0++,k1++,k2++,k3++)
				{	double t0 = T[k0] + T[k2];
					double t1 = T[k1] + T[k3];
					T[k2] = T[k0] - T[k2];
					T[k3] = T[k1] - T[k3];
					T[k0] = t0 + t1;
					T[k1] = t0 - t1;
				}
				if(Granularity>4)
				{	for (int k0=BlocSize4,k1=BlocSize4+BlocSize,k2=BlocSize4+BlocSize2,k3=BlocSize4+BlocSize3; k0<BlocSize4+BlocSize; k0++,k1++,k2++,k3++)
					{	double pr = Irt2 * (T[k1]-T[k3]);
						double pi = Irt2 * (T[k1]+T[k3]);
						T[k3] = T[k2] + pi;
						T[k1] = pi - T[k2];
						T[k2] = T[k0] - pr;
						T[k0] = T[k0] + pr;
					}
				}
			}
			else
			{	double arg = th2*piovn;
				double c1 = Math.cos(arg);
				double s1 = Math.sin(arg);
				double c2 = c1*c1 - s1*s1;
				double s2 = c1*s1 + c1*s1;
				double c3 = c1*c2 - s1*s2;
				double s3 = c2*s1 + s2*c1;

				int iS= jr*BlocSize4;
				int kS= ji*BlocSize4;
				int ilast = iS+BlocSize;
				for(int i0=iS,i1=iS+BlocSize,i2=iS+BlocSize2,i3=iS+BlocSize3; i0<ilast; i0++,i1++,i2++,i3++)
				{	int k0 = kS + i0 - iS;
					int k1 = kS + i0 - iS +BlocSize;
					int k2 = kS + i0 - iS +BlocSize2;
					int k3 = kS + i0 - iS +BlocSize3;
					double r1 = T[i1]*c1 - T[k1]*s1;
					double r5 = T[i1]*s1 + T[k1]*c1;
					double t2 = T[i2]*c2 - T[k2]*s2;
					double t6 = T[i2]*s2 + T[k2]*c2;
					double t3 = T[i3]*c3 - T[k3]*s3;
					double t7 = T[i3]*s3 + T[k3]*c3;
					double t0 = T[i0] + t2;
					double t4 = T[k0] + t6;
					t2 = T[i0] - t2;
					t6 = T[k0] - t6;
					double t1 = r1 + t3;
					double t5 = r5 + t7;
					t3 = r1 - t3;
					t7 = r5 - t7;
					T[i0] = t0 + t1;
					T[k3] = t4 + t5;
					T[k2] = t0 - t1;
					T[i1] = t5 - t4;
					T[i2] = t2 - t7;
					T[k1] = t6 + t3;
					T[k0] = t2 + t7;
					T[i3] = t3 - t6;
				}
				jr += 2;
				ji -= 2;
				if(ji<=jl)
				{	ji = 2*jr - 1;
					jl = jr;
				}
			}
		}
	}
	
	void Order1()
	{
		int k= 4;
		int kl= 2;
		for(int j=4; j<=FftSize; j+=2)
		{	if(k>j)
    		{	double t= T[j-1];
    			T[j-1]= T[k-1];
    			T[k-1]= t;
    		}
			k-= 2;
			if(k<=kl)
			{	k= 2*j;
				kl= j;
			}
		}
	}
	
	void Order2()
	{
		int L[]= new int[16];
		L[1]=FftSize;
		for(int k=2; k<=n2pow; k++)
			L[k]= L[k-1]/2;
		for(int k=n2pow+1; k<16; k++)
			L[k]=2;
		int ij= 2;
		for(int  j1=  2;  j1<= L[15]; j1+= 2)
		for(int  j2= j1;  j2<= L[14]; j2+= L[15])
		for(int  j3= j2;  j3<= L[13]; j3+= L[14])
		for(int  j4= j3;  j4<= L[12]; j4+= L[13])
		for(int  j5= j4;  j5<= L[11]; j5+= L[12])
		for(int  j6= j5;  j6<= L[10]; j6+= L[11])
		for(int  j7= j6;  j7<= L[9];  j7+= L[10])
		for(int  j8= j7;  j8<= L[8];  j8+= L[9])
		for(int  j9= j8;  j9<= L[7];  j9+= L[8])
		for(int j10= j9;  j10<= L[6]; j10+= L[7])
		for(int j11= j10; j11<= L[5]; j11+= L[6])
		for(int j12= j11; j12<= L[4]; j12+= L[5])
		for(int j13= j12; j13<= L[3]; j13+= L[4])
		for(int j14= j13; j14<= L[2]; j14+= L[3])
		for(int ji=j14; ji<= L[1]; ji+=L[2],ij+=2)
			if(ij<ji)
			{	int ij1= ij-2;
				int ji1= ji-2;
				double t= T[ij1];
				T[ij1]= T[ji1];
				T[ji1]= t;
				ij1++;ji1++;
				t= T[ij1];
				T[ij1]= T[ji1];
				T[ji1]= t;
			}
	}
	
	boolean Fft(double Tab[])
	{
		if(Tab.length<FftSize/2)
		{	Error= eError.ERR_ENTRY;	// pas assez de données
			return false;
		}
		for(int i=0,j=0; i<FftSize; i+=2,j++)
		{	T[i]= Tab[j];		// Partie réelle
			T[i+1]= 0;			// Partie imaginaire
		}
		return FftEnd();
	}
	
	boolean Fft(short Tab[])
	{
		if(Tab.length<FftSize/2)
		{	Error= eError.ERR_ENTRY;	// pas assez de données
			return false;
		}
		for(int i=0,j=0; i<FftSize; i+=2,j++)
		{	T[i]= (double)Tab[j];		// Partie réelle
			T[i+1]= 0;			// Partie imaginaire
		}
		return FftEnd();
	}

    double getMaxValue(){
		int i;
        double  freq = 0;
		int ampVal = 0;
		for(i=0; i< Out.length ; i++) {
			if ((int) Out[i] > ampVal) {
				ampVal = (int) Out[i];
				freq = i;
			}
		}
        freq = freq*23.5-30;
		if(freq > 0)return freq;
		return 0;
	}

	int getMaxIndex(){
		int ampVal = 0;
		int freq = 0;
		for(int i=0; i< Out.length ; i++) {
			if ((int) Out[i] > ampVal) {
				ampVal = (int) Out[i];
				freq = i;
			}
		}
		return freq;
	}
	
	boolean FftEnd()
	{
		int Granularity= 1;
		if(n2pow%2 != 0)
		{	Granularity= 2;
			Fr2Tr(FftSize/Granularity);
		}
		for(int i=0; i<n2pow/2; i++)
		{	Granularity*= 4;
			Fr4Tr(FftSize/Granularity, Granularity);
		}
		Order1();
		Order2();
		for(int i=3; i<FftSize; i+=2)
			T[i]= -T[i];
		// Calcule le module
		for(int i=0,j=0; i<FftSize; i+=2,j++)
			Out[j]= Math.sqrt(T[i]*T[i]+T[i+1]*T[i+1])/(FftSize/4.0);
		return true;
	}
}
