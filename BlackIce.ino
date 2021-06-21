#include <DHT.h>
#include <SoftwareSerial.h>

#define DHTPIN1 4
#define DHTPIN2 5
#define DHTTYPE DHT22

DHT dht1(DHTPIN1, DHTTYPE);
DHT dht2(DHTPIN2, DHTTYPE);

int AA = 10; //물펌프
int AB = 6; //물펌프
int Raindrops_pin = A0;
int Blackice = 1;

float humi1;
float temp1;
float humi2;
float temp2;

int raindrops; //Stores raindrops value
String weather; //Sunny, Rainny

SoftwareSerial mySerial(2,3); //RX,TX
String ssid = "SBLAPTOP";
String PASSWORD = "12341234";
String host = "192.168.43.49";
void connectWifi(){
  String join ="AT+CWJAP=\""+ssid+"\",\""+PASSWORD+"\"";
  Serial.println("Connect Wifi...");
  mySerial.println(join);
  delay(10000);
  if(mySerial.find("OK"))
  {
    Serial.print("WIFI Successfully Connected!!\n");
    }else
    {
      Serial.println("connect timeout\n");
      }
      delay(1000);
      }
      
void httpclient(String char_input){
        delay(100);
        Serial.println("connect TCP...");
        mySerial.println("AT+CIPSTART=\"TCP\",\""+host+"\",8000");
        delay(500);
        
        if(Serial.find("ERROR"))
        {
        Serial.println("TCP Connect Error");
        return;
        }
        
        else{
          Serial.print("TCP Successfully Connected!!\n");
          delay(3000);
        }
        
        Serial.println("Send data...");
        String url=char_input;
        String h = " ";
        String cmd="GET /process.php?temp1="+url+" HTTP/1.0\r\n\r\n";
        mySerial.print("AT+CIPSEND=");
        mySerial.println(cmd.length());
        Serial.print("AT+CIPSEND=");
        Serial.println(cmd.length());
        delay(500);
        mySerial.println(cmd);
        Serial.println(cmd);
        delay(100);
        if(Serial.find("ERROR")) return;
        mySerial.println("AT+CIPCLOSE");
        delay(100);
            }

void setup()
{
  Serial.begin(9600);
  mySerial.begin(9600);
  dht1.begin();
  dht2.begin();
  pinMode(AA, OUTPUT);  // AA를 출력 핀으로 설정
  pinMode(AB, OUTPUT);  // AB를 출력 핀으로 설정
  connectWifi();
  delay(500);
  }

void loop()
{
  humi1 = dht1.readHumidity();
  temp1 = dht1.readTemperature();
  humi2 = dht2.readHumidity();
  temp2 = dht2.readTemperature();
  
  raindrops = analogRead(Raindrops_pin);

  if(analogRead(A0)<700){
    weather = "Rainny";
  }
  else{
    weather = "Sunny";
  }
  
  String str_output = String(temp1)+"&humi1="+String(humi1)+"&temp2="+String(temp2)+"&humi2="+String(humi2)+"&weather="+String(weather);
  delay(1000);
  
  if(Blackice == 1){
  digitalWrite(AA, HIGH);  // 정방향으로 모터 회전
  digitalWrite(AB, LOW);
  delay(5000);  // 5초동안 상태 유지(1000ms = 5s)
 
  digitalWrite(AA, LOW);  // 모터 정지
  digitalWrite(AB, LOW);
  delay(5000);  // 5초동안 상태 유지(1000ms = 5s)
  }
  
  httpclient(str_output);
  delay(1000);
  
  //Serial.find("+IPD");
  while (mySerial.available())
  {
    char response = mySerial.read();
    Serial.write(response);
    if(response=='\r') Serial.print('\n');
    }
    Serial.println("\n==================================\n");
    delay(2000);
 }
