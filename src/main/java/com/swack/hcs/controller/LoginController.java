package com.swack.hcs.controller;

import com.swack.hcs.repository.ChatRepository;
import com.swack.hcs.service.LoginService;
import com.swack.hcs.service.UserService;
import com.swack.hcs.util.AppConstants;
import com.swack.hcs.util.Loggable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ログインコントローラ.
 */
@Controller
public class LoginController implements Loggable {

  private final ChatRepository chatRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private LoginService loginService;

  LoginController(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  /**
   * ログイン画面表示.
   *
   * @return 前回ログインしたユーザーの、ログイン状態保持になっていればチャット画面、されていなければログイン画面
   */
  @GetMapping("/login")
  public String get() {
    /*if (loginService.isLogin() && userService.loginStore(loginService.getLoginedUserId())) {
      return "redirect:/?roomId=R0000";
    }*/
    return "login";
  }

  /**
   * ログイン.
   *
   * @param mailAddress   メールアドレス
   * @param password      パスワード
   * @param isLoginStore 「ログイン状態保持」のチェックが入っていた場合はloginStore、そうでなければnull
   * @param model         モデル
   * @return チャット画面
   */
  @PostMapping("/login")
  public String login(@RequestParam(name = "mailAddress") String mailAddress,
      @RequestParam(name = "password") String password,
      @RequestParam (name = "isLoginStore", required = false) String isLoginStore, Model model) {
    log().info("[login:post]mailAddress:" + mailAddress);

    // ログイン処理
    boolean result = loginService.login(mailAddress, password);
    if (!result) {
      model.addAttribute("errorMsg", AppConstants.MSG_ERR_LOGIN_PARAM_MISTAKE);
      return "login";
    }

    // 「ログイン状態保持」のチェックが入っているか？
    if (isLoginStore != null) {
      userService.loginStore(loginService.getLoginedUserId());
    }

    return "redirect:/?roomId=R0000";
  }

  /**
   * ログアウト.
   *
   * @return ログイン画面
   */
  @GetMapping("/logout")
  public String logout() {

    String userId = loginService.getLoginedUserId();
    log().info("[logout:get]userId:" + userId);

    // ログインユーザ情報を破棄
    loginService.logout();

    return "redirect:/login";
  }
}
