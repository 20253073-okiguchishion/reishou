package com.swack.hcs.controller;

import com.swack.hcs.service.LoginService;
import com.swack.hcs.service.UserService;
import com.swack.hcs.util.AppConstants;
import com.swack.hcs.util.Loggable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ユーザ登録コントローラ.
 */
@Controller
public class SignupController implements Loggable {

  @Autowired
  private UserService userService;

  @Autowired
  private LoginService loginService;

  /**
   * 新規登録画面表示
   *
   * @return 新規登録画面
   */
  @GetMapping("/signup")
  public String get() {
    return "signup";
  }

  /**
   * 新規登録
   *
   * @param name          氏名
   * @param mailAddress   メールアドレス
   * @param password      パスワード
   * @param isLoginStore 「ログイン状態保持」のチェックが入っていた場合はlogin、そうでなければnull
   * @param model         モデル
   *
   * @return チェックが入っていた場合はチャット画面、入っていない場合はログイン画面、内容の不備や不具合等でユーザーを追加できない場合は新規登録画面
   */
  @PostMapping("/signup")
  public String signup(@RequestParam (name = "name") String name,
      @RequestParam (name = "mailAddress") String mailAddress,
      @RequestParam (name = "password") String password,
      @RequestParam (name = "isLoginStore", required = false) String isLoginStore ,Model model) {

    // 入力チェック
    boolean validate = isValidate(name, mailAddress, password);

    if (!validate) {
      model.addAttribute("errorMsg", AppConstants.MSG_ERR_USERS_PARAM_MISTAKE);
      return "signup";
    }

    // 登録済チェック
    boolean register = isRegistered(name, mailAddress);
    if (register) {
      model.addAttribute("errorMsg", AppConstants.MSG_ERR_USERS_ISREGISTERED);
      return "signup";
    }

    // 登録処理
    boolean result = userService.insert(name, mailAddress, password);
    if (!result) {
      model.addAttribute("errorMsg", "登録できませんでした。再度登録し直してください。");
      return "signup";
    } else {

      // 「ログイン状態保持」のチェックが入っているか？
      if (isLoginStore == null) {
        // 入っていない場合は、ログイン画面へ
        model.addAttribute("successMsg", AppConstants.MSG_INFO_USERS_ENTRY_SUCCESS);
        return "login";
      } else {
        // 入っている場合は、チャット画面へ
        signupToLogin(mailAddress, password);
        return "redirect:/?roomId=R0000";
      }

    }

  }

  /**
   * ユーザーの重複チェック
   *
   * @param name 氏名
   * @param mailAddress メールアドレス
   * @param model モデル
   *
   * @return すでに登録済のユーザーならtrue、そうでなければfalse
   */
  public boolean isRegistered(String name, String mailAddress) {
    boolean result = userService.check(name, mailAddress);

    return result;
  }

  /**
   * 入力チェック
   *
   * @param name 氏名
   * @param mailAddress メールアドレス
   * @param password パスワード
   *
   * @return 問題なければtrue、そうでなければfalse
   */
  public boolean isValidate(String name, String mailAddress, String password) {
    // 名前、メールアドレス、パスワードの空白、nullチェック
    if (name == null || name.isBlank()) {
      return false;
    }

    if (mailAddress == null || mailAddress.isBlank()) {
      return false;
    }

    if (password == null || password.isBlank()) {
      return false;
    }

    // メールアドレスの入力チェック・「@swack.com」で終わるかを確認
    if (!(mailAddress.endsWith("@swack.com"))) {
      return false;
    }

    // パスワードの入力チェック
    // 8文字以上であるか？
    if (password.length() < 8) {
      return false;
    }

    // パスワードは、大文字小文字数字が併用されているか？
    // 小文字の有無チェック
    // 小文字が1文字以上というパターンを生成
    Pattern p = Pattern.compile("\\p{Lower}+");

    // 入力されたパスワードと照合
    Matcher m = p.matcher(password);
    if (!(m.find())) {
      return false;
    }

    // 大文字の有無チェック
    // 大文字が1文字以上というパターンを生成
    Pattern p2 = Pattern.compile("\\p{Upper}+");

    // 入力されたパスワードと照合
    Matcher m2 = p2.matcher(password);
    if (!(m2.find())) {
      return false;
    }

    // 数字の有無チェック
    // 数字が1文字以上というパターンを生成
    Pattern p3 = Pattern.compile("\\d+");

    // 入力されたパスワードと照合
    Matcher m3 = p3.matcher(password);
    if (!(m3.find())) {
      return false;
    }

    return true;

  }

  /**
   * ユーザー作成時に、「ログイン情報保持」のチェックが入っていた場合、ここから直接ログイン
   *
   * @param mailAddress メールアドレス
   * @param password パスワード
   * @return チャット画面
   */
  public void signupToLogin(String mailAddress, String password) {
    log().info("[login:post]mailAddress:" + mailAddress);

    // ログイン処理
    loginService.login(mailAddress, password);

  }
}
