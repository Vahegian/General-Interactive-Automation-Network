#include <Wire.h>
#include <math.h>
const int deltimeon = 1;
const int deltimeoff = 0;
const char rowsp[]= {8,9,13,12,11,10};
const char rowsn[]= {7,6,5,4,3,2};
int SentMessage[4];
int l = 0;
int r = 0;
int showFor = 50;

/*************************************************************
 *                          Setup                            *
 *************************************************************/

void setup() {
  Serial.begin(9600);
  openI2C();
//  Wire.onReceive(receiveCoords);
  setPinModes();  
  litTheMatrix();
  showG(3,3,showFor);
  showI(3,3,showFor);
  showA(3,3,showFor);
  showN(3,3,showFor);
  showRobotInitLoc();
  Wire.flush();

}

void setPinModes(){
  for(int i = 0; i<6; i++){
    pinMode(rowsp[i], OUTPUT);
    pinMode(rowsn[i], OUTPUT);  
  }
}

void showG(int x, int y, int leveON){
  int coords[][2] = {{x+3,y+3},{x+4,y+3},{x+5,y+3},
                      {x+6,y+3},{x+6,y+2},{x+6,y+1},
                      {x+6,y+0},{x+5,y+0},{x+4,y+0},
                      {x+3,y+0},{x+2,y+0},{x+1,y+0},
                      {x+0,y+0},{x+0,y+1},{x+0,y+2},
                      {x+0,y+3},{x+0,y+4},{x+0,y+5},
                      {x+0,y+6},{x+1,y+6},{x+2,y+6},
                      {x+3,y+6},{x+4,y+6},{x+5,y+6},
                      {x+6,y+6},{x+6,y+5}};
  for(int t = 0; t<leveON; t++){
    for(int i =0; i< 26; i++){
      showLoc(coords[i][0], coords[i][1]);
    }
  }
}

void showI(int x, int y, int leveON){
  int coords[][2] = {{x+0,y+0},{x+1,y+0},{x+2,y+0},
                      {x+3,y+0},{x+4,y+0},{x+5,y+0},
                      {x+6,y+0},{x+0,y+1},{x+1,y+1},
                      {x+2,y+1},{x+3,y+1},{x+4,y+1},
                      {x+5,y+1},{x+6,y+1},{x+3,y+2},
                      {x+3,y+3},{x+3,y+4},{x+3,y+5},
                      {x+3,y+6},{x+0,y+6},{x+1,y+6},
                      {x+2,y+6},{x+4,y+6},{x+5,y+6},
                      {x+6,y+6},{x+0,y+5},{x+1,y+5},
                      {x+2,y+5},{x+4,y+5},{x+5,y+5},
                      {x+6,y+5}};
  for(int t = 0; t<leveON; t++){
    for(int i =0; i< 31; i++){
      showLoc(coords[i][0], coords[i][1]);
    }
  }
}

void showA(int x, int y, int leveON){
  int coords[][2] = {{x+3,y+6},{x+2,y+6},{x+4,y+6},
                      {x+1,y+2},{x+2,y+2},{x+3,y+2},
                      {x+4,y+2},{x+5,y+2},
                      {x+0,y+0},{x+6,y+0},{x+2,y+4},
                      {x+4,y+4}};
  for(int t = 0; t<leveON; t++){
    for(int i =0; i< 13; i++){
      showLoc(coords[i][0], coords[i][1]);
    }
  }
}

void showN(int x, int y, int leveON){
  int coords[][2] = {{x+0,y+6},{x+6,y+0},{x+3,y+3},
                      {x+4,y+2},{x+5,y+1},{x+2,y+4},
                      {x+1,y+5},{x+0,y+1},{x+0,y+2},
                      {x+0,y+3},{x+0,y+4},{x+0,y+5},
                      {x+0,y+6},{x+0,y+0},{x+6,y+0},
                      {x+6,y+1},{x+6,y+2},{x+6,y+3},
                      {x+6,y+4},{x+6,y+5},{x+6,y+6},
                      };
  for(int t = 0; t<leveON; t++){
    for(int i =0; i< 21; i++){
      showLoc(coords[i][0], coords[i][1]);
    }
  }
}

/*************************************************************
 *                        Communication                      *
 *************************************************************/

void loop() {
  for(int i=0; i<4 ;i++){
    if(Serial.available()){
      SentMessage[i] = Serial.read();
    }
  }

  delay(10);
  Serial.print(SentMessage[0]);
  Serial.print(SentMessage[1]);
  Serial.print(SentMessage[2]);
  Serial.println(SentMessage[3]);
  Serial.flush();
  Wire.flush();

  showCoords(SentMessage[0], SentMessage[1], SentMessage[2], SentMessage[3]);
  
  
  delayMicroseconds(350);
//  openI2C();
}

/*************************************************************
 *                         Matrix Control                    *
 *************************************************************/

