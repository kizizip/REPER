# import joblib
# import numpy as np
# from sklearn.neural_network import MLPClassifier
# from landmark_extraction import extract_landmarks_from_camera  # 랜드마크 추출 함수 임포트

# # 정규화 함수 정의
# def normalize_landmarks(landmarks, selected_landmarks=None):
#     # 정규화할 랜드마크를 선택적으로 받기
#     if selected_landmarks is None:
#         selected_landmarks = [0, 1, 5, 9, 13, 17]  # 예시: 손목과 각 손가락 끝만 선택
    
#     normalized_landmarks = []
#     wrist = landmarks[0]  # 손목 기준으로 정규화
    
#     # 선택한 랜드마크들만 정규화
#     for idx in selected_landmarks:
#         landmark = landmarks[idx]
#         normalized_landmarks.append([
#             (landmark[0] - wrist[0]),  # x 값 정규화
#             (landmark[1] - wrist[1]),  # y 값 정규화
#             (landmark[2] - wrist[2])   # z 값 정규화
#         ])
    
#     return np.array(normalized_landmarks).flatten()  # 1차원 배열로 반환

# def train_model():
#     X_train = []
#     y_train = []

#     print("학습을 시작합니다. 손동작을 인식하여 '0' 또는 '1'을 입력하세요.")
#     while True:
#         landmarks = extract_landmarks_from_camera()  # 실시간으로 랜드마크 추출
#         if landmarks is None:
#             continue
        
#         # 정규화할 랜드마크 선택
#         selected_landmarks = [0, 1, 5, 9, 13]  # 예시로 손목과 각 손가락 끝만 선택
        
#         # 랜드마크 정규화
#         landmarks = normalize_landmarks(landmarks, selected_landmarks)

#         label = int(input("손동작 레이블을 입력하세요 (0: 책 넘기기, 1: 따봉): "))
        
#         # 정규화된 랜드마크 추가
#         X_train.append(landmarks)
#         y_train.append(label)
#         print(f"샘플 {len(X_train)}개 추가됨 - 레이블: {label}")

#         if len(X_train) > 5:  
#             X_train = np.array(X_train)
#             y_train = np.array(y_train)
            
#             # 모델 학습
#             model = MLPClassifier(hidden_layer_sizes=(100,), max_iter=500)
#             model.fit(X_train, y_train)
#             print("모델 학습 완료!")
            
#             # 학습된 모델을 저장
#             joblib.dump(model, 'gesture_model.pkl')
#             print("모델이 'gesture_model.pkl'로 저장되었습니다.")
#             break

#     return model

# # 모델 학습 함수 호출
# train_model()


import joblib
import numpy as np
from sklearn.neural_network import MLPClassifier

from landmark_extraction import extract_landmarks_from_video, get_video_files  # 영상 파일에서 랜드마크 추출 함수 임포트

# 모델 학습 함수
def train_model():
    X_train = []
    y_train = []

    video_files = get_video_files('/Users/seohyunlee/Desktop/motionData')  # 비디오 파일 목록 가져오기
    print("학습을 시작합니다. 손동작을 인식하여 '0' 또는 '1'을 입력하세요.")

    for video_file in video_files:
        print(f"파일 {video_file} 처리 중...")

        landmarks = extract_landmarks_from_video(video_file)  # 영상에서 랜드마크 추출
        if landmarks is None:
            continue
        
        # 랜드마크의 크기 확인 (같은 크기인지 확인)
        if len(landmarks.shape) != 2 or landmarks.shape[1] != 15:  # 15는 정규화된 랜드마크의 크기 (5개의 랜드마크, 각 3개 좌표)
            print(f"잘못된 랜드마크 크기: {landmarks.shape}, 파일을 건너뜁니다.")
            continue
        
        # 랜드마크를 일정 크기로 자르거나 패딩 처리하기 (프레임 수가 일정하지 않다면)
        if landmarks.shape[0] < 5:  # 랜드마크가 5개 이하일 경우, 건너뛰기
            print(f"랜드마크가 부족한 영상: {video_file}, 건너뜁니다.")
            continue
        elif landmarks.shape[0] > 5:  # 랜드마크가 5개 이상일 경우, 필요한 부분만 추출
            landmarks = landmarks[:5]  # 상위 5개의 랜드마크만 사용
        
        # 랜드마크 레이블 입력 받기
        label = int(input(f"파일 {video_file}에 대한 손동작 레이블을 입력하세요 (0: 책 넘기기, 1: 따봉): "))
        
        X_train.append(landmarks.flatten())  # 랜드마크 추가
        y_train.append(label)  # 레이블 추가
        print(f"샘플 {len(X_train)}개 추가됨 - 레이블: {label}")

    # 학습
    X_train = np.array(X_train)
    y_train = np.array(y_train)

    # 모델 학습
    model = MLPClassifier(hidden_layer_sizes=(100,), max_iter=500)
    model.fit(X_train, y_train)
    print("모델 학습 완료!")
    
    # 학습된 모델을 저장
    joblib.dump(model, 'gesture_model.pkl')
    print("모델이 'gesture_model.pkl'로 저장되었습니다.")

# 모델 학습 함수 호출
train_model()
