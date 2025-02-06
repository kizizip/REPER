INSERT INTO user (created_at, email, password, phone, role, user_name) VALUES
(NOW(), 'owner1@example.com', 'password123', '010-1234-5678', 'OWNER', 'John Doe'),
(NOW(), 'staff1@example.com', 'password123', '010-2345-6789', 'STAFF', 'Jane Smith'),
(NOW(), 'owner2@example.com', 'password123', '010-3456-7890', 'OWNER', 'Alice Johnson'),
(NOW(), 'staff2@example.com', 'password123', '010-4567-8901', 'STAFF', 'Bob Brown'),
(NOW(), 'owner3@example.com', 'password123', '010-5678-9012', 'OWNER', 'Charlie Green'),
(NOW(), 'staff3@example.com', 'password123', '010-6789-0123', 'STAFF', 'Diana White'),
(NOW(), 'owner4@example.com', 'password123', '010-7890-1234', 'OWNER', 'Ethan Black'),
(NOW(), 'staff4@example.com', 'password123', '010-0123-5678', 'STAFF', 'Rachel Coral');

INSERT INTO Store (store_id, user_id, store_name) VALUES
(1, 1, 'Sunrise Bakery'),
(2, 1, 'Moonlight Cafe'),
(3, 2, 'Green Garden Restaurant'),
(4, 2, 'Ocean View Diner'),
(5, 2, 'Happiness'),
(6, 3, 'Spice Hub'),
(7, 3, 'Golden Spoon'),
(8, 4, 'Urban Eats'),
(9, 4, 'Mountain Grill'),
(10, 4, 'jihyejcafe');

INSERT INTO orders (order_id, order_date, store_id, is_completed) VALUES (1, '2025-01-16 10:51:29', 1, true);
INSERT INTO orders (order_id, order_date, store_id, is_completed) VALUES (2, '2025-01-14 10:51:29', 1, true);
INSERT INTO orders (order_id, order_date, store_id, is_completed) VALUES (3, '2024-12-30 10:51:29', 2, true);
INSERT INTO orders (order_id, order_date, store_id, is_completed) VALUES (4, '2025-01-17 10:51:29', 2, true);
INSERT INTO orders (order_id, order_date, store_id, is_completed) VALUES (5, '2025-01-27 10:51:29', 3, true);

INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (1, 1, 1, 4, '스트로우 없이');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (2, 1, 24, 2, '시럽 빼고');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (3, 1, 3, 1, '아이싱 없이');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (1, 2, 20, 3, '따뜻하게');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (2, 2, 22, 4, '시럽 빼고');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (3, 2, 16, 5, '우유 추가');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (1, 3, 27, 3, '아이싱 없이');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (2, 3, 20, 1, '설탕 추가');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (1, 4, 33, 2, '스트로우 없이');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (2, 4, 37, 4, '샷 추가');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (1, 5, 29, 5, '우유 추가');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (2, 5, 15, 1, '설탕 추가');
INSERT INTO order_detail (order_detail_id, order_id, recipe_id, quantity, customer_request) VALUES (3, 5, 26, 2, '스트로우 없이');

