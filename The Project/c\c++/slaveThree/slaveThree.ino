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
//  animateA();
  
}

void loop() {  
  if(SentMessage[0]< 6 && SentMessage[1]>5){
//    SentMessage[0] = map(SentMessage[0], 6,11,0,5);
    SentMessage[1] = map(SentMessage[1], 6,11,0,5);
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
  Serial.println(SentMessage[1]);
}


void A(int speedTime){
  int coords[][2] = {{0,0},{1,2},{2,4},{3,4},{4,2},
                     {5,0},{2,5},{3,5},{2,2},{3,2}};
  for(int i =0; i<10; i++){
    allLEDsOFF();
    digitalWrite(rowsp[coords[i][0]], HIGH);
    digitalWrite(rowsn[coords[i][1]], HIGH);
    delay(speedTime);
  }
}

void animateA(){
  for (int a=0; a<10; a++){
    for(int i =0; i<5; i++){
      A(10-a);
    }
  }
  for(int i =0; i<100; i++){
      A(1);
    }
}

void showFingerLoc(int x, int y){
  allLEDsOFF();
  digitalWrite(rowsp[x], HIGH);
  digitalWrite(rowsn[y], HIGH);
}



