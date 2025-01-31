package com.d109.reper.config;

import com.d109.reper.domain.Animation;
import com.d109.reper.repository.AnimationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnimationDataInitializer {

    private final AnimationRepository animationRepository;

    @PostConstruct
    public void initAnimationData() {

        animationRepository.save(new Animation("요거트 블랜더 간다", "https://cdn.lottielab.com/l/Dybf1Je8jZjR1L.json"));
        animationRepository.save(new Animation("사이다 붓다", "https://cdn.lottielab.com/l/6Y3CtYEieXZZrx.json"));
        animationRepository.save(new Animation("얼음 담다", "https://cdn.lottielab.com/l/7Hr5gt1NZkp3ci.json"));
        animationRepository.save(new Animation("얼음컵 준비", "https://cdn.lottielab.com/l/6QbraoGrKFWZwG.json"));
        animationRepository.save(new Animation("티백", "https://cdn.lottielab.com/l/72bFfMRVK8HRpz.json"));
        animationRepository.save(new Animation("아이스티 붓다", "https://cdn.lottielab.com/l/ErsA1gsymfKrp6.json"));
        animationRepository.save(new Animation("딸기 블랜더 간다", "https://cdn.lottielab.com/l/4gZp1dQ4pj4mB1.json"));
        animationRepository.save(new Animation("물 붓다", "https://cdn.lottielab.com/l/3ByZp4zyFxH5L6.json"));
        animationRepository.save(new Animation("스팀우유 붓다", "https://cdn.lottielab.com/l/C5V62488EpY7ZK.json"));
        animationRepository.save(new Animation("우유 붓다", "https://cdn.lottielab.com/l/CUbn1kkAFg2Mry.json"));
        animationRepository.save(new Animation("샷 붓다", "https://cdn.lottielab.com/l/DnWc3zyzDZBhAx.json"));
        animationRepository.save(new Animation("블루베리 블랜더 간다", "https://cdn.lottielab.com/l/FFMJLV74Qm6bfo.json"));
        animationRepository.save(new Animation("자바칩 블랜더 간다", "https://cdn.lottielab.com/l/ENtDzinSoQZDPq.json"));
        animationRepository.save(new Animation("민트초코 블랜더 간다", "https://cdn.lottielab.com/l/95Dg6EC3kRfgri.json"));
        animationRepository.save(new Animation("쿠앤크 블랜더 간다", "https://cdn.lottielab.com/l/7XThKYcimpCMMt.json"));
        animationRepository.save(new Animation("우유 스팀", "https://cdn.lottielab.com/l/9R9pCXFPnWSKTU.json"));
        animationRepository.save(new Animation("휘핑크림", "https://cdn.lottielab.com/l/FJ5ab6hdNstTRJ.json"));
        animationRepository.save(new Animation("바닐라 시럽 펌프", "https://cdn.lottielab.com/l/4kq2VaFenuZhLs.json"));
        animationRepository.save(new Animation("초코 파우더 컵", "https://cdn.lottielab.com/l/7FQ8q41J2syYBB.json"));
        animationRepository.save(new Animation("초코 파우더", "https://cdn.lottielab.com/l/BmkF6MHBAZSQdW.json"));
        animationRepository.save(new Animation("헤이즐넛 시럽 펌프", "https://cdn.lottielab.com/l/CQQ78SacnAXP8k.json"));
        animationRepository.save(new Animation("카라멜 드리즐링", "https://cdn.lottielab.com/l/EVPfK3e5skieMi.json"));
        animationRepository.save(new Animation("카라멜 시럽 펌프", "https://cdn.lottielab.com/l/qXGCejGzgtaXe0.json"));
        animationRepository.save(new Animation("고구마 파우더 컵", "https://cdn.lottielab.com/l/Co1f3bNMNDtEar.json"));
        animationRepository.save(new Animation("요거트 파우더 컵", "https://cdn.lottielab.com/l/4dN1uJNg62XiKZ.json"));
        animationRepository.save(new Animation("딸기 베이스 컵", "https://cdn.lottielab.com/l/2Y6Ggm7jWcNaJ6.json"));
        animationRepository.save(new Animation("레몬청 스푼", "https://cdn.lottielab.com/l/A95CqnJxqAfT79.json"));
        animationRepository.save(new Animation("자몽청 스푼", "https://cdn.lottielab.com/l/CLut8Kky51eJ7B.json"));
        animationRepository.save(new Animation("우유거품 스푼", "https://cdn.lottielab.com/l/3Zb2Fu2gKEqwdu.json"));
        animationRepository.save(new Animation("대추청 스푼", "https://cdn.lottielab.com/l/6Tm6WxmBktLg6d.json"));
        animationRepository.save(new Animation("초코소스 스푼", "https://cdn.lottielab.com/l/81ckDCvhP5kb4H.json"));
        animationRepository.save(new Animation("에스프레소 추출", "https://cdn.lottielab.com/l/7s4iHVxukkaC57.json"));
        animationRepository.save(new Animation("카라멜소스 스푼", "https://cdn.lottielab.com/l/9p7bDTV4ad7gPo.json"));
    }
}
