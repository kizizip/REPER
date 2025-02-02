package com.d109.reper.service;

import com.d109.reper.domain.Animation;
import com.d109.reper.domain.Ingredient;
import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.RecipeStep;
import com.d109.reper.repository.AnimationRepository;
import com.d109.reper.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final AnimationRepository animationRepository;
    private final EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    // 레시피 등록 (개선된 매핑 로직 적용)
    @Transactional
    public void saveRecipes(List<Recipe> recipes) {
        logger.info("트랜잭션 시작 - 레시피 저장");

        // 1) animation 테이블에 저장된 모든 Animation 정보 조회
        List<Animation> animations = animationRepository.findAll();

        // 2) 각 레시피에 대해 처리
        for (Recipe recipe : recipes) {
            logger.info("레시피 저장: {}", recipe.getRecipeName());
            recipe.setCreatedAt(LocalDateTime.now());

            // 레시피 단계(RecipeStep) 처리
            List<RecipeStep> originalSteps = new ArrayList<>(recipe.getRecipeSteps());
            recipe.getRecipeSteps().clear();  // 기존 단계 리스트 초기화

            for (int i = 0; i < originalSteps.size(); i++) {
                RecipeStep originalStep = originalSteps.get(i);
                RecipeStep step = new RecipeStep();
                step.setInstruction(originalStep.getInstruction());
                step.setStepNumber(i + 1);
                step.setCreatedAt(LocalDateTime.now());
                step.setUpdatedAt(LocalDateTime.now());

                // 개선된 도메인 규칙 기반 애니메이션 매핑 처리
                String animationUrl = getMappedAnimationUrl(step.getInstruction(), animations);
                if (animationUrl != null) {
                    step.setAnimationUrl(animationUrl);
                }
                recipe.addRecipeStep(step);  // 양방향 연관관계 설정
            }

            // 재료 처리 (기존 로직 유지)
            List<Ingredient> ingredients = recipe.getIngredients();
            recipe.setIngredients(new ArrayList<>());
            for (Ingredient ingredient : ingredients) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setIngredientName(ingredient.getIngredientName());
                recipe.addIngredient(newIngredient);
            }

            recipeRepository.save(recipe);
            logger.info("레시피 저장 완료: {}", recipe.getRecipeName());
        }
    }

    /**
     * 레시피 단계의 instruction과 애니메이션의 키워드를 정규화한 후,
     * 토큰 단위 매칭 점수를 계산하여 가장 잘 맞는 애니메이션 URL을 반환합니다.
     *
     * @param instruction 해당 레시피 단계의 설명
     * @param animations  전체 애니메이션 목록
     * @return 매핑된 애니메이션 URL (매칭 실패 시 null)
     */
    private String getMappedAnimationUrl(String instruction, List<Animation> animations) {
        if (instruction == null || instruction.trim().isEmpty()) return null;

        // instruction을 정규화 (소문자화, 불필요한 기호 제거)
        String normalizedInstruction = normalize(instruction);
        double bestScore = 0.0;
        String bestUrl = null;

        for (Animation anim : animations) {
            // 애니메이션 키워드 정규화 및 토큰화 (공백 기준)
            String normalizedKeyword = normalize(anim.getKeyword());
            String[] tokens = normalizedKeyword.split("\\s+");

            int matchCount = 0;
            for (String token : tokens) {
                if (tokenMatches(token, normalizedInstruction)) {
                    matchCount++;
                }
            }
            double score = (double) matchCount / tokens.length;

            // 만약 모든 토큰이 매칭되면 바로 해당 URL 반환
            if (score == 1.0) {
                return anim.getAnimationUrl();
            }
            // 최고 점수 갱신
            if (score > bestScore) {
                bestScore = score;
                bestUrl = anim.getAnimationUrl();
            }
        }
        // 일정 임계값(예: 0.7 이상) 이상의 점수가 있으면 해당 애니메이션 반환
        return bestScore >= 0.7 ? bestUrl : null;
    }

    /**
     * 입력된 텍스트를 소문자로 변환하고, 알파벳과 한글, 공백만 남기도록 정규화합니다.
     *
     * @param text 원본 문자열
     * @return 정규화된 문자열
     */
    private String normalize(String text) {
        // 숫자와 특수문자는 제거하고 공백은 하나로 치환합니다.
        return text.toLowerCase().replaceAll("[^가-힣a-z\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * 애니메이션 키워드의 토큰과 instruction 간의 매칭 여부를 판단합니다.
     * 동사의 경우 기본형과 변형(예: "간다" vs "갈아")을 고려합니다.
     *
     * @param token       애니메이션 키워드의 개별 토큰 (예: "간다", "붓다")
     * @param instruction 정규화된 instruction 문자열
     * @return 매칭되면 true, 아니면 false
     */
    private boolean tokenMatches(String token, String instruction) {
        // 동사 및 액션에 대한 변형 처리
        if (token.equals("간다")) {
            // instruction에 "갈"이 포함되면 매칭
            return instruction.contains("갈");
        } else if (token.equals("붓다")) {
            return instruction.contains("붓");
        } else if (token.equals("담다")) {
            return instruction.contains("담");
        } else if (token.equals("준비")) {
            return instruction.contains("준비");
        } else if (token.equals("스팀")) {
            return instruction.contains("스팀");
        } else if (token.equals("펌프")) {
            return instruction.contains("펌프");
        } else if (token.equals("드리즐링")) {
            return instruction.contains("드리즐");
        } else if (token.equals("추출")) {
            return instruction.contains("추출");
        }
        // 그 외 일반 토큰은 단순 substring 매칭
        return instruction.contains(token);
    }

    // 레시피 조회(가게별)
    public List<Recipe> findRecipesByStore(Long storeId) {
        return recipeRepository.findByStore(storeId);
    }

    // 레시피 단건 조회
    public Recipe findRecipe(Long recipeId) {
        return recipeRepository.findOne(recipeId);
    }

    // 레시피 단건 삭제
    @Transactional
    public void deleteRecipe(Long recipeId) {
        recipeRepository.delete(recipeId);
    }
}
