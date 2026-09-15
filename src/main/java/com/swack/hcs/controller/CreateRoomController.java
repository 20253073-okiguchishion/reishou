package com.swack.hcs.controller;

import com.swack.hcs.bean.Room;
import com.swack.hcs.bean.UserData;
import com.swack.hcs.service.LoginService;
import com.swack.hcs.service.RoomService;
import com.swack.hcs.util.AppConstants;
import com.swack.hcs.util.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
  public String get() {
    if (!isAdminLogined()) {
      log().warn("[createroom:get]管理者権限が無い状態で部屋作成画面にアクセスされました。");
      return "redirect:/login";
    }

    return "createroom";
  }

  /**
   * 部屋作成処理.
   *
   * @param roomName ルーム名
   * @param privated 非公開ルームかどうか
   * @param model    モデル
   * @return 遷移先
   */
  @PostMapping("/createroom")
  public String post(@RequestParam(name = "roomName") String roomName,
      @RequestParam(name = "privated", required = false, defaultValue = "false") boolean privated,
      Model model) {
    log().info("[createroom:post]roomName:" + roomName);

    if (!isAdminLogined()) {
      log().warn("[createroom:post]管理者権限が無い状態で部屋作成が試行されました。");
      return "redirect:/login";
    }

    // 単項目チェック(空欄・文字数)
    if (roomName == null || roomName.isBlank() || roomName.length() > 50) {
      model.addAttribute("errorMsg", AppConstants.MSG_ERR_USERS_PARAM_MISTAKE);
      return "createroom";
    }

    // ルーム名の重複チェック
    if (roomService.isRegistered(roomName)) {
      model.addAttribute("errorMsg", AppConstants.MSG_ERR_ROOM_ISREGISTERED);
      return "createroom";
    }

    String userId = loginService.getLoginedUserId();

    Room createdRoom = roomService.createRoom(roomName, userId, privated);
    log().info("[createroom:post]roomId:" + createdRoom.roomId());

    return "redirect:/?roomId=" + createdRoom.roomId();
  }

  /**
   * ログイン中かつ管理者権限を持っているか判定する.
   *
   * @return ログイン中かつ管理者の場合はtrue
   */
  private boolean isAdminLogined() {
    if (!loginService.isLogin()) {
      return false;
    }

    UserData userData = loginService.getLoginedUserInfo();
    return userData != null && userData.isAdmin();
  }

}
