#include <stdio.h>
#include <stdlib.h>

#define NORTE 0
#define ESTE 1
#define SUR 2
#define OESTE 3

#define TOPEX 5
#define TOPEY 5

int giro(int orientacion);
int adelanteX(int orientacion, int coordX);
int adelanteY(int orientazioa, int coordY);
int meta(int coordX, int coordY);
void escribirOrientacion(int orientacion);
main()
{
    int obstaculo;
    int coordX=0;
    int coordY=0;
    int orientacion=NORTE;

    do{
        printf("Hay un obstaculo (0/1)? \n");
	fflush(stdout);
        scanf("%d",&obstaculo);
	printf("Recibido %d\n", obstaculo);
	fflush(stdout);
        if(obstaculo==0)
        {
            printf("Tiene que girar 90� a la derecha\n");

		fflush(stdout);

            orientacion = giro(orientacion);
            escribirOrientacion(orientacion);

        }
        else if (obstaculo==1)   // no hay obstaculo, avanza
        {

            printf("Sigue adelante\n");
	fflush(stdout);
            coordX = adelanteX(orientacion, coordX);
            coordY = adelanteY(orientacion, coordY);
            printf("Coordenadas(X, Y): %d, %d\n",coordX,coordY);
	fflush(stdout);
            escribirOrientacion(orientacion);

        }
    } while (meta(coordX, coordY)==0);//devuelve 1 si ha llegado a meta 0 en caso contrario

    printf("HA LLEGADO!\n");
	fflush(stdout);
}

int giro (int orientacion)
{
   //siempre gira 90� a la derecha
    if(orientacion==OESTE)
    {
        return NORTE;
    }
    else
    {
        orientacion = orientacion + 1 ;
        return orientacion;
    }

}

int adelanteX(int orientacion, int coordX)
{
    switch(orientacion)
    {
    case ESTE:
        coordX = coordX +1;
        break;

    case OESTE:
        coordX = coordX-1;
    }
    return coordX;
}

int adelanteY(int orientacion, int coordY)
{
    switch(orientacion)
    {
    case SUR:
          coordY=coordY-1;
        break;
    case NORTE:coordY=coordY+1;

    }

   return coordY;

    }

int meta(int coordX, int coordY)
{

    if((coordX==TOPEX) && (coordY==TOPEY)) //ha llegado
    {

        return(1);
    }
    else {//no ha llegado
        return(0);
    }
}

void escribirOrientacion(int orientacion)
{
     switch(orientacion)
    {
    case NORTE:
        printf("Orientacion: NORTE\n");
        break;
    case SUR:
          printf("Orientacion: SUR\n");
        break;
    case ESTE:
        printf("Orientacion: ESTE\n");
        break;
    case OESTE:
        printf("Orientacion: OESTE\n");
     }

	fflush(stdout);
}

