import joblib
import numpy as np
from landmark_extraction import extract_landmarks_from_camera  # 카메라에서 랜드마크 추출 함수 import

# 모델 로드
model = joblib.load('gesture_model.pkl')

# 예측 함수
def predict_gesture(landmarks):
    landmarks = np.array(landmarks).flatten().reshape(1, -1)
    prediction = model.predict(landmarks)
    return prediction[0]

# 실시간으로 예측을 수행하는 함수
def predict_from_camera():
    print("카메라에서 손동작을 추적합니다...")
    while True:
        landmarks = extract_landmarks_from_camera()  # 실시간으로 랜드마크 추출
        if landmarks is None:
            continue
        
        gesture = predict_gesture(landmarks)  # 예측
        print(f"예측된 손동작: {gesture}")  # 예측된 동작 출력

if __name__ == '__main__':
    predict_from_camera()  # 실시간 예측 실행
