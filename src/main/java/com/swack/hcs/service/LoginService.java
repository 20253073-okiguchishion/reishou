package com.swack.hcs.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swack.hcs.bean.UserData;
import com.swack.hcs.repository.UserRepository;

/**
 * ログイン管理の業務ロジッククラス.
 * ログイン関連の操作を提供します。
 *
 * @author 情報太郎
 */
@Transactional
@Service
public class LoginService {

  /** セッションに格納するユーザーデータのキー */
  private final String SESSION_USER_DATA_KEY = "userData";

  /** セッション情報 */
  @Autowired
  HttpSession session;

  @Autowired
  private UserRepository userRepository;

  /**
   * ログイン処理.
   *
   * @param mailAddress メールアドレス
   * @param password    パスワード
   * @return ログイン成功時はtrue、失敗時はfalse
   */
  public boolean login(String mailAddress, String password) {

    UserData userData = userRepository.login(mailAddress, password);
    if (userData == null) {
      // ユーザーデータが取得できなかった場合はログイン失敗
      return false;
    }

    // ログイン成功時はセッションにユーザーデータを格納
    setLoginedUserInfo(userData);

    return true;
  }

  /**
   * ログアウト処理.
   */
  public void logout() {
    // セッション情報を破棄
    session.invalidate();
  }

  /**
   * ログイン中ユーザーのユーザーIDを取得.
   *
   * @return ログイン中ユーザーのユーザーID
   */
  public String getLoginedUserId() {
    UserData userData = (UserData) session.getAttribute(SESSION_USER_DATA_KEY);

    if (userData == null) {
      return "Unknown User(セッション格納無し)";
    }

    return userData.userId();
  }

  /**
   * ログイン中ユーザーのユーザーデータを取得.
   *
   * @return ログイン中ユーザーのユーザーデータ(未ログインの場合はnull)
   */
  public UserData getLoginedUserInfo() {
    UserData userData = (UserData) session.getAttribute(SESSION_USER_DATA_KEY);
    return userData;
  }

  /**
   * ログイン中ユーザーのユーザーデータを設定.
   *
   * @param userData ユーザーデータ
   */
  public void setLoginedUserInfo(UserData userData) {
    // ログイン成功時はセッションにユーザーデータを格納
    session.setAttribute(SESSION_USER_DATA_KEY, userData);
  }

  /**
   * ログインチェック.
   *
   * @return ログイン中の場合はtrue、未ログインの場合はfalse
   */
  public boolean isLogin() {
    // ログインチェック
    UserData userData = (UserData) session.getAttribute(SESSION_USER_DATA_KEY);
    if (userData == null) {
      return false;
    }

    return true;
  }

}
