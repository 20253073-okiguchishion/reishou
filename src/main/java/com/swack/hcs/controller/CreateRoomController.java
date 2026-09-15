package com.swack.hcs.controller;

import com.swack.hcs.util.Loggable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 部屋作成コントローラ.
 * 本機能は管理者のみが利用可能です。
 */
@Controller
public class CreateRoomController implements Loggable {

  /**
   * 部屋作成画面表示.
   *
   * @return 部屋作成画面
   */
  @GetMapping("/createroom")
  public String get() {

    return "createroom";
  }

}
