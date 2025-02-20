//package com.d109.reper.elasticsearch;
//
//import com.d109.reper.domain.*;
//import com.d109.reper.repository.RecipeJpaRepository;
//import com.d109.reper.repository.StoreRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.IntStream;
//
//@Component
//@RequiredArgsConstructor
//public class DummyDataInitializer implements CommandLineRunner {
//
//    private final RecipeJpaRepository recipeRepository; // 레시피 리포지토리만 사용
//    private final StoreRepository storeRepository;
//
//    private final Random random = new Random();
//    private final int TOTAL_RECIPES = 500; // 새로 추가할 레시피 개수
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//
//        Store store = storeRepository.findById(1L)
//                .orElseThrow(()-> new RuntimeException("not exist store 1"));
//
//        long currentRecipeCount = recipeRepository.count(); // 현재 레시피 개수 확인
//        long startIndex = currentRecipeCount + 1; // 새 레시피의 시작 ID
//
//        System.out.println("현재 레시피 개수: " + currentRecipeCount);
//        System.out.println("새 레시피 추가 시작 ID: " + startIndex);
//
//        List<String> baseRecipeNames = Arrays.asList(
//                "에스프레소", "아메리카노", "카페라떼", "카푸치노", "바닐라라떼",
//                "헤이즐넛라떼", "아인슈페너", "카페모카", "카라멜마끼아또", "초콜릿 라떼",
//                "고구마 라떼", "딸기라떼", "아이스티", "히비스커스", "얼그레이",
//                "레몬차", "자몽차", "대추차", "레몬 에이드", "자몽 에이드",
//                "청포도 에이드", "요거트 스무디", "딸기요거트 스무디", "블루베리요거트 스무디",
//                "자바칩 프라페", "민트초코 프라페", "쿠앤크 프라페"
//        );
//
//        List<RecipeCategory> categories = Arrays.asList(RecipeCategory.values());
//        List<RecipeType> types = Arrays.asList(RecipeType.values());
//
//        IntStream.range(0, TOTAL_RECIPES).forEach(i -> {
//            long recipeId = startIndex + i;
//            String baseName = baseRecipeNames.get(random.nextInt(baseRecipeNames.size()));
//            String modifiedName = modifyRecipeName(baseName, recipeId);
//
//            Recipe recipe = new Recipe();
//            recipe.setRecipeName(modifiedName);
//            recipe.setStore(store); // Store ID를 1로 고정
//            recipe.setCategory(categories.get(random.nextInt(categories.size())));
//            recipe.setType(types.get(random.nextInt(types.size())));
//            recipe.setRecipeImg("https://dummyimage.com/200x200/000/fff&text=" + modifiedName);
//            recipe.setCreatedAt(LocalDateTime.now());
//
//            // 재료 추가 (2~5개)
//            int ingredientCount = random.nextInt(4) + 2;
//            IntStream.range(1, ingredientCount + 1).forEach(j -> {
//                Ingredient ingredient = new Ingredient();
//                ingredient.setIngredientName(modifiedName + " 재료 " + j);
//                recipe.addIngredient(ingredient); // 연관관계 메서드를 사용하여 추가
//            });
//
//            // 조리 단계 추가 (2~4단계)
//            int stepCount = random.nextInt(3) + 2;
//            IntStream.range(1, stepCount + 1).forEach(j -> {
//                RecipeStep step = new RecipeStep();
//                step.setStepNumber(j);
//                step.setInstruction(modifiedName + " 만드는 방법 단계 " + j);
//                step.setAnimationUrl("https://dummyurl.com/step" + j);
//                step.setCreatedAt(LocalDateTime.now());
//                step.setUpdatedAt(LocalDateTime.now());
//                recipe.addRecipeStep(step); // 연관관계 메서드를 사용하여 추가
//            });
//
//            recipeRepository.save(recipe); // 저장하면 관련된 Ingredient, RecipeStep도 자동으로 저장됨
//        });
//
//        System.out.println("✅ 기존 데이터 이후로 " + TOTAL_RECIPES + "개의 더미 레시피 데이터 추가 완료!");
//    }
//
//    // 레시피 이름을 랜덤하게 변형하는 메서드
//    private String modifyRecipeName(String baseName, long index) {
//        List<String> modifiers = Arrays.asList("스페셜", "달콤한", "진한", "라이트", "더블", "트리플", "고급", "프리미엄", "한정판", "클래식");
//        String modifier = modifiers.get(random.nextInt(modifiers.size()));
//        return baseName + " " + modifier + " " + index;
//    }
//}