INSERT INTO notice (store_id, user_id, title, content, updated_at) VALUES
(1, 1, '다음주 근무 일정 공유', '다음주 근무 일정 확인 부탁드립니다. 변경 사항 있으면 미리 알려주세요.', NOW()),
(1, 1, '신제품: 바질 치아바타 출시!', '바질 치아바타가 새롭게 출시되었습니다. 고객 응대 시 추천해 주세요!', NOW() - INTERVAL 3 HOUR),
(1, 1, '청소 구역 변경 안내', '이번 주부터 청소 구역이 변경됩니다. 상세 내용은 공지판을 참고하세요.', NOW() - INTERVAL 5 HOUR),
(1, 1, '주말 근무 일정 확정', '주말 근무 일정이 확정되었습니다. 변경이 필요한 분은 미리 조정해 주세요.', NOW() - INTERVAL 1 DAY),
(1, 1, '신메뉴: 딸기 크림 브륄레 라떼', '신메뉴 출시! 고객들에게 적극 홍보해주세요.', NOW() - INTERVAL 10 MINUTE),
(2, 1, '신메뉴: 말차 라떼 출시!', '말차 라떼가 새롭게 추가되었습니다. 고객들에게 적극 추천 바랍니다.', NOW()),
(2, 1, '에스프레소 머신 점검 예정', '이번 주 금요일 오전 10시부터 12시까지 머신 점검이 있습니다. 참고 바랍니다.', NOW() - INTERVAL 2 DAY),
(2, 1, '음료 제조 속도 향상 안내', '바쁜 시간대 주문 처리를 빠르게 할 수 있도록 동선과 역할을 조정했습니다.', NOW() - INTERVAL 30 MINUTE),
(3, 2, '주방 위생 관리 강화', '주방 위생 점검이 강화되었습니다. 모든 직원은 위생 규정을 철저히 준수해 주세요.', NOW()),
(3, 2, '테이블 정리 기준 변경', '고객 퇴장 후 테이블 세팅 기준이 변경되었습니다. 공지사항 참고 바랍니다.', NOW() - INTERVAL 45 MINUTE),
(3, 2, '메뉴 리뉴얼 테스트 진행', '일부 메뉴가 테스트 운영됩니다. 고객 피드백을 적극 수집해주세요.', NOW() - INTERVAL 5 HOUR),
(4, 2, '주말 근무 일정 확정', '주말 근무 일정이 확정되었습니다. 변경이 필요한 분은 미리 조정해 주세요.', NOW()),
(4, 2, '해변가 테라스 정리 기준', '야외 테라스 이용 고객 증가로 정리 기준이 변경되었습니다. 확인 바랍니다.', NOW() - INTERVAL 3 HOUR),
(4, 2, '단체 예약 대응 방안', '단체 예약이 많아졌습니다. 서빙 시 협력해서 빠른 응대 부탁드립니다.', NOW() - INTERVAL 1 DAY),
(5, 2, '신메뉴: 딸기 크림 브륄레 라떼', '신메뉴 출시! 고객들에게 적극 홍보해주세요.', NOW()),
(5, 2, '커피 원두 변경 안내', '이번 주부터 원두가 변경됩니다. 기존 원두와 차이점을 숙지해 주세요.', NOW() - INTERVAL 20 MINUTE),
(5, 2, 'POS 시스템 점검', '이번 주 수요일 오전 2시 POS 시스템 점검이 예정되어 있습니다.', NOW() - INTERVAL 2 DAY),
(6, 3, '스파이스 강도 변경 안내', '몇몇 메뉴의 스파이스 강도가 조정되었습니다. 주문 시 설명해주세요.', NOW()),
(6, 3, '배달 서비스 개시', '배달 서비스가 시작됩니다. 배달 주문에 대한 매뉴얼을 숙지해 주세요.', NOW() - INTERVAL 6 HOUR),
(6, 3, '주문 접수 마감 시간 조정', '마감 시간 조정으로 인해 라스트 오더 시간이 변경되었습니다. 참고 바랍니다.', NOW() - INTERVAL 30 MINUTE),
(7, 3, '고객 서비스 교육 안내', '고객 응대 매뉴얼이 개정되었습니다. 새로운 교육 일정 확인 바랍니다.', NOW()),
(7, 3, '이달의 추천 메뉴 변경', '이번 달 추천 메뉴가 변경되었습니다. 숙지하고 고객에게 안내해 주세요.', NOW() - INTERVAL 4 HOUR),
(7, 3, '단체 예약 안내', '이번 주 금요일 단체 예약이 있습니다. 최상의 서비스 부탁드립니다.', NOW() - INTERVAL 1 DAY),
(8, 4, '배달 픽업 장소 변경', '배달 기사님들이 헷갈리지 않도록 픽업 장소가 변경되었습니다. 확인 바랍니다.', NOW()),
(8, 4, '매장 내 좌석 배치 변경', '고객 편의를 위해 좌석 배치를 변경했습니다. 새로운 배치 참고하세요.', NOW() - INTERVAL 2 HOUR),
(8, 4, '할인 프로모션 진행', '이번 주말 할인 프로모션이 있습니다. 고객들에게 적극 안내 바랍니다.', NOW() - INTERVAL 5 MINUTE),
(9, 4, '화로 관리 주의사항', '화로 사용 시 주의할 점이 업데이트되었습니다. 안전 지침을 숙지해 주세요.', NOW()),
(9, 4, '기상 안내', '기상 상황에 따라 운영이 변경될 수 있습니다. 당일 공지를 확인해 주세요.', NOW() - INTERVAL 3 DAY),
(9, 4, '예약 시스템 개선', '온라인 예약 시스템이 개선되었습니다. 고객 응대 시 참고해 주세요.', NOW() - INTERVAL 1 DAY);

INSERT INTO store_employee (user_id, store_id, is_employed) VALUES
((SELECT user_id FROM user WHERE email = 'staff1@example.com'), 1, true),
((SELECT user_id FROM user WHERE email = 'staff2@example.com'), 2, false),
((SELECT user_id FROM user WHERE email = 'staff3@example.com'), 3, true),
((SELECT user_id FROM user WHERE email = 'staff4@example.com'), 4, true),
((SELECT user_id FROM user WHERE email = 'staff1@example.com'), 2, true),
((SELECT user_id FROM user WHERE email = 'staff3@example.com'), 4, true),
((SELECT user_id FROM user WHERE email = 'staff2@example.com'), 1, false),
((SELECT user_id FROM user WHERE email = 'staff4@example.com'), 5, true);