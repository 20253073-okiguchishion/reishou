package com.swack.hcs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swack.hcs.bean.Room;
import com.swack.hcs.bean.UserData;
import com.swack.hcs.service.LoginService;
import com.swack.hcs.service.RoomService;


@Controller
/**
 * ダイレクトメッセージ参加コントローラ.
 */
public class JoinDirectController {

@Autowired
private LoginService loginService;

@Autowired
private RoomService roomService;

  /**
   * ダイレクトメッセージ参加画面表示.
   * @param model
   * @return
   */
  @GetMapping("/joindirect")
  public String get(Model model) {
    String userId = loginService.getLoginedUserId();
    List<UserData> directUser = roomService.getDirectUsers(userId);
    model.addAttribute("directUser", directUser);
    return "joindirect";
  }

  /**
   * ダイレクトメッセージ参加処理.
   * @param targetUserId
   * @param model
   * @return
   */
  @PostMapping("/joindirect")
  public String post(@RequestParam("targetUserId") String targetUserId, Model model) {
    String userId = loginService.getLoginedUserId();
    Room room = roomService.createDirectRoom(userId, targetUserId);
    return "redirect:/?roomId=" + room.roomId();
  }
}
