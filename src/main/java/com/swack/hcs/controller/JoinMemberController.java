package com.swack.hcs.controller;

import com.swack.hcs.bean.UserData;
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
 * メンバー追加コントローラ.
 */
@Controller
public class JoinMemberController implements Loggable {

  @Autowired
  private RoomService roomService;
  /**
   * 部屋参加画面表示.
   * @param roomId
   * @param model
   * @return
   */
  @GetMapping("/joinmember")
  public String get(@RequestParam("roomId") String roomId, Model model) {
    model.addAttribute("roomId", roomId);
    List<UserData> joinableUsers = roomService.getJoinableUsers(roomId);
    model.addAttribute("joinableUsers", joinableUsers);
    return "joinmember";
  }

  /**
   * 部屋参加処理.
   * @param roomId
   * @param userIds
   * @param model
   * @return
   */
  @PostMapping("/joinmember")
  public String post(@RequestParam("roomId") String roomId,
      @RequestParam(name = "userIds", required = false) List<String> userIds,
      Model model) {
    if (userIds == null || userIds.isEmpty()) {
      model.addAttribute("errorMessage", "ユーザーが選択されていません。");
      model.addAttribute("roomId", roomId);
      model.addAttribute("joinableUsers", roomService.getJoinableUsers(roomId));
      return "joinmember";
    }
    roomService.inviteUsers(roomId, userIds);
    return "redirect:/?roomId=" + roomId;
  }

}
