#include <Wire.h>

const int deltimeon = 10;
const int deltimeoff = 0;
const char rowsp[]= {2,3,4,5,6,7};
const char rowsn[]= {8,9,10,11,12,13};
int SentMessage[2];

void setup() {
//  Serial.begin(9600);
  Wire.begin(9); 
  Wire.onReceive(receiveCoords);
  setPinModes();
//  animateI();
  
}

void loop() {
  if(SentMessage[0]> 5 && SentMessage[1]<6){
    SentMessage[0] = map(SentMessage[0], 6,11,0,5);
    showFingerLoc(SentMessage[0], SentMessage[1]);
  }else{
    allLEDsOFF();
  }
  delayMicroseconds(350);
  setup();
}

void allLEDsOFF(){
  for(int i = 0; i<6; i++){
    digitalWrite(rowsn[i], LOW);
    digitalWrite(rowsp[i], LOW); 
  }
}

void setPinModes(){
  for(int i = 0; i<6; i++){
    pinMode(rowsp[i], OUTPUT);
    pinMode(rowsn[i], OUTPUT);  
  }
}
  
void receiveCoords(int bytes){
  for(int i=0; i<2 ;i++){
        SentMessage[i] = Wire.read();
      }
}


void I(int speedTime){
  int coords[][2] = {{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{2,2},{2,3},{3,2},{3,3},
                     {4,2},{4,3},{1,2},{1,3},{5,0},{5,1},{5,2},{5,3},{5,4},{5,5}};
  for(int i =0; i<20; i++){
    allLEDsOFF();
    digitalWrite(rowsp[coords[i][1]], HIGH);
    digitalWrite(rowsn[coords[i][0]], HIGH);
    delay(speedTime);
  }
}

void animateI(){
  for (int a=0; a<10; a++){
    for(int i =0; i<5; i++){
      I(10-a);
    }
  }
  for(int i =0; i<100; i++){
      I(1);
    }
}

void showFingerLoc(int x, int y){
  allLEDsOFF();
  digitalWrite(rowsp[x], HIGH);
  digitalWrite(rowsn[y], HIGH);
}



