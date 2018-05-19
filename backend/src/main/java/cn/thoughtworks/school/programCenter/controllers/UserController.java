package cn.thoughtworks.school.programCenter.controllers;

import cn.thoughtworks.school.programCenter.services.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    private UserCenterService userCenterService;

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public ResponseEntity getUserById(@PathVariable Long userId) {
        return userCenterService.getUserInfo(userId);
    }

  @RequestMapping(value = "/v2/users", method = RequestMethod.GET)
  public ResponseEntity getUsersByUsername(@RequestParam(value = "userName", defaultValue = "") String userName) {
    return userCenterService.getUserByNameOrEmail(userName);
  }

}
