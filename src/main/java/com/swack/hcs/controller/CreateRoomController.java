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
import com.swack.hcs.util.AppConstants;
import com.swack.hcs.util.Loggable;


/**
 * 部屋作成コントローラ.
 * 本機能は管理者のみが利用可能です。
 */
@Controller
public class CreateRoomController implements Loggable {

  @Autowired
  private RoomService roomService;

  @Autowired
  private LoginService loginService;

  /**
   * 部屋作成画面表示.
   *
   * @return 部屋作成画面
   */
  @GetMapping("/createroom")
  public String get(Model model) {
    if (!isAdmin()) {
      // 管理者でない場合はアクセス拒否
      return "redirect:/login";
    }
    String userId = loginService.getLoginedUserId();
    List<UserData> users = roomService.getUsers(userId);
    model.addAttribute("users", users);
    return "createroom";
  }

  /**
   * 部屋作成処理.
   *
   * @param roomName 部屋名
   * @param privated 非公開フラグ
   * @param model   モデル
   * @return 部屋作成後のリダイレクト先
   */
  @PostMapping("/createroom")
  public String post(@RequestParam("roomName") String roomName,
      @RequestParam(name = "privated", required = false, defaultValue = "false") boolean privated,
      @RequestParam(name = "inviteUserIds", required = false) List<String> inviteUserIds, Model model) {
    if (roomName == null || roomName.trim().isEmpty() || roomName.length() > 50) {
      model.addAttribute("errorMessage", AppConstants.MSG_ERR_USERS_PARAM_MISTAKE);
      return "createroom";
    }
    if (roomService.isRoomNameExists(roomName)) {
      model.addAttribute("errorMessage", AppConstants.MSG_ERR_ROOM_ISREGISTERED);
      return "createroom";
    }
    String userId = loginService.getLoginedUserId();
    Room room = roomService.createRoom(roomName, privated, userId);
    if (inviteUserIds != null && !inviteUserIds.isEmpty()) {
      roomService.inviteUsers(room.roomId(), inviteUserIds);
    }
    return "redirect:/?roomId=" + room.roomId();
  }

  /**
   * 管理者かどうかを判定する.
   * @return 管理者の場合はtrue、それ以外はfalse
   */
  private boolean isAdmin() {
    UserData userData = loginService.getLoginedUserInfo();
    if (userData == null) {
      return false;
    }
    return userData.isAdmin();
  }

}
