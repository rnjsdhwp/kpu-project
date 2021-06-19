import cv2
import tensorflow.keras
import numpy as np
import requests, time
from bs4 import BeautifulSoup
from picamera import PiCamera

# 이미지 전처리
def preprocessing(frame):
    # 사이즈 조정
    size = (224, 224)
    frame_resized = cv2.resize(frame, size, interpolation=cv2.INTER_AREA)
    
    # 이미지 정규화
    frame_normalized = (frame_resized.astype(np.float32) / 127.0) - 1
    
    # 이미지 차원 재조정 - 예측을 위한 reshape
    frame_reshaped = frame_normalized.reshape((1, 224, 224, 3))
    
    return frame_reshaped

## 학습된 모델 불러오기
#model_filename = 'converted_keras/keras_model.h5'
model_filename = 'keras_model.h5'
model = tensorflow.keras.models.load_model(model_filename)

capture = cv2.VideoCapture(0)

# 캡쳐 프레임 사이즈 조절
capture.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)


while True:
    ret, frame = capture.read()
    frame_fliped = cv2.flip(frame, 1)
        
    if cv2.waitKey(200) > 0: 
        break
    
    preprocessed = preprocessing(frame_fliped)
    prediction = model.predict(preprocessed)
    
    if prediction[0,1] < prediction[0,0] and prediction[0,2] < prediction[0,0]:
        cv2.putText(frame_fliped, 'road', (20, 40), cv2.FONT_HERSHEY_PLAIN, 4, (255, 255, 255))
        requests.post('http://172.30.1.28:8000/http-test.php',{'roadstate':'road'}).text
        
    elif prediction[0,0] < prediction[0,1] and prediction[0,2] < prediction[0,1]:
        cv2.putText(frame_fliped, 'wet road', (20, 40), cv2.FONT_HERSHEY_PLAIN, 4, (255, 255, 255))
        requests.post('http://172.30.1.28:8000/http-test.php',{'roadstate':'wetroad'}).text
        
    elif prediction[0,0] < prediction[0,2] and prediction[0,1] < prediction[0,2]:
        cv2.putText(frame_fliped, 'ice road', (20, 40), cv2.FONT_HERSHEY_PLAIN, 4, (255, 255, 255))
        requests.post('http://172.30.1.28:8000/http-test.php',{'roadstate':'iceroad'}).text
    
    cv2.imshow("VideoFrame", frame_fliped)
    time.sleep(3)
    
    
capture.release()
cv2.destroyAllWindows()