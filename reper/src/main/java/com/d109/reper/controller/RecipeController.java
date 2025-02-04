package com.d109.reper.controller;

import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.Store;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final StoreRepository storeRepository;

    // spring boot에서 다른 서버(python)로 http 요청을 보낼 때 사용
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PYTHON_SERVER_URL = "http://localhost:5000/upload";

    //레시피 파일 업로드 + python 서버로 전송
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadRecipeFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("storeId") Long storeId) {
        try {
            // http 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 파일을 바이너리 데이터로 변환
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); //원본 파일 이름 유지
                }
            });
            body.add("storeId", storeId.toString());

            // python 서버에 파일 전송
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    PYTHON_SERVER_URL, requestEntity, String.class
            );

            // python 서버에서 받은 응답 반환
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Python 서버 응답 오류");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생");
        }
    }

    // 레시피 저장
    @PostMapping("/stores/{storeId}/recipes")
    public ResponseEntity<Void> createRecipes(@PathVariable Long storeId,
                                              @RequestBody List<Recipe> recipes) {
        Store store = storeRepository.findById(storeId).orElseThrow(); // Store 조회
        for (Recipe recipe : recipes) {
            recipe.setStore(store); // storeId를 Recipe에 연결하여 특정 매장에 속하도록 설정
        }
        recipeService.saveRecipes(recipes);
        return ResponseEntity.ok().build();
    }


    //레시피 조회(가게별)
    @GetMapping("/stores/{storeId}/recipes")
    public ResponseEntity<List<Recipe>> getRecipeByStore(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(recipeService.findRecipesByStore(storeId));
    }

    //레시피 조회(단건)
    @GetMapping("/recipes/{recipeId}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.findRecipe(recipeId));
    }

    //레시피 삭제(단건)
    @DeleteMapping("/recipes/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok().build();
    }
}
