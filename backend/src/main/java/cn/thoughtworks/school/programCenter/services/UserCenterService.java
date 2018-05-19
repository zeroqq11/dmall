package cn.thoughtworks.school.programCenter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCenterService {
  private final static Integer TUTOR_ROLE = 2;

  @Value("${userCenter}")
  private String userCenterUrl;

  public ResponseEntity getUserInfo(Long userId) {
    String getUsersUrl = userCenterUrl + "/api/users/" + userId;

    RestTemplate template = new RestTemplate();
    ResponseEntity<Map> result = template.getForEntity(getUsersUrl, Map.class);
    return result;
  }

  public List getUsersByIds(String ids) {
    if (ids.equals("")) {
      return new ArrayList();
    }
    String getUsersUrl = userCenterUrl + "/api/users/ids/" + ids;
    RestTemplate template = new RestTemplate();
    ResponseEntity<List> result = template.getForEntity(getUsersUrl, List.class);
    return result.getBody();
  }

  public ResponseEntity getUserByNameOrEmail(String nameOrEmail) {
    RestTemplate template = new RestTemplate();
    String getUsersUrl = userCenterUrl + "/api/users";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder
        .fromUriString(getUsersUrl)
        .queryParam("nameOrEmail", nameOrEmail);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<List> result = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, List.class);
    return result;

  }

  public Map getUserByName(String name) {
    RestTemplate template = new RestTemplate();
    String getUsersUrl = userCenterUrl + "/api/users/username";
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    UriComponentsBuilder builder = UriComponentsBuilder
        .fromUriString(getUsersUrl)
        .queryParam("username", name);
    HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity result = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, Map.class);
    return (Map) result.getBody();
  }

  public void addTutorRole(Long tutorId) {
    String url = userCenterUrl + "/api/users/roles";
    RestTemplate template = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json; charset=UTF-8");
    Map data = new HashMap();
    data.put("userId", tutorId);
    data.put("role", TUTOR_ROLE);
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<Map<String, String>>(data, headers);
    template.exchange(url, HttpMethod.POST, requestEntity, Map.class);
  }
}
