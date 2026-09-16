package com.swack.hcs.controller;

import com.swack.hcs.bean.Room;
import com.swack.hcs.service.LoginService;
import com.swack.hcs.service.RoomService;
import com.swack.hcs.util.Loggable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 部屋参加コントローラ.
 */
@Controller
public class JoinRoomController implements Loggable {

  @Autowired
  private RoomService roomService;

  @Autowired
  private LoginService loginService;

  /**
   * 部屋参加画面表示.
   *
   * @param model モデル
   * @return 部屋参加画面
   */
  @GetMapping("/joinroom")
  public String get(Model model) {
    String userId = loginService.getLoginedUserId();
    List<Room> publicRooms = roomService.getPublicRooms(userId);
    model.addAttribute("publicRooms", publicRooms);

    return "joinroom";
  }

  /**
   * 部屋参加処理.
   *
   * @param roomId 参加するルームID
   * @param model  モデル
   * @return 遷移先
   */
  @PostMapping("/joinroom")
  public String post(@RequestParam("roomId") String roomId, Model model) {
    log().info("[joinroom:post]roomId:" + roomId);

    String userId = loginService.getLoginedUserId();

    Room room = roomService.getRoom(roomId);
    if (room == null) {
      model.addAttribute("errorMsg", "指定された部屋は存在しません。");
      return "joinroom";
    }

    if (roomService.isRoomJoined(roomId, userId)) {
      model.addAttribute("errorMsg", "既にこのルームに参加しています。");
      return "joinroom";
    }

    roomService.joinPublicRoom(roomId, userId);
    return "redirect:/?roomId=" + roomId;
  }

}
