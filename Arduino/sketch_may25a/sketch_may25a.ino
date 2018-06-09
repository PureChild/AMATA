#include <SPI.h>
#include <MFRC522.h>
#include <SoftwareSerial.h>
#define BT_RXD 8
#define BT_TXD 7
SoftwareSerial bluetooth(BT_RXD, BT_TXD);
#define RST_PIN   9                            // reset핀은 9번으로 설정
#define SS_PIN    10                           // SS핀은 10번으로 설정
                                               // SS핀은 데이터를 주고받는 역할의 핀( SS = Slave Selector )
int r = 1;
int b = 4;
MFRC522 mfrc(SS_PIN, RST_PIN);                 

void setup(){
  Serial.begin(9600);                         // 시리얼 통신, 속도는 9600
  SPI.begin();                                
  bluetooth.begin(9600);

pinMode(1, OUTPUT);
pinMode(4, OUTPUT);
pinMode(5, OUTPUT);
                                             
  mfrc.PCD_Init();                               
}

void loop(){
  if (bluetooth.available()){
    Serial.write(bluetooth.read());
  }
  if (Serial.available()){
    bluetooth.write(Serial.read());
  }
  
  if ( !mfrc.PICC_IsNewCardPresent() || !mfrc.PICC_ReadCardSerial() ) {   
                                               // 태그 접촉이 되지 않았을때 또는 ID가 읽혀지지 않았을때
    delay(500);                                // 0.5초 딜레이 
    return;                                    // return 
 } 

  Serial.print("Card UID:");                  // 태그의 ID출력
  String uid = String(mfrc.uid.uidByte[0]) +" "+ String(mfrc.uid.uidByte[1]) +" "+ String(mfrc.uid.uidByte[2]) +" "+ String(mfrc.uid.uidByte[3]);  // 태그 UID

bluetooth.println(uid);
Serial.print(uid);
Serial.print('\n');
delay(1000);                                  // 1초 딜레이
}


