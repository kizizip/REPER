# import cv2
# import mediapipe as mp
# import numpy as np

# # 필요한 랜드마크만 선택 (예: 손목, 엄지, 검지 끝, 등)
# def get_required_landmarks(landmarks):
#     # 예시로 손목, 엄지, 검지, 중지 끝, 약지 끝을 선택
#     required_landmarks = [landmarks[i] for i in [0, 4, 8, 12, 16]]  # 0: 손목, 4: 엄지, 8: 검지, 12: 중지, 16: 약지
#     return np.array(required_landmarks)

# def normalize_landmarks(landmarks):
#     normalized_landmarks = []
#     wrist = landmarks[0]  # 손목 기준으로 정규화
    
#     for landmark in landmarks:
#         normalized_landmarks.append([
#             (landmark[0] - wrist[0]),  # x 값 정규화
#             (landmark[1] - wrist[1]),  # y 값 정규화
#             (landmark[2] - wrist[2])   # z 값 정규화
#         ])
    
#     return np.array(normalized_landmarks).flatten()  # 1차원 배열로 반환

# def extract_landmarks_from_camera():
#     # Mediapipe Hands 모델 초기화
#     mp_hands = mp.solutions.hands
#     hands = mp_hands.Hands()
#     mp_draw = mp.solutions.drawing_utils
    
#     # 카메라에서 실시간 비디오 캡처
#     cap = cv2.VideoCapture(0)  # 카메라 시작
#     all_landmarks = []  # 모든 손 랜드마크를 저장할 리스트
    
#     while True:
#         ret, frame = cap.read()
#         if not ret:
#             break
        
#         # RGB로 변환
#         frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
#         # 손 랜드마크 탐지
#         result = hands.process(frame_rgb)
        
#         # 손이 있으면 랜드마크 좌표 추출
#         if result.multi_hand_landmarks:
#             for hand_landmarks in result.multi_hand_landmarks:
#                 landmarks = []
#                 for landmark in hand_landmarks.landmark:
#                     landmarks.append([landmark.x, landmark.y, landmark.z])  # 21개의 좌표
                
#                 # 필요한 랜드마크만 추출
#                 required_landmarks = get_required_landmarks(landmarks)  # 필요한 랜드마크만 선택
#                 normalized_landmarks = normalize_landmarks(required_landmarks)  # 정규화
                
#                 all_landmarks.append(normalized_landmarks)  # 정규화된 손 랜드마크 저장
        
#         # 랜드마크가 있으면 화면에 그리기
#         if result.multi_hand_landmarks:
#             for hand_landmarks in result.multi_hand_landmarks:
#                 mp_draw.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

#         # 화면에 출력
#         cv2.imshow("Hand Landmarks", frame)
        
#         # 'q'를 누르면 종료
#         if cv2.waitKey(1) & 0xFF == ord('q'):
#             break
    
#     cap.release()  # 카메라 리소스 해제
#     cv2.destroyAllWindows()  # 모든 OpenCV 윈도우 닫기

#     if len(all_landmarks) > 0:
#         return np.array(all_landmarks)  # 정규화된 랜드마크 반환
#     else:
#         return None


import cv2
import mediapipe as mp
import numpy as np
import os

# 영상 파일을 디렉토리에서 가져오기
def get_video_files(directory):
    return [os.path.join(directory, f) for f in os.listdir(directory) if f.endswith(('.mp4', '.avi', '.mov'))]

# 필요한 랜드마크만 선택 (예: 손목, 엄지, 검지 끝, 등)
def get_required_landmarks(landmarks):
    required_landmarks = [landmarks[i] for i in [0, 4, 8, 12, 16]]  # 0: 손목, 4: 엄지, 8: 검지, 12: 중지, 16: 약지
    return np.array(required_landmarks)

def normalize_landmarks(landmarks):
    normalized_landmarks = []
    wrist = landmarks[0]  # 손목 기준으로 정규화
    
    for landmark in landmarks:
        normalized_landmarks.append([
            (landmark[0] - wrist[0]),  # x 값 정규화
            (landmark[1] - wrist[1]),  # y 값 정규화
            (landmark[2] - wrist[2])   # z 값 정규화
        ])
    
    return np.array(normalized_landmarks).flatten()  # 1차원 배열로 반환

# 영상을 통해 실시간으로 랜드마크 추출
def extract_landmarks_from_video(video_file):
    mp_hands = mp.solutions.hands
    hands = mp_hands.Hands()
    mp_draw = mp.solutions.drawing_utils
    
    cap = cv2.VideoCapture(video_file)  # 비디오 파일 열기
    all_landmarks = []  # 모든 손 랜드마크를 저장할 리스트
    
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break
        
        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
        result = hands.process(frame_rgb)
        
        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                landmarks = []
                for landmark in hand_landmarks.landmark:
                    landmarks.append([landmark.x, landmark.y, landmark.z])  # 21개의 좌표
                
                required_landmarks = get_required_landmarks(landmarks)  # 필요한 랜드마크만 선택
                normalized_landmarks = normalize_landmarks(required_landmarks)  # 정규화
                
                all_landmarks.append(normalized_landmarks)  # 정규화된 손 랜드마크 저장
        
        # 화면에 그리기
        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                mp_draw.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
        
    cap.release()  # 카메라 리소스 해제
    
    if len(all_landmarks) > 0:
        return np.array(all_landmarks)  # 정규화된 랜드마크 반환
    else:
        return None




def extract_landmarks_from_camera():
    # Mediapipe Hands 모델 초기화
    mp_hands = mp.solutions.hands
    hands = mp_hands.Hands()
    mp_draw = mp.solutions.drawing_utils
    
    # 카메라에서 실시간 비디오 캡처
    cap = cv2.VideoCapture(0)  # 카메라 시작
    all_landmarks = []  # 모든 손 랜드마크를 저장할 리스트
    
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        
        # RGB로 변환
        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
        # 손 랜드마크 탐지
        result = hands.process(frame_rgb)
        
        # 손이 있으면 랜드마크 좌표 추출
        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                landmarks = []
                for landmark in hand_landmarks.landmark:
                    landmarks.append([landmark.x, landmark.y, landmark.z])  # 21개의 좌표
                
                # 필요한 랜드마크만 추출
                required_landmarks = get_required_landmarks(landmarks)  # 필요한 랜드마크만 선택
                normalized_landmarks = normalize_landmarks(required_landmarks)  # 정규화
                
                all_landmarks.append(normalized_landmarks)  # 정규화된 손 랜드마크 저장
        
        # 랜드마크가 있으면 화면에 그리기
        if result.multi_hand_landmarks:
            for hand_landmarks in result.multi_hand_landmarks:
                mp_draw.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)

        # 화면에 출력
        cv2.imshow("Hand Landmarks", frame)
        
        # 'q'를 누르면 종료
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    
    cap.release()  # 카메라 리소스 해제
    cv2.destroyAllWindows()  # 모든 OpenCV 윈도우 닫기

    if len(all_landmarks) > 0:
        return np.array(all_landmarks)  # 정규화된 랜드마크 반환
    else:
        return None