package com.prolog.prologbackend.Member.Service.Other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Member.DTO.Request.KaKaoInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
    @Value("${kakao.id}")
    private String CLIENT_ID;
    @Value("${kakao.uri}")
    private String REDIRECT_URI;


    public String getKakaoToken(String code){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", CONTENT_TYPE);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);

        HttpEntity entity = new HttpEntity(body, headers);
        String requestUrl = "https://kauth.kakao.com/oauth/token";

        ResponseEntity<Map> response =
                restTemplate.exchange(requestUrl, HttpMethod.POST,entity, Map.class);

        return response.getBody().get("access_token").toString();
    }

    public KaKaoInfoDto getKakaoInfo(String kakaoToken){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", CONTENT_TYPE);
        headers.add("Authorization", kakaoToken);

        HttpEntity sec_entity = new HttpEntity(headers);
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        ResponseEntity<String> sec_response =
                restTemplate.exchange(requestUrl,HttpMethod.POST,sec_entity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        KaKaoInfoDto responseBody = null;
        try {
            responseBody = objectMapper.readValue(sec_response.getBody(), KaKaoInfoDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return responseBody;
    }
}
