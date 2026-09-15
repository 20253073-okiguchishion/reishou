package com.swack.hcs.controller;

import com.swack.hcs.bean.ChatLog;
import com.swack.hcs.bean.Room;
import com.swack.hcs.bean.UserData;
import com.swack.hcs.service.ChatService;
import com.swack.hcs.service.LoginService;
// import com.swack.hcs.service.ChatServiceDummy;
import com.swack.hcs.util.Loggable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * メインコントローラ.
 */
@Controller
public class MainController implements Loggable {

  @Autowired
  private LoginService loginService;

  @Autowired
  private ChatService chatService;

  // Dummy
  // @Autowired
  // private ChatServiceDummy chatService;

  /**
   * メイン画面表示.
   *
   * @param roomId 部屋ID
   * @param model  モデル
   * @return メイン画面
   */
  @GetMapping("/")
  public String get(
      @RequestParam(name = "roomId", required = false) String roomId,
      Model model) {
    log().info("[/:get]roomid:" + roomId);

    UserData user = loginService.getLoginedUserInfo();

    if (roomId == null) {
      // 初期ルームをeveryoneにする
      roomId = "R0000";
    }

    Room room = chatService.getRoom(roomId, user.userId());
    List<Room> roomList = chatService.getRoomList(user.userId());
    List<Room> directList = chatService.getDirectList(user.userId());
    List<ChatLog> chatLogList = chatService.getChatlogList(user.userId(), roomId);

    model.addAttribute("user", user);
    model.addAttribute("room", room);
    model.addAttribute("roomList", roomList);
    model.addAttribute("directList", directList);
    model.addAttribute("chatLogList", chatLogList);
    return "main";
  }

  /**
   * チャット送信.
   *
   * @param roomId  部屋ID
   * @param message メッセージ
   * @return メイン画面
   */
  @PostMapping("/main")
  public String save(
      @RequestParam(name = "roomId") String roomId,
      @RequestParam(name = "message") String message) {
    log().info("[/:post]roomid:" + roomId + " message:" + message);

    chatService.saveChatLog(roomId, loginService.getLoginedUserId(), message);

    return "redirect:/?roomId=" + roomId;
  }

}