void allLEDsOFF(){
  for(int i = 0; i<6; i++){
    digitalWrite(rowsn[i], LOW);
    digitalWrite(rowsp[i],HIGH); 
  }
}

void showLoc(int x, int y){
  broadcast(x, y); 
  if(SentMessage[0]<6 && SentMessage[1]<6){
    allLEDsOFF();
    digitalWrite(rowsp[x], LOW);
    digitalWrite(rowsn[y], HIGH);
  }else{
    allLEDsOFF();
  }
}

void showFingerLoc(int x, int y){
  if(x>10 && y>10){showFingerLoc(x-1, y-1);} 
  else if(x<10 && y>10){showFingerLoc(x, y-1);} 
  else if(x>10 && y<10){showFingerLoc(x-1, y);}
  else{ 
    showLoc(x,y);
    showLoc(x,y+1);
    showLoc(x+1,y);
    showLoc(x+1,y+1);
  }
}

void showRobotLoc(int x, int y, int o){
  if(o==1){showRobotNorth(x,y);}
  else if(o==2){showRobotSouth(x,y);}
  else if(o==3){showRobotWest(x,y);}
  else if(o==4){showRobotEast(x,y);}
}

void showRobotNorth(int x,int y){
   if(x==0){showRobotNorth(x+1, y);}
   else if(x>10){showRobotNorth(x-1,y);}
   else if(y<3){showRobotNorth(x,y+(3-y));}
   else if(y>11){showRobotNorth(x,y-1);}
   else{
    showLoc(x,y);
    showLoc(x+1,y);
    showLoc(x-1,y);
    showLoc(x,y-3);
    showLoc(x+1,y-3);
    showLoc(x-1,y-3);
    showLoc(x+1,y-1);
    showLoc(x-1,y-1);
    showLoc(x+1,y-2);
    showLoc(x-1,y-2);
   }
}

void showRobotSouth(int x,int y){
  if(x==0){showRobotSouth(x+1, y);}
   else if(x>10){showRobotSouth(x-1,y);}
   else if(y>8){showRobotSouth(x,y-(y-8));}
//   else if(y>10){showRobotSouth(x,y-1);}
   else{
    showLoc(x,y);
    showLoc(x+1,y);
    showLoc(x-1,y);
    showLoc(x,y+3);
    showLoc(x+1,y+3);
    showLoc(x-1,y+3);
    showLoc(x+1,y+1);
    showLoc(x-1,y+1);
    showLoc(x+1,y+2);
    showLoc(x-1,y+2);
   }
}
void showRobotWest(int x,int y){
  if(x>8){showRobotWest(x-(x-8),y);}
//   else if(x>10){showRobotWest(x-1,y);}
   else if(y==0){showRobotWest(x,y+1);}
   else if(y>10){showRobotWest(x,y-1);}
   else{
    showLoc(x,y);
    showLoc(x,y+1);
    showLoc(x,y-1);
    showLoc(x+3,y);
    showLoc(x+3,y+1);
    showLoc(x+3,y-1);
    showLoc(x+1,y+1);
    showLoc(x+1,y-1);
    showLoc(x+2,y+1);
    showLoc(x+2,y-1);
   }
}
void showRobotEast(int x,int y){
  if(x<3){showRobotEast(x+(3-x),y);}
   else if(x>11){showRobotEast(x-1,y);}
   else if(y==0){showRobotEast(x,y+1);}
   else if(y>10){showRobotEast(x,y-1);}
   else{
    showLoc(x,y);
    showLoc(x,y+1);
    showLoc(x,y-1);
    showLoc(x-3,y);
    showLoc(x-3,y+1);
    showLoc(x-3,y-1);
    showLoc(x-1,y+1);
    showLoc(x-1,y-1);
    showLoc(x-2,y+1);
    showLoc(x-2,y-1);
   }
}

void showCoords(int x, int y, int o, int op){
  if (o==0){ showFingerLoc(x,y);}
  if (o!=0){showRobotLoc(x,y,o);}
  else if (op==4){showRobotLoc(x,y,o);}
}

/*************************************************************
 *               Use for checking states of LEDs             *
 *************************************************************/

void showRobotInitLoc(){
  for (int i=0; i<800; i++){
    showRobotLoc(7,2,3);
  }
}

void litTheMatrix(){
  for(int n=0; n<1; n++){
    for (int i=0; i<12; i++){
      for (int j=0; j<12; j++){
        showLoc(i,j);
        delay(8);
      }
    }
  }

  for(int n=0; n<1; n++){
    for (int i=0; i<12; i++){
      for (int j=0; j<12; j++){
        showLoc(j,i);
        delay(8);
      }
    }
  }
}

/*************************************************************
 *                            I2C                            *
 *************************************************************/

void openI2C(){Wire.begin();}

void broadcast(int x, int y){
  Wire.beginTransmission(9);
  Wire.write(x);
  Wire.write(y);
  Wire.endTransmission();
}


