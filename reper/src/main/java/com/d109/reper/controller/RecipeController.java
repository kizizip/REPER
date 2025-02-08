package com.d109.reper.controller;

import com.d109.reper.domain.Recipe;
import com.d109.reper.domain.Store;
import com.d109.reper.elasticsearch.NoticeDocument;
import com.d109.reper.elasticsearch.RecipeDocument;
import com.d109.reper.repository.StoreRepository;
import com.d109.reper.response.RecipeResponseDto;
import com.d109.reper.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private static final String PYTHON_SERVER_URL = "http://i12d109.p.ssafy.io:20250/upload";

    //레시피 파일 업로드 + python 서버로 전송
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "레시피 파일 업로드",
            description = """
                    레시피 파일 업로드 -> python 서버로 전송 -> 레시피 저장((POST)/stores/{storeId}/recipes)까지 연결됨
                    그냥 이것만 실행하시면 저장까지 가능.
                    """
            )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The recipe has been successfully saved."),
            @ApiResponse(responseCode = "400", description = "Required part 'file' is not present."),
            @ApiResponse(responseCode = "400", description = "Store not found for the given storeId."),
            @ApiResponse(responseCode = "500", description = "Error processing the file."),
            @ApiResponse(responseCode = "500", description = "Python server response error.")
    })
    public ResponseEntity<String> uploadRecipeFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("storeId") Long storeId) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Required part 'file' is not present.");
        }

        boolean storeExists = storeRepository.existsById(storeId);
        if (!storeExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Store not found for the given storeId.");
        }

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
                return ResponseEntity.ok().body("The recipe has been successfully saved.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Python server response error.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file.");
        }
    }


    // 레시피 저장
    @PostMapping("/stores/{storeId}/recipes")
    public ResponseEntity<Void> createRecipes(@PathVariable Long storeId,
                                              @RequestBody List<Recipe> recipes) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("Store not found for the given storeId.")); // Store 조회

        for (Recipe recipe : recipes) {
            recipe.setStore(store); // storeId를 Recipe에 연결하여 특정 매장에 속하도록 설정
        }

        recipeService.saveRecipes(recipes);
        return ResponseEntity.ok().build();
    }


    //레시피 조회(가게별)
    @GetMapping("/stores/{storeId}/recipes")
    @Operation(summary = "레시피 조회(가게별)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "json"),
            @ApiResponse(responseCode = "404", description = "Store not found.")
    })
    public ResponseEntity<List<RecipeResponseDto>> getRecipeByStore(@PathVariable Long storeId) {
        List<Recipe> recipes = recipeService.findRecipesByStore(storeId);

        List<RecipeResponseDto> responses = recipes.stream()
                .map(RecipeResponseDto::new)
                .toList();

        return ResponseEntity.ok(responses);
    }


    //레시피 조회(단건)
    @GetMapping("/recipes/{recipeId}")
    @Operation(summary = "레시피 조회(단건)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "json"),
            @ApiResponse(responseCode = "404", description = "Recipe not found.")
    })
    public ResponseEntity<RecipeResponseDto> getRecipe(@PathVariable Long recipeId) {
        Recipe recipe = recipeService.findRecipe(recipeId);
        return ResponseEntity.ok(new RecipeResponseDto(recipe));
    }


    //레시피 삭제(단건)
    @DeleteMapping("/recipes/{recipeId}")
    @Operation(summary = "레시피 삭제(단건)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe successfully deleted."),
            @ApiResponse(responseCode = "404", description = "Recipe not found.")
    })
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok().body("Recipe successfully deleted.");
    }


    // 가게별 레시피 검색
    @GetMapping("/stores/{storeId}/recipes/search")
    @Operation(summary = "레시피 검색")
    public List<RecipeDocument> searchRecipeName(
            @PathVariable Long storeId,
            @RequestParam("recipeName") String keyword) {

        return recipeService.searchRecipeName(storeId, keyword);
    }


    // 가게별 레시피에서 재료 포함 검색
    @GetMapping("/stores/{storeId}/recipes/search/include")
    @Operation(summary = "재료 포함 검색")
    public List<RecipeDocument> searchIncludeIngredient(
            @PathVariable Long storeId,
            @RequestParam("ingredient") String keyword) {

        return recipeService.searchIncludeIngredient(storeId, keyword);
    }


    // 재료 미포함 검색
    @GetMapping("/stores/{storeId}/recipes/search/exclude")
    @Operation(summary = "재료 미포함 검색")
    public ResponseEntity<List<RecipeDocument>> searchExcludeIngredient(
            @PathVariable Long storeId,
            @RequestParam("ingredient") String keyword) {

        List<RecipeDocument> results = recipeService.searchExcludeIngredient(storeId, keyword);

        if (results.isEmpty()) {
            return  ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(results);
    }

}
