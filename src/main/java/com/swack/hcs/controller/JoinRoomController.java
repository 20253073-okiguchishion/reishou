package com.swack.hcs.controller;

import com.swack.hcs.util.Loggable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 部屋参加コントローラ.
 */
@Controller
public class JoinRoomController implements Loggable {

  /**
   * 部屋参加画面表示.
   *
   * @param model モデル
   * @return 部屋参加画面
   */
  @GetMapping("/joinroom")
  public String get(Model model) {

    return "joinroom";
  }

}
